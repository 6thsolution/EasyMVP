package easymvp.weaver;

import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
final class JavassistUtils {
    static CtClass[] stringToCtClass(ClassPool pool, String[] params)
            throws NotFoundException {
        CtClass[] ctClasses = new CtClass[params.length];
        for (int i = 0; i < params.length; i++) {
            ctClasses[i] = pool.get(params[i]);
        }
        return ctClasses;
    }

    static String[] ctClassToString(CtClass[] params) {
        String[] strings = new String[params.length];
        for (int i = 0; i < params.length; i++) {
            strings[i] = params[i].getName();
        }
        return strings;
    }

    static boolean sameSignature(List<String> parameters, CtMethod method)
            throws NotFoundException {
        CtClass[] methodParameters = method.getParameterTypes();
        if (methodParameters.length == 0 && parameters.size() == 0) return true;
        if (methodParameters.length != 0 && parameters.size() == 0) return false;
        if (methodParameters.length == 0 && parameters.size() != 0) return false;
        for (CtClass clazz : method.getParameterTypes()) {
            if (!parameters.contains(clazz.getName())) return false;
        }
        return true;
    }
}
