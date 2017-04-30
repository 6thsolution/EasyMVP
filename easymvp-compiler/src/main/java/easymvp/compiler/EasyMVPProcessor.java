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

package easymvp.compiler;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import easymvp.annotation.ActivityView;
import easymvp.annotation.CustomView;
import easymvp.annotation.FragmentView;
import easymvp.annotation.Presenter;
import easymvp.annotation.conductor.ConductorController;
import easymvp.compiler.generator.ClassGenerator;
import easymvp.compiler.generator.DelegateClassGenerator;
import easymvp.compiler.generator.PresenterLoaderGenerator;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@AutoService(Processor.class)
public class EasyMVPProcessor extends AbstractProcessor {

    private static final String ANDROID_ACTIVITY_CLASS_NAME = "android.app.Activity";
    private static final String ANDROID_SUPPORT_ACTIVITY_CLASS_NAME =
            "android.support.v7.app.AppCompatActivity";
    private static final String ANDROID_FRAGMENT_CLASS_NAME = "android.app.Fragment";
    private static final String ANDROID_SUPPORT_FRAGMENT_CLASS_NAME =
            "android.support.v4.app.Fragment";
    private static final String ANDROID_CUSTOM_VIEW_CLASS_NAME = "android.view.View";
    private static final String CONDUCTOR_CONTROLLER_CLASS_NAME =
            "com.bluelinelabs.conductor.Controller";
    private static final String DELEGATE_CLASS_SUFFIX = "_ViewDelegate";

    private Messager messager;
    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;

    /** A flag that allow processor to generate presenter loaders only once to avoid IO exception */
    private boolean isLoadersCopied = false;
    private boolean isSupportLoadersCopied = false;
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(ActivityView.class.getCanonicalName());
        types.add(FragmentView.class.getCanonicalName());
        types.add(CustomView.class.getCanonicalName());
        types.add(Presenter.class.getCanonicalName());
        types.add(ConductorController.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, DelegateClassGenerator> delegates = makeDelegates(roundEnv);
        for (Map.Entry<TypeElement, DelegateClassGenerator> entry : delegates.entrySet()) {
            write(entry.getValue());
        }
        generatePresenterLoaders();
        return true;
    }

    private void generatePresenterLoaders() {
        if (!isLoadersCopied) {
            PresenterLoaderGenerator
                    androidPresenterLoader = new PresenterLoaderGenerator(false);
            write(androidPresenterLoader);
            isLoadersCopied = true;
        }
        if (!isSupportLoadersCopied) {
            PresenterLoaderGenerator supportLibraryPresenterLoader =
                    new PresenterLoaderGenerator(true);
            write(supportLibraryPresenterLoader);
            isSupportLoadersCopied = true;
        }
    }

    private void write(ClassGenerator classGenerator) {
        try {
            classGenerator.build().writeTo(filer);
        } catch (Exception e) {
            error("Unable to write ( " + e.getMessage() + " )");
            //TODO for presenter loader classes it throws "attempt to recreate" exception.
        }
    }

    private Map<TypeElement, DelegateClassGenerator> makeDelegates(
            RoundEnvironment roundEnv) {
        //Key is view class as TypeElement
        Map<TypeElement, DelegateClassGenerator> delegateClassMap =
                new LinkedHashMap<>();

        for (Element element : roundEnv.getElementsAnnotatedWith(ActivityView.class)) {
            parseActivityView(element, delegateClassMap);
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(FragmentView.class)) {
            parseFragmentView(element, delegateClassMap);
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(CustomView.class)) {
            parseCustomView(element, delegateClassMap);
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(ConductorController.class)) {
            parseConductorController(element, delegateClassMap);
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(Presenter.class)) {
            parsePresenterInjection(element, delegateClassMap);
        }
        return delegateClassMap;
    }

    private void parseActivityView(Element element,
                                   Map<TypeElement, DelegateClassGenerator> delegateClassMap) {
        //TODO print errors
        if (!SuperficialValidation.validateElement(element)) {
            error("Superficial validation error for %s", element.getSimpleName());
            return;
        }
        if (!Validator.isNotAbstractClass(element)) {
            error("%s is abstract", element.getSimpleName());
            return;
        }
        boolean isActivity =
                Validator.isSubType(element, ANDROID_ACTIVITY_CLASS_NAME, processingEnv);
        boolean isSupportActivity =
                Validator.isSubType(element, ANDROID_SUPPORT_ACTIVITY_CLASS_NAME, processingEnv);
        if (!isActivity && !isSupportActivity) {
            error("%s must extend Activity or AppCompatActivity", element.getSimpleName());
            return;
        }
        //getEnclosing for class type will returns its package/
        TypeElement enclosingElement = (TypeElement) element;
        DelegateClassGenerator delegateClassGenerator =
                getDelegate(enclosingElement, delegateClassMap);
        ActivityView annotation = element.getAnnotation(ActivityView.class);
        delegateClassGenerator.setResourceID(annotation.layout());
        if (isSupportActivity) {
            delegateClassGenerator.setViewType(ViewType.SUPPORT_ACTIVITY);
        } else {
            delegateClassGenerator.setViewType(ViewType.ACTIVITY);
        }
        try {
            annotation.presenter();
        } catch (MirroredTypeException mte) {
            parsePresenter(delegateClassGenerator, mte);
        }
    }

    private void parseFragmentView(Element element,
                                   Map<TypeElement, DelegateClassGenerator> delegateClassMap) {
        //TODO print errors
        if (!SuperficialValidation.validateElement(element)) {
            error("Superficial validation error for %s", element.getSimpleName());
            return;
        }
        if (!Validator.isNotAbstractClass(element)) {
            error("%s is abstract", element.getSimpleName());
            return;
        }
        boolean isFragment =
                Validator.isSubType(element, ANDROID_FRAGMENT_CLASS_NAME, processingEnv);
        boolean isSupportFragment =
                Validator.isSubType(element, ANDROID_SUPPORT_FRAGMENT_CLASS_NAME, processingEnv);
        if (!isFragment && !isSupportFragment) {
            error("%s must extend Fragment or support Fragment", element.getSimpleName());
            return;
        }
        //getEnclosing for class type will returns its package/
        TypeElement enclosingElement = (TypeElement) element;
        DelegateClassGenerator delegateClassGenerator =
                getDelegate(enclosingElement, delegateClassMap);
        if (isFragment) {
            delegateClassGenerator.setViewType(ViewType.FRAGMENT);
        } else {
            delegateClassGenerator.setViewType(ViewType.SUPPORT_FRAGMENT);
        }
        FragmentView annotation = element.getAnnotation(FragmentView.class);
        try {
            annotation.presenter();
        } catch (MirroredTypeException mte) {
            parsePresenter(delegateClassGenerator, mte);
        }
    }

    private void parseCustomView(Element element,
                                 Map<TypeElement, DelegateClassGenerator> delegateClassMap) {
        //TODO print errors
        if (!SuperficialValidation.validateElement(element)) {
            error("Superficial validation error for %s", element.getSimpleName());
            return;
        }
        if (!Validator.isNotAbstractClass(element)) {
            error("%s is abstract", element.getSimpleName());
            return;
        }
        if (!Validator.isSubType(element, ANDROID_CUSTOM_VIEW_CLASS_NAME, processingEnv)) {
            error("%s must extend View", element.getSimpleName());
            return;
        }

        //getEnclosing for class type will returns its package/
        TypeElement enclosingElement = (TypeElement) element;
        DelegateClassGenerator delegateClassGenerator =
                getDelegate(enclosingElement, delegateClassMap);
        delegateClassGenerator.setViewType(ViewType.CUSTOM_VIEW);

        CustomView annotation = element.getAnnotation(CustomView.class);
        try {
            annotation.presenter();
        } catch (MirroredTypeException mte) {
            parsePresenter(delegateClassGenerator, mte);
        }
    }

    private void parseConductorController(Element element,
                                          Map<TypeElement, DelegateClassGenerator> delegateClassMap) {
        if (!SuperficialValidation.validateElement(element)) {
            error("Superficial validation error for %s", element.getSimpleName());
            return;
        }
        if (!Validator.isNotAbstractClass(element)) {
            error("%s is abstract", element.getSimpleName());
            return;
        }
        if (!Validator.isSubType(element, CONDUCTOR_CONTROLLER_CLASS_NAME, processingEnv)) {
            error("%s must extend View", element.getSimpleName());
            return;
        }
        //getEnclosing for class type will returns its package/
        TypeElement enclosingElement = (TypeElement) element;
        DelegateClassGenerator delegateClassGenerator =
                getDelegate(enclosingElement, delegateClassMap);
        delegateClassGenerator.setViewType(ViewType.CONDUCTOR_CONTROLLER);
        ConductorController annotation = element.getAnnotation(ConductorController.class);
        try {
            annotation.presenter();
        } catch (MirroredTypeException mte) {
            parsePresenter(delegateClassGenerator, mte);
        }
    }

    private void parsePresenterInjection(Element element,
                                         Map<TypeElement, DelegateClassGenerator> delegateClassMap) {
        //TODO print errors
        if (!SuperficialValidation.validateElement(element)) {
            error("Superficial validation error for %s", element.getSimpleName());
            return;
        }
        if (Validator.isPrivate(element)) {
            error("%s can't be private", element.getSimpleName());
            return;
        }
        VariableElement variableElement = (VariableElement) element;
        DelegateClassGenerator delegateClassGenerator =
                getDelegate((TypeElement) element.getEnclosingElement(),
                            delegateClassMap);
        delegateClassGenerator.setViewPresenterField(variableElement.getSimpleName().toString());
        if (variableElement.getAnnotation(Inject.class) != null) {
            delegateClassGenerator.injectablePresenterInView(true);
        }
        delegateClassGenerator.setPresenterTypeInView(variableElement.asType().toString());
    }

    private void parsePresenter(DelegateClassGenerator delegateClassGenerator,
                                MirroredTypeException mte) {
        TypeElement presenterElement = getTypeElement(mte);
        delegateClassGenerator.setPresenter(getClassName(presenterElement));
        String presenterView = findViewTypeOfPresenter(presenterElement);
        delegateClassGenerator.setPresenterViewQualifiedName(presenterView);
    }

    private String findViewTypeOfPresenter(TypeElement presenterElement) {
        TypeElement currentClass = presenterElement;
        while (currentClass != null) {
            if (currentClass.getSuperclass() instanceof DeclaredType) {
                List<? extends TypeMirror> superClassParameters =
                        ((DeclaredType) currentClass.getSuperclass()).getTypeArguments();

                if (superClassParameters.size() == 1) {
                    String type = superClassParameters.get(0).toString();
                    if (!"V".equals(type)) return type;
                }
            }
            currentClass = getSuperClass(currentClass);
        }
        return "";
    }

    private TypeElement getSuperClass(TypeElement typeElement) {
        if (!(typeElement.getSuperclass() instanceof DeclaredType)) return null;
        DeclaredType declaredAncestor = (DeclaredType) typeElement.getSuperclass();
        return (TypeElement) declaredAncestor.asElement();
    }

    private DelegateClassGenerator getDelegate(TypeElement enclosingElement,
                                               Map<TypeElement, DelegateClassGenerator> delegateClassMap) {
        DelegateClassGenerator
                delegateClassGenerator = delegateClassMap.get(enclosingElement);
        if (delegateClassGenerator == null) {
            ClassName viewClass = getClassName(enclosingElement);
            String packageName = getPackageName(enclosingElement);
            String delegateClassName = getSimpleClassName(enclosingElement) + DELEGATE_CLASS_SUFFIX;
            delegateClassGenerator =
                    new DelegateClassGenerator(packageName, delegateClassName, viewClass);
            delegateClassMap.put(enclosingElement, delegateClassGenerator);
        }
        return delegateClassGenerator;
    }

    private String getSimpleClassName(TypeElement type) {
        return type.getSimpleName().toString();
    }

    private ClassName getClassName(TypeElement typeElement) {
        return ClassName.bestGuess(typeElement.getQualifiedName().toString());
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private TypeElement getTypeElement(String canonicalName) {
        return elementUtils.getTypeElement(canonicalName);
    }

    private TypeElement getTypeElement(MirroredTypeException mte) {
        DeclaredType declaredType = (DeclaredType) mte.getTypeMirror();
        return (TypeElement) declaredType.asElement();
    }

    /**
     * {@link com.squareup.javapoet.ClassName#canonicalName} is not public.
     */
    private String classNameToCanonicalName(ClassName className) {
        List<String> names = new ArrayList<>();
        names.add(className.packageName());
        names.addAll(className.simpleNames());
        return names.get(0).isEmpty()
                ? join(".", names.subList(1, names.size()))
                : join(".", names);
    }

    private String join(String separator, List<String> parts) {
        if (parts.isEmpty()) return "";
        StringBuilder result = new StringBuilder();
        result.append(parts.get(0));
        for (int i = 1; i < parts.size(); i++) {
            result.append(separator).append(parts.get(i));
        }
        return result.toString();
    }

    private void error(String message, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(message, args));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


}
