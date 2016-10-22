package easymvp.compiler.generator.decorator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

import easymvp.compiler.generator.DelegateClassGenerator;

import static easymvp.compiler.generator.AndroidLoaderUtils.getSupportLoader;
import static easymvp.compiler.generator.AndroidLoaderUtils.getSupportLoaderCallbacks;
import static easymvp.compiler.generator.AndroidLoaderUtils.getSupportLoaderManager;
import static easymvp.compiler.generator.AndroidLoaderUtils.getSupportPresenterLoader;
import static easymvp.compiler.util.ClassNames.APPCOMPAT_ACTIVITY_CLASS;
import static easymvp.compiler.util.ClassNames.CONTEXT;
import static easymvp.compiler.util.ClassNames.CONTEXT_WRAPPER;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class CustomViewDecorator extends BaseDecorator {

    public CustomViewDecorator(DelegateClassGenerator delegateClassGenerator) {
        super(delegateClassGenerator);
    }

    @Override
    protected void addMethods(TypeSpec.Builder result) {
        super.addMethods(result);
        result.addMethod(MethodSpec.methodBuilder("scanActivity")
                                 .addModifiers(Modifier.PRIVATE)
                                 .returns(APPCOMPAT_ACTIVITY_CLASS)
                                 .addParameter(CONTEXT, "context")
                                 .beginControlFlow("if(context instanceof $T)", APPCOMPAT_ACTIVITY_CLASS)
                                 .addStatement("return ($T)context", APPCOMPAT_ACTIVITY_CLASS)
                                 .endControlFlow()
                                 .beginControlFlow("else if(context instanceof $T)", CONTEXT_WRAPPER)
                                 .addStatement("return scanActivity((($T)context).getBaseContext())",
                                               CONTEXT_WRAPPER)
                                 .endControlFlow()
                                 .addStatement("return null")
                                 .build());
    }

    @Override
    public MethodSpec getLoaderManagerMethod(MethodSpec.Builder methodSignature) {
        return methodSignature.addStatement("return scanActivity(view.getContext()).getSupportLoaderManager()",
                                            APPCOMPAT_ACTIVITY_CLASS)
                .returns(getSupportLoaderManager())
                .build();
    }

    @Override
    public String createContextField(String viewField) {
        return "final $T context = " + viewField + ".getContext()";
    }

    @Override
    protected void implementInitializer(MethodSpec.Builder method) {

    }

    @Override
    protected ClassName getPresenterLoaderClass() {
        return getSupportPresenterLoader();
    }

    @Override
    protected ClassName getLoaderCallbacksClass() {
        return getSupportLoaderCallbacks();
    }

    @Override
    protected ClassName getLoaderClass() {
        return getSupportLoader();
    }

    @Override
    protected MethodSpec.Builder getOnLoaderResetMethod(ParameterizedTypeName loader) {
        return getCallbacksMethod(METHOD_ON_LOADER_RESET)
                .returns(TypeName.VOID)
                .addParameter(loader, "loader")
                .addStatement("delegate.get().detachView()")
                .addStatement("delegate.get().$L = null", FIELD_PRESENTER);
    }
}
