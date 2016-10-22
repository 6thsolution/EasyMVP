package easymvp.compiler.generator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import javax.lang.model.element.Modifier;

import easymvp.Presenter;

import static easymvp.compiler.util.ClassNames.CONTEXT;
import static easymvp.compiler.util.ClassNames.PROVIDER;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public final class PresenterLoaderGenerator extends ClassGenerator {
    private static final String METHOD_GET_PRESENTER = "get";
    private static final TypeName PRESENTER_FACTORY_FIELD =
            ParameterizedTypeName.get(PROVIDER, TypeVariableName.get("P"));

    //All generated fields for our custom loader
    private static final String FIELD_PRESENTER = "presenter";
    private static final String FIELD_PRESENTER_FACTORY = "presenterFactory";
    //All methods that MUST be implemented
    private static final String METHOD_ON_START_LOADING = "onStartLoading";
    private static final String METHOD_ON_FORCE_LOAD = "onForceLoad";
    private static final String METHOD_ON_RESET = "onReset";

    private boolean supportLibrary;

    public PresenterLoaderGenerator(boolean supportLibrary) {
        super(AndroidLoaderUtils.getPresenterLoader(supportLibrary).packageName(),
              AndroidLoaderUtils.getPresenterLoader(supportLibrary).simpleName());
        this.supportLibrary = supportLibrary;
    }

    @Override
    public JavaFile build() {
        TypeSpec.Builder result =
                TypeSpec.classBuilder(getClassName()).addModifiers(Modifier.PUBLIC)
                        .addTypeVariable(TypeVariableName.get("P", Presenter.class))
                        .superclass(ParameterizedTypeName.get(AndroidLoaderUtils.getLoader(supportLibrary),
                                                              TypeVariableName.get("P")));
        addConstructor(result);
        addFields(result);
        addMethods(result);
        return JavaFile.builder(getPackageName(), result.build())
                .addFileComment("Generated class from EasyMVP. Do not modify!").build();
    }

    private void addConstructor(TypeSpec.Builder result) {
        result.addMethod(MethodSpec.constructorBuilder()
                                 .addParameter(CONTEXT, "context")
                                 .addParameter(
                                         ParameterSpec.builder(PRESENTER_FACTORY_FIELD, "presenterFactory")
                                                 .build())
                                 .addModifiers(Modifier.PUBLIC)
                                 .addStatement("super(context)")
                                 .addStatement("this.$L = $L", FIELD_PRESENTER_FACTORY, "presenterFactory")
                                 .build());
    }

    private void addFields(TypeSpec.Builder result) {
        result.addField(FieldSpec.builder(TypeVariableName.get("P"), FIELD_PRESENTER)
                                .addModifiers(Modifier.PRIVATE)
                                .build());
        result.addField(
                FieldSpec.builder(PRESENTER_FACTORY_FIELD, FIELD_PRESENTER_FACTORY)
                        .addModifiers(Modifier.PRIVATE)
                        .build());
    }

    private void addMethods(TypeSpec.Builder result) {
        result.addMethod(getOnStartLoadingMethod())
                .addMethod(getOnForceLoadMethod())
                .addMethod(getOnResetMethod());
    }

    private MethodSpec getOnStartLoadingMethod() {
        MethodSpec.Builder method = getDefaultMethod(METHOD_ON_START_LOADING);
        method.beginControlFlow("if ($L != null)", FIELD_PRESENTER)
                .addStatement("deliverResult($L)", FIELD_PRESENTER)
                .endControlFlow()
                .beginControlFlow("else")
                .addStatement("forceLoad()")
                .endControlFlow();
        return method.build();
    }

    private MethodSpec getOnForceLoadMethod() {
        MethodSpec.Builder method = getDefaultMethod(METHOD_ON_FORCE_LOAD);
        method.addStatement("$L = $L.$L()", FIELD_PRESENTER, FIELD_PRESENTER_FACTORY,
                            METHOD_GET_PRESENTER);
        method.addStatement("deliverResult($L)", FIELD_PRESENTER);
        return method.build();
    }

    private MethodSpec getOnResetMethod() {
        MethodSpec.Builder method = getDefaultMethod(METHOD_ON_RESET);
        method.beginControlFlow("if ($L != null)", FIELD_PRESENTER);
        method.addStatement(callPresenterPreDestroy(FIELD_PRESENTER));
        method.addStatement("$L = null", FIELD_PRESENTER);
        method.endControlFlow();
        return method.build();
    }

    private MethodSpec.Builder getDefaultMethod(String methodName) {
        return MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PROTECTED)
                .returns(void.class)
                .addAnnotation(Override.class);
    }

    private String callPresenterPreDestroy(String presenterVar) {
        return presenterVar + ".onDestroyed()";
    }

}
