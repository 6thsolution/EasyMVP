package easymvp.compiler.generator.decorator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import easymvp.compiler.generator.DelegateClassGenerator;
import java.util.concurrent.atomic.AtomicInteger;
import javax.lang.model.element.Modifier;

import static easymvp.compiler.util.ClassNames.BUNDLE;
import static easymvp.compiler.util.ClassNames.CONTEXT;
import static easymvp.compiler.util.ClassNames.PROVIDER;
import static easymvp.compiler.util.ClassNames.WEAK_REFERENCE;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public abstract class BaseDecorator {
    protected static final String FIELD_PRESENTER = "presenter";
    protected static final String METHOD_ON_LOADER_RESET = "onLoaderReset";
    private static final String METHOD_INITIALIZE = "initialize";
    private static final String METHOD_ATTACH_VIEW = "attachView";
    private static final String METHOD_DETACH_VIEW = "detachView";
    private static final String METHOD_DESTROY = "destroy";
    private static final String METHOD_GET_LOADER_MANAGER = "getLoaderManager";
    private static final String CLASS_PRESENTER_FACTORY = "PresenterFactory";
    private static final String CLASS_PRESENTER_LOADER_CALLBACKS = "PresenterLoaderCallbacks";
    private static final String METHOD_ON_CREATE_LOADER = "onCreateLoader";
    private static final String METHOD_ON_LOAD_FINISHED = "onLoadFinished";
    private static final String FIELD_PRESENTER_DELIVERED = "presenterDelivered";
    private static final AtomicInteger LOADER_ID = new AtomicInteger(500);

    protected DelegateClassGenerator delegateClassGenerator;

    public BaseDecorator(DelegateClassGenerator delegateClassGenerator) {
        this.delegateClassGenerator = delegateClassGenerator;
    }

    /**
     * {@code getLoaderManager(T view);}
     */
    public abstract MethodSpec getLoaderManagerMethod(MethodSpec.Builder methodSignature);

    public abstract String createContextField(String viewField);

    public void build(TypeSpec.Builder result) {
        addFields(result);
        addMethods(result);
        addInnerClasses(result);
    }

    protected void addFields(TypeSpec.Builder result) {
        ParameterizedTypeName loader = ParameterizedTypeName.get(getLoaderClass(),
                delegateClassGenerator.getPresenterClass());
        result.addField(
                FieldSpec.builder(delegateClassGenerator.getPresenterClass(), FIELD_PRESENTER,
                        Modifier.PRIVATE).build())
                .addField(FieldSpec.builder(loader, "loader", Modifier.PRIVATE).build())
                .addField(FieldSpec.builder(TypeName.INT, "loaderId", Modifier.PRIVATE).build());
    }

    protected void addMethods(TypeSpec.Builder result) {
        result.addMethod(getInitializeMethod())
                .addMethod(getInitializeMethodWithFactory())
                .addMethod(getAttachViewMethod())
                .addMethod(getDetachViewMethod())
                .addMethod(getDestroyMethod())
                .addMethod(getLoaderManagerMethod(getLoaderMethodSignature()));
    }

    protected void addInnerClasses(TypeSpec.Builder result) {
        if (!delegateClassGenerator.isInjectablePresenterInView()) {
            result.addType(getPresenterFactoryClass(result));
        }
        result.addType(getPresenterLoaderCallbacks());
    }

    private MethodSpec getInitializeMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder(METHOD_INITIALIZE)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(delegateClassGenerator.getViewClass(), "view")
                .returns(TypeName.VOID);

        if (delegateClassGenerator.isInjectablePresenterInView()) {
            method.addStatement("// Intentionally left blank!");
        } else {
            initLoader(method, "new " + CLASS_PRESENTER_FACTORY + "()");
            implementInitializer(method);
        }
        return method.build();
    }

    private MethodSpec getInitializeMethodWithFactory() {
        MethodSpec.Builder method = MethodSpec.methodBuilder(METHOD_INITIALIZE)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(delegateClassGenerator.getViewClass(), "view")
                .returns(TypeName.VOID);

        method.addParameter(ParameterSpec.builder(presenterFactoryTypeName(), "presenterFactory",
                Modifier.FINAL).build());
        if (!delegateClassGenerator.isInjectablePresenterInView()) {
            method.addStatement("// Intentionally left blank!");
        } else {
            initLoader(method, "presenterFactory");
            implementInitializer(method);
        }
        return method.build();
    }

    private void initLoader(MethodSpec.Builder method, String presenterProvider) {
        method.addStatement(createContextField("view"), CONTEXT);
        String predefinedPresenterId = delegateClassGenerator.getPresenterId();
        String presenterId;
        if (predefinedPresenterId != null && !predefinedPresenterId.isEmpty()) {
            presenterId = "view." + predefinedPresenterId;
        } else {
            presenterId = LOADER_ID.incrementAndGet() + "";
        }
        method.addStatement("loaderId = $L", presenterId);
        method.addStatement("loader = $L(view).initLoader($L,null,$L)", METHOD_GET_LOADER_MANAGER,
                presenterId,
                "new PresenterLoaderCallbacks(context, view, this, " + presenterProvider + ")");
    }

    protected abstract void implementInitializer(MethodSpec.Builder method);

    private MethodSpec getAttachViewMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder(METHOD_ATTACH_VIEW)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(delegateClassGenerator.getViewClass(), "view")
                .returns(TypeName.VOID);
        method.addStatement(callPresenterAttachView(FIELD_PRESENTER, "view", "$T"),
                ClassName.bestGuess(delegateClassGenerator.getPresenterViewQualifiedName()));
        return method.build();
    }

    private MethodSpec getDetachViewMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder(METHOD_DETACH_VIEW)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class);
        method.addStatement(callPresenterDetachView(FIELD_PRESENTER));
        return method.build();
    }

    private MethodSpec getDestroyMethod() {
        MethodSpec.Builder method = MethodSpec.methodBuilder(METHOD_DESTROY)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(delegateClassGenerator.getViewClass(), "view")
                .addAnnotation(Override.class);
        method.addStatement(addStatementInOnDestroyMethod());
        return method.build();
    }

    protected String addStatementInOnDestroyMethod() {
        return "// Intentionally left blank!";
    }

    private String callPresenterAttachView(String presenterVar, String viewVar,
            String presenterViewType) {
        return presenterVar + ".onViewAttached((" + presenterViewType + ")" + viewVar + ")";
    }

    private String callPresenterDetachView(String presenterVar) {
        return presenterVar + ".onViewDetached()";
    }

    private MethodSpec.Builder getLoaderMethodSignature() {
        return MethodSpec.methodBuilder(METHOD_GET_LOADER_MANAGER)
                .addModifiers(Modifier.PRIVATE)
                .addParameter(delegateClassGenerator.getViewClass(), "view");
    }

    private TypeSpec getPresenterFactoryClass(TypeSpec.Builder result) {
        //  result.addType(
        return TypeSpec.classBuilder(CLASS_PRESENTER_FACTORY)
                .addSuperinterface(presenterFactoryTypeName())
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .addMethod(MethodSpec.methodBuilder("get")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .addStatement("return new $T()", delegateClassGenerator.getPresenterClass())
                        .returns(delegateClassGenerator.getPresenterClass())
                        .build())
                .build();
    }

    private TypeName presenterFactoryTypeName() {
        return ParameterizedTypeName.get(PROVIDER, delegateClassGenerator.getPresenterClass());
    }

    private TypeSpec getPresenterLoaderCallbacks() {
        ParameterizedTypeName loader = ParameterizedTypeName.get(getLoaderClass(),
                delegateClassGenerator.getPresenterClass());
        TypeName contextWeakReference = ParameterizedTypeName.get(WEAK_REFERENCE, CONTEXT);
        TypeName viewWeakReference =
                ParameterizedTypeName.get(WEAK_REFERENCE, delegateClassGenerator.getViewClass());
        TypeName delegateWeakReference =
                ParameterizedTypeName.get(WEAK_REFERENCE, delegateClassGenerator.getClassName());

        TypeSpec.Builder result = TypeSpec.classBuilder(CLASS_PRESENTER_LOADER_CALLBACKS)
                .addSuperinterface(ParameterizedTypeName.get(getLoaderCallbacksClass(),
                        delegateClassGenerator.getPresenterClass()))
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .addField(FieldSpec.builder(boolean.class, FIELD_PRESENTER_DELIVERED,
                        Modifier.PRIVATE).initializer("false").build())
                .addField(FieldSpec.builder(contextWeakReference, "context", Modifier.PRIVATE)
                        .initializer("null")
                        .build())
                .addField(FieldSpec.builder(viewWeakReference, "view", Modifier.PRIVATE)
                        .initializer("null")
                        .build())
                .addField(FieldSpec.builder(delegateWeakReference, "delegate", Modifier.PRIVATE)
                        .initializer("null")
                        .build())
                .addField(
                        FieldSpec.builder(presenterFactoryTypeName(), "provider", Modifier.PRIVATE)
                                .initializer("null")
                                .build())
                //adding constructor
                .addMethod(MethodSpec.constructorBuilder()
                        .addParameter(CONTEXT, "context")
                        .addParameter(delegateClassGenerator.getViewClass(), "view")
                        .addParameter(delegateClassGenerator.getClassName(), "delegate")
                        .addParameter(presenterFactoryTypeName(), "provider")
                        .addStatement("this.context = new $T(context)", contextWeakReference)
                        .addStatement("this.view = new $T(view)", viewWeakReference)
                        .addStatement("this.delegate = new $T(delegate)", delegateWeakReference)
                        .addStatement("this.provider = provider")
                        .build())
                //create presenter loader
                .addMethod(getCallbacksMethod(METHOD_ON_CREATE_LOADER).returns(loader)
                        .addParameter(int.class, "id")
                        .addParameter(BUNDLE, "bundle")
                        .addStatement("return new $T($L,$L)", getPresenterLoaderClass(),
                                "context.get()", "provider")
                        .build());
        //implement onLoadFinished
        MethodSpec.Builder onLoadFinished =
                getCallbacksMethod(METHOD_ON_LOAD_FINISHED).returns(TypeName.VOID)
                        .addParameter(loader, "loader")
                        .addParameter(delegateClassGenerator.getPresenterClass(), "presenter")
                        .beginControlFlow("if (!$L)", FIELD_PRESENTER_DELIVERED)
                        .addStatement("delegate.get().$L = presenter", FIELD_PRESENTER);
        String presenterFieldInView = delegateClassGenerator.getPresenterFieldNameInView();
        if (presenterFieldInView != null && !presenterFieldInView.isEmpty()) {
            onLoadFinished.addStatement("view.get().$L = ($T) $L", presenterFieldInView,
                    delegateClassGenerator.getPresenterTypeInView(), FIELD_PRESENTER);
        }
        onLoadFinished.addStatement("$L = true", FIELD_PRESENTER_DELIVERED).endControlFlow();

        //implement onLoaderReset
        MethodSpec.Builder onLoaderReset = getOnLoaderResetMethod(loader);
        if (presenterFieldInView != null && !presenterFieldInView.isEmpty()) {
            onLoaderReset.beginControlFlow("if (view.get() != null)").
                    addStatement("view.get().$L = null", presenterFieldInView).endControlFlow();
        }
        result.addMethod(onLoadFinished.build());
        result.addMethod(onLoaderReset.build());

        return result.build();
    }

    protected abstract ClassName getPresenterLoaderClass();

    protected MethodSpec.Builder getCallbacksMethod(String methodName) {
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class);
    }

    protected abstract ClassName getLoaderCallbacksClass();

    protected abstract ClassName getLoaderClass();

    protected MethodSpec.Builder getOnLoaderResetMethod(ParameterizedTypeName loader) {
        return getCallbacksMethod(METHOD_ON_LOADER_RESET).returns(TypeName.VOID)
                .addParameter(loader, "loader")
                .beginControlFlow("if (delegate.get() != null)")
                .addStatement("delegate.get().$L = null", FIELD_PRESENTER)
                .endControlFlow();
    }
}
