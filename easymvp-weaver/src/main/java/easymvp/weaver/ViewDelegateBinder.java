/*
 * Copyright (C) 2016 6thSolution.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package easymvp.weaver;

import java.util.Arrays;
import java.util.Set;

import easymvp.annotation.ActivityView;
import easymvp.annotation.CustomView;
import easymvp.annotation.FragmentView;
import easymvp.annotation.conductor.ConductorController;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import weaver.common.WeaveEnvironment;
import weaver.instrumentation.injection.ClassInjector;
import weaver.processor.WeaverProcessor;

import static easymvp.weaver.JavassistUtils.ctClassToString;
import static easymvp.weaver.JavassistUtils.sameSignature;
import static easymvp.weaver.JavassistUtils.stringToCtClass;

/**
 * Bytecode weaver processor.
 * <p>
 * After generating <code>ViewDelegate</code> classes, this processor will run and do these things:
 * <ol>
 * <li>Finds all classes that annotated with {@link ActivityView} ,{@link FragmentView} and {@link CustomView}.</li>
 * <li>For each view, Adds a field correspond to his generated <code>ViewDelegate</code> class.</li>
 * <li>Invokes all methods from injected field in the right place of view lifecycle.</li>
 * </ol>
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class ViewDelegateBinder extends WeaverProcessor {

    private static final String FIELD_VIEW_DELEGATE = "$$viewDelegate";
    private static final String FIELD_PRESENTER_PROVIDER = "$$presenterProvider";

    private static final String BUNDLE_CLASS = "android.os.Bundle";
    private static final String LAYOUT_INFLATER_CLASS = "android.view.LayoutInflater";
    private static final String VIEW_GROUP_CLASS = "android.view.ViewGroup";
    private static final String VIEW_CLASS = "android.view.View";

    private static final String POINT_CUT = "org.aspectj.lang.JoinPoint";

    private static final String STATEMENT_CALL_INITIALIZE = "$s." + FIELD_VIEW_DELEGATE + ".initialize($s);";
    private static final String STATEMENT_CALL_INITIALIZE_WITH_FACTORY =
            "$s." + FIELD_VIEW_DELEGATE + ".initialize($s, $s." + FIELD_PRESENTER_PROVIDER + ");";
    private static final String STATEMENT_CALL_ATTACH = "$s." + FIELD_VIEW_DELEGATE + ".attachView($s);";
    private static final String STATEMENT_CALL_DETACH = "$s." + FIELD_VIEW_DELEGATE + ".detachView();";

    private static final String ASPECTJ_GEN_METHOD = "_aroundBody";
    private ClassPool pool;

    private Dagger2Extension dagger2Extension;

    @Override
    public synchronized void init(WeaveEnvironment env) {
        super.init(env);
        pool = env.getClassPool();
        dagger2Extension = new Dagger2Extension(this, pool);
    }

    @Override
    public void transform(Set<? extends CtClass> candidateClasses) throws Exception {
        log("Starting EasyMVP-Binder");
        for (CtClass ctClass : candidateClasses) {
            if (ctClass.hasAnnotation(ActivityView.class)) {
                log("Start weaving " + ctClass.getSimpleName());
                ClassInjector classInjector = instrumentation.startWeaving(ctClass);
                injectDelegateField(ctClass, classInjector);
                injectDelegateLifeCycleIntoActivity(ctClass, classInjector);
                writeClass(ctClass);
            } else if (ctClass.hasAnnotation(FragmentView.class)) {
                log("Start weaving " + ctClass.getSimpleName());
                ClassInjector classInjector = instrumentation.startWeaving(ctClass);
                injectDelegateField(ctClass, classInjector);
                injectDelegateLifeCycleIntoFragment(ctClass, classInjector);
                writeClass(ctClass);
            }
            else if(ctClass.hasAnnotation(CustomView.class)){
                log("Start weaving " + ctClass.getSimpleName());
                ClassInjector classInjector = instrumentation.startWeaving(ctClass);
                injectDelegateField(ctClass, classInjector);
                injectDelegateLifeCycleIntoCustomView(ctClass, classInjector);
                writeClass(ctClass);
            } else if (ctClass.hasAnnotation(ConductorController.class)) {
                log("Start weaving " + ctClass.getSimpleName());
                ClassInjector classInjector = instrumentation.startWeaving(ctClass);
                injectDelegateField(ctClass, classInjector);
                injectDelegateLifeCycleIntoConductorController(ctClass, classInjector);
                writeClass(ctClass);
            }
        }
    }

    @Override
    public String getName() {
        return "EasyMVP";
    }

    private void injectDelegateField(CtClass ctClass, ClassInjector classInjector)
            throws Exception {
        String viewClassName = ctClass.getName();
        String delegateClassName = viewClassName + "_ViewDelegate";
        insertDelegateField(classInjector, delegateClassName);
    }

    private void injectDelegateLifeCycleIntoActivity(CtClass ctClass, ClassInjector classInjector)
            throws Exception {
        CtMethod onCreate = findBestMethod(ctClass, "onCreate", BUNDLE_CLASS);
        CtMethod onStart = findBestMethod(ctClass, "onStart");
        CtMethod onStop = findBestMethod(ctClass, "onStop");
        boolean applied = dagger2Extension.apply(ctClass);
        AfterSuper(classInjector, onCreate,
                   applied ? STATEMENT_CALL_INITIALIZE_WITH_FACTORY : STATEMENT_CALL_INITIALIZE);
        AfterSuper(classInjector, onStart, STATEMENT_CALL_ATTACH);
        beforeSuper(classInjector, onStop, STATEMENT_CALL_DETACH);
    }

    private void injectDelegateLifeCycleIntoFragment(CtClass ctClass, ClassInjector classInjector)
            throws Exception {
        CtMethod onActivityCreated = findBestMethod(ctClass, "onActivityCreated", BUNDLE_CLASS);
        CtMethod onResume = findBestMethod(ctClass, "onResume");
        CtMethod onPause = findBestMethod(ctClass, "onPause");
        boolean applied = dagger2Extension.apply(ctClass);
        AfterSuper(classInjector, onActivityCreated,
                applied ? STATEMENT_CALL_INITIALIZE_WITH_FACTORY : STATEMENT_CALL_INITIALIZE);
        AfterSuper(classInjector, onResume, STATEMENT_CALL_ATTACH);
        beforeSuper(classInjector, onPause, STATEMENT_CALL_DETACH);
    }

    private void injectDelegateLifeCycleIntoCustomView(CtClass ctClass,ClassInjector classInjector)
            throws Exception {
        CtMethod onAttachedToWindow = findBestMethod(ctClass, "onAttachedToWindow");
        CtMethod onDetachedFromWindow = findBestMethod(ctClass, "onDetachedFromWindow");
        boolean applied = dagger2Extension.apply(ctClass);
        AfterSuper(classInjector, onAttachedToWindow, STATEMENT_CALL_ATTACH);

        AfterSuper(classInjector, onAttachedToWindow,
                   applied ? STATEMENT_CALL_INITIALIZE_WITH_FACTORY : STATEMENT_CALL_INITIALIZE);
//        atTheBeginning(classInjector, onDetachedFromWindow, STATEMENT_CALL_DETACH);

    }

    private void injectDelegateLifeCycleIntoConductorController(CtClass ctClass,
                                                                ClassInjector classInjector)
            throws Exception {
        CtMethod onCreateView =
                findBestMethod(ctClass, "onCreateView", LAYOUT_INFLATER_CLASS, VIEW_GROUP_CLASS);
        CtMethod onAttach = findBestMethod(ctClass, "onAttach", VIEW_CLASS);
        CtMethod onDetach = findBestMethod(ctClass, "onDetach", VIEW_CLASS);
        boolean applied = dagger2Extension.apply(ctClass);
        atTheBeginning(classInjector, onCreateView,
                applied ? STATEMENT_CALL_INITIALIZE_WITH_FACTORY : STATEMENT_CALL_INITIALIZE, true);
        AfterSuper(classInjector, onAttach, STATEMENT_CALL_ATTACH);
        beforeSuper(classInjector, onDetach, STATEMENT_CALL_DETACH);
    }

    /**
     * It is possible that aspectj already manipulated this method, so in this case we should inject
     * our code into {@code methodName_aroundBodyX()} which X is the lowest number of all similar
     * methods to {@code methodName_aroundBody}.
     */
    private CtMethod findBestMethod(CtClass ctClass, String methodName, String... params)
            throws NotFoundException {
        CtMethod baseMethod = null;
        try {
            baseMethod = ctClass.getDeclaredMethod(methodName, stringToCtClass(pool, params));
        } catch (NotFoundException e) {
            for (CtMethod ctMethod : ctClass.getMethods()) {
                if (ctMethod.getName().equals(methodName) &&
                        sameSignature(Arrays.asList(params), ctMethod)) {
                    baseMethod = ctMethod;
                    break;
                }
            }
        }
        CtMethod bestAspectJMethod = null;
        for (CtMethod candidate : ctClass.getDeclaredMethods()) {
            //aspectj is already manipulated this class
            if (isAnAspectJMethod(baseMethod, candidate)) {
                bestAspectJMethod = getLowerNumberOfAspectJMethods(bestAspectJMethod, candidate);
            }
        }

        CtMethod bestMethod = bestAspectJMethod != null ? bestAspectJMethod : baseMethod;
        if (bestMethod != null) {
            log("Best method for " + methodName + " is: [" + bestMethod.getName() + "]");
        }
        return bestMethod;
    }

    private boolean isAnAspectJMethod(CtMethod baseMethod, CtMethod aspectMethodCandidate)
            throws NotFoundException {
        if (aspectMethodCandidate.getName().contains(baseMethod.getName() + ASPECTJ_GEN_METHOD)) {
            //first and last parameter of _aroundBody are baseView and PointCut, so we will ignore them
            boolean areSame = false;
            CtClass[] baseMethodParams = baseMethod.getParameterTypes();
            CtClass[] aspectMethodParams = aspectMethodCandidate.getParameterTypes();
            if (baseMethodParams.length == 0 && aspectMethodParams.length == 2) {
                return true;
            }
            if (aspectMethodParams.length - baseMethodParams.length > 2) {
                return false;
            }
            for (int i = 1; i < aspectMethodParams.length - 1; i++) {
                areSame = baseMethodParams[i - 1].getName().equals(aspectMethodParams[i].getName());
            }
            return areSame;
        }
        return false;
    }

    private CtMethod getLowerNumberOfAspectJMethods(CtMethod best, CtMethod candidate) {
        if (best == null) {
            return candidate;
        }
        int bestNum = getAspectJMethodNumber(best.getName());
        int candidateNum = getAspectJMethodNumber(candidate.getName());
        return bestNum < candidateNum ? best : candidate;
    }

    private int getAspectJMethodNumber(String methodName) {
        String num = methodName.substring(
                methodName.indexOf(ASPECTJ_GEN_METHOD) + ASPECTJ_GEN_METHOD.length());
        return Integer.valueOf(num);
    }

    private void AfterSuper(ClassInjector classInjector, CtMethod method,
                            String statement) throws Exception {
        if (method.getName().contains(ASPECTJ_GEN_METHOD)) {
            statement = statement.replaceAll("\\$s", "\\ajc\\$this");
            String methodName =
                    method.getName().substring(0, method.getName().indexOf(ASPECTJ_GEN_METHOD));
            classInjector.insertMethod(method.getName(),
                                       ctClassToString(method.getParameterTypes()))
                    .ifExists()
                    .afterACallTo(methodName, statement).inject().inject();
        } else {
            statement = statement.replaceAll("\\$s", "this");
            classInjector.insertMethod(method.getName(),
                                       ctClassToString(method.getParameterTypes()))
                    .ifExistsButNotOverride()
                    .override("{" +
                                      "super." + method.getName() + "($$);" +
                                      statement +
                                      "}").inject()
                    .ifExists()
                    .afterSuper(statement).inject()
                    .inject();
        }
    }

    private void beforeSuper(ClassInjector classInjector, CtMethod method, String statement)
            throws Exception {
        if (method.getName().contains(ASPECTJ_GEN_METHOD)) {
            statement = statement.replaceAll("\\$s", "\\ajc\\$this");
            String methodName =
                    method.getName().substring(0, method.getName().indexOf(ASPECTJ_GEN_METHOD));
            classInjector.insertMethod(method.getName(),
                                       ctClassToString(method.getParameterTypes()))
                    .ifExists()
                    .beforeACallTo(methodName, statement).inject().inject();
        } else {
            statement = statement.replaceAll("\\$s", "this");
            classInjector.insertMethod(method.getName(),
                                       ctClassToString(method.getParameterTypes()))
                    .ifExistsButNotOverride()
                    .override("{" +
                                      statement +
                                      "super." + method.getName() + "($$);" +
                                      "}").inject()
                    .ifExists()
                    .beforeSuper(statement).inject()
                    .inject();
        }

    }

    private void atTheBeginning(ClassInjector classInjector, CtMethod method,
                                String statement, boolean returnSuperClass) throws Exception {
        if (method.getName().contains(ASPECTJ_GEN_METHOD)) {
            statement = statement.replaceAll("\\$s", "\\ajc\\$this");
            classInjector.insertMethod(method.getName(),
                    ctClassToString(method.getParameterTypes()))
                    .ifExists()
                    .atTheBeginning(statement).inject().inject();
        } else {
            statement = statement.replaceAll("\\$s", "this");
            String override;
            if (returnSuperClass) {
                //TODO refactor method
                override = "{" +
                        "android.view.View $$$supercall =  super." + method.getName() + "($$);" +
                        statement +
                        "return $$$supercall;"+
                        "}";
            } else {
                override = "{" +
                        statement +
                        "super." + method.getName() + "($$);" +
                        "}";
            }
            classInjector.insertMethod(method.getName(),
                    ctClassToString(method.getParameterTypes()))
                    .ifExistsButNotOverride()
                    .override(override).inject()
                    .ifExists()
                    .atTheBeginning(statement).inject()
                    .inject();
        }
    }

    private void insertDelegateField(ClassInjector classInjector, String delegateClassName)
            throws Exception {
        classInjector.insertField(delegateClassName, FIELD_VIEW_DELEGATE)
                .initializeIt()
                .inject();
    }

    void log(String message) {
        logger.info("[EasyMVP] -> " + message);
    }

    void log(String tag, String message) {
        logger.info("[EasyMVP] -> [" + tag + "] -> " + message);
    }
}
