package easymvp.compiler.generator;

import com.squareup.javapoet.ClassName;

import easymvp.compiler.ViewType;

import static easymvp.compiler.util.ClassNames.*;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class AndroidLoaderUtils {

    static ClassName getLoader(boolean supportLibrary) {
        return get(supportLibrary, SUPPORT_LOADER, LOADER);
    }

    static ClassName getLoader(ViewType viewType) {
        return get(viewType, SUPPORT_LOADER, LOADER);
    }

    public static ClassName getLoader() {
        return LOADER;
    }

    public static ClassName getSupportLoader() {
        return SUPPORT_LOADER;
    }

    public static ClassName getLoaderManager() {
        return LOADER_MANAGER;
    }

    public static ClassName getSupportLoaderManager() {
        return SUPPORT_LOADER_MANAGER;
    }

    public static ClassName getLoaderCallbacks() {
        return LOADER_CALLBACKS;
    }

    public static ClassName getSupportLoaderCallbacks() {
        return SUPPORT_LOADER_CALLBACKS;
    }

    public static ClassName getPresenterLoader() {
        return PRESENTER_LOADER;
    }

    public static ClassName getSupportPresenterLoader() {
        return SUPPORT_PRESENTER_LOADER;
    }

    static ClassName getLoaderCallbacks(ViewType viewType) {
        return get(viewType, SUPPORT_LOADER_CALLBACKS, LOADER_CALLBACKS);
    }

    static ClassName getPresenterLoader(ViewType viewType) {
        return get(viewType, SUPPORT_PRESENTER_LOADER, PRESENTER_LOADER);
    }

    static ClassName getPresenterLoader(boolean supportLibrary) {
        return get(supportLibrary, SUPPORT_PRESENTER_LOADER, PRESENTER_LOADER);
    }

    private static ClassName get(ViewType viewType, ClassName supportVersion,
                                 ClassName defaultVersion) {
        switch (viewType) {
            case ACTIVITY:
            case SUPPORT_FRAGMENT:
                return supportVersion;
            case FRAGMENT:
                return defaultVersion;
            default:
                return defaultVersion;
        }
    }

    private static ClassName get(boolean isSupportLibrary, ClassName supportVersion,
                                 ClassName defaultVersion) {
        if (isSupportLibrary) {
            return supportVersion;
        } else {
            return defaultVersion;
        }
    }
}
