package easymvp.compiler.util;

import com.squareup.javapoet.ClassName;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public final class ClassNames {
    public static final ClassName CONTEXT = ClassName.get("android.content", "Context");
    public static final ClassName BUNDLE = ClassName.get("android.os", "Bundle");
    public static final ClassName WEAK_REFERENCE = ClassName.get("java.lang.ref", "WeakReference");
    public static final ClassName PROVIDER = ClassName.get("javax.inject", "Provider");
    public static final ClassName APPCOMPAT_ACTIVITY_CLASS =
            ClassName.get("android.support.v7.app", "AppCompatActivity");
    public static final ClassName CONTEXT_WRAPPER =
            ClassName.get("android.content", "ContextWrapper");
    public static final ClassName VIEW_DELEGATE =
            ClassName.get("easymvp.internal", "ViewDelegate");
    //loaders related class names
    public static final ClassName LOADER = ClassName.get("android.content", "Loader");
    public static final ClassName SUPPORT_LOADER =
            ClassName.get("android.support.v4.content", "Loader");
    public static final ClassName LOADER_MANAGER = ClassName.get("android.app", "LoaderManager");
    public static final ClassName SUPPORT_LOADER_MANAGER =
            ClassName.get("android.support.v4.app", "LoaderManager");

    public static final ClassName LOADER_CALLBACKS =
            ClassName.get("android.app.LoaderManager", "LoaderCallbacks");

    public static final ClassName SUPPORT_LOADER_CALLBACKS =
            ClassName.get("android.support.v4.app.LoaderManager", "LoaderCallbacks");

    public static final ClassName PRESENTER_LOADER =
            ClassName.get("easymvp.loader", "PresenterLoader");
    public static final ClassName SUPPORT_PRESENTER_LOADER =
            ClassName.get("easymvp.loader", "SupportPresenterLoader");

}
