package easymvp.compiler.generator.decorator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import easymvp.compiler.generator.DelegateClassGenerator;

import static easymvp.compiler.generator.AndroidLoaderUtils.getLoader;
import static easymvp.compiler.generator.AndroidLoaderUtils.getLoaderCallbacks;
import static easymvp.compiler.generator.AndroidLoaderUtils.getLoaderManager;
import static easymvp.compiler.generator.AndroidLoaderUtils.getPresenterLoader;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class ConductorControllerDecorator extends BaseDecorator {

    public ConductorControllerDecorator(DelegateClassGenerator delegateClassGenerator) {
        super(delegateClassGenerator);
    }

    @Override
    public MethodSpec getLoaderManagerMethod(MethodSpec.Builder methodSignature) {
        return methodSignature.addStatement("return view.getActivity().getLoaderManager()")
                .returns(getLoaderManager())
                .build();
    }

    @Override
    public String createContextField(String viewField) {
        return "final $T context = " + viewField + ".getActivity().getApplicationContext()";
    }

    @Override
    protected void implementInitializer(MethodSpec.Builder method) {

    }

    @Override
    protected String addStatementInOnDestroyMethod() {
        return "if (view.getActivity() == null) return;\n"
                + "getLoaderManager(view).destroyLoader(loaderId)";
    }

    @Override
    protected ClassName getPresenterLoaderClass() {
        return getPresenterLoader();
    }

    @Override
    protected ClassName getLoaderCallbacksClass() {
        return getLoaderCallbacks();
    }

    @Override
    protected ClassName getLoaderClass() {
        return getLoader();
    }
}
