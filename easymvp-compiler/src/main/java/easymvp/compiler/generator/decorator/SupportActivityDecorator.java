package easymvp.compiler.generator.decorator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import easymvp.compiler.generator.DelegateClassGenerator;

import static easymvp.compiler.generator.AndroidLoaderUtils.getSupportLoader;
import static easymvp.compiler.generator.AndroidLoaderUtils.getSupportLoaderCallbacks;
import static easymvp.compiler.generator.AndroidLoaderUtils.getSupportLoaderManager;
import static easymvp.compiler.generator.AndroidLoaderUtils.getSupportPresenterLoader;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class SupportActivityDecorator extends ActivityDecorator {

    public SupportActivityDecorator(DelegateClassGenerator delegateClassGenerator) {
        super(delegateClassGenerator);
    }

    @Override
    public MethodSpec getLoaderManagerMethod(MethodSpec.Builder methodSignature) {
        return methodSignature.addStatement("return view.getSupportLoaderManager()")
                .returns(getSupportLoaderManager())
                .build();
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
}
