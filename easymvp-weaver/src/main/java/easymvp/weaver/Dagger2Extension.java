package easymvp.weaver;

import javax.inject.Inject;
import javax.inject.Provider;

import easymvp.annotation.Presenter;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LineNumberAttribute;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class Dagger2Extension {

    private final ClassPool pool;
    private final ViewDelegateBinder viewDelegateBinder;

    Dagger2Extension(ViewDelegateBinder viewDelegateBinder, ClassPool pool) {
        this.viewDelegateBinder = viewDelegateBinder;
        this.pool = pool;
    }

    boolean apply(CtClass candidateClass)
            throws CannotCompileException, NotFoundException {
        CtField presenterField = null;
        for (CtField ctField : candidateClass.getDeclaredFields()) {
            if (ctField.hasAnnotation(Inject.class) && ctField.hasAnnotation(Presenter.class)) {
                presenterField = ctField;
                break;
            }
        }
        if (presenterField == null) {
            return false;
        }
        CtClass membersInjectorClass = pool.get(
                candidateClass.getPackageName() + "." + candidateClass.getSimpleName() + "_MembersInjector");
        CtField presenterFieldInInjector = null;
        for (CtField field : membersInjectorClass.getDeclaredFields()) {
            if (field.getName().equals(presenterField.getName() + "Provider")) {
                presenterFieldInInjector = field;
                break;
            }
        }
        if (presenterFieldInInjector == null) {
            return false;
        }
        viewDelegateBinder.log("DaggerExtension", "MembersInjector class has been found for " +
                candidateClass.getSimpleName());
        addPresenterProviderField(candidateClass, presenterFieldInInjector);
        replacePresenterWithPresenterProvider(membersInjectorClass, presenterField.getName(),
                                              presenterFieldInInjector.getName());
        return true;
    }

    private void addPresenterProviderField(CtClass candidateClass, CtField presenterFieldInInjector)
            throws CannotCompileException {
        CtField presenterProvider =
                CtField.make(Provider.class.getName() + " $$presenterProvider = null;", candidateClass);
        presenterProvider.setGenericSignature(presenterFieldInInjector.getGenericSignature());
        candidateClass.addField(presenterProvider);
    }

    private void replacePresenterWithPresenterProvider(CtClass membersInjectorClass,
                                                       final String presenterFieldName,
                                                       final String presenterProviderField)
            throws NotFoundException, CannotCompileException {
        CtMethod injectMembers = membersInjectorClass.getDeclaredMethod("injectMembers");
        final int[] injectedPresenterLineNumber = {-1};
        injectMembers.instrument(new ExprEditor() {
            @Override
            public void edit(FieldAccess f) throws CannotCompileException {
                if (f.isWriter() && f.getFieldName().equals(presenterFieldName)) {
                    injectedPresenterLineNumber[0] = f.getLineNumber();
                }
            }
        });
        if (injectedPresenterLineNumber[0] != -1) {
            deleteLine(injectMembers, injectedPresenterLineNumber[0]);
            fillPresenterProviderInView(injectMembers, presenterProviderField, injectedPresenterLineNumber[0]);
            viewDelegateBinder.log("DaggerExtension",
                                   "Remove presenter injection from " +
                                           membersInjectorClass.getSimpleName() +
                                           " and replace with our presenterProvider ");
            viewDelegateBinder.writeClass(membersInjectorClass);
        }
    }

    private void deleteLine(CtMethod method, int lineNumberToReplace) {
        CodeAttribute codeAttribute = method.getMethodInfo().getCodeAttribute();
        LineNumberAttribute
                lineNumberAttribute = (LineNumberAttribute) codeAttribute.getAttribute(LineNumberAttribute.tag);
        int startPc = lineNumberAttribute.toStartPc(lineNumberToReplace);
        int endPc = lineNumberAttribute.toStartPc(lineNumberToReplace + 1);
        byte[] code = codeAttribute.getCode();
        for (int i = startPc; i < endPc; i++) {
            code[i] = CodeAttribute.NOP;
        }
    }

    private void fillPresenterProviderInView(CtMethod method, String fieldToUse, int lineNumber)
            throws CannotCompileException {
        method.insertAt(lineNumber, "$1.$$presenterProvider = " + fieldToUse + ";");
    }

}
