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

package easymvp.compiler.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

import easymvp.compiler.ViewType;
import easymvp.compiler.generator.decorator.ActivityDecorator;
import easymvp.compiler.generator.decorator.BaseDecorator;
import easymvp.compiler.generator.decorator.ConductorControllerDecorator;
import easymvp.compiler.generator.decorator.CustomViewDecorator;
import easymvp.compiler.generator.decorator.FragmentDecorator;
import easymvp.compiler.generator.decorator.SupportActivityDecorator;
import easymvp.compiler.generator.decorator.SupportFragmentDecorator;

import static easymvp.compiler.util.ClassNames.PROVIDER;
import static easymvp.compiler.util.ClassNames.VIEW_DELEGATE;

/**
 * This class is responsible for generating .java files, which implement {@link
 * easymvp.internal.ViewDelegate}.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class DelegateClassGenerator extends ClassGenerator {
    private final ClassName viewClass;
    private ClassName presenterClass;
    private ViewType viewType;
    private int resourceID = -1;
    private String presenterFieldNameInView = "";
    private String presenterViewQualifiedName;
    private boolean injectablePresenterInView = false;
    private ClassName presenterTypeInView;
    private BaseDecorator decorator;

    public DelegateClassGenerator(String packageName, String className, ClassName viewClass) {
        super(packageName, className);
        this.viewClass = viewClass;
    }

    public ClassName getViewClass() {
        return viewClass;
    }

    public String getPresenterViewQualifiedName() {
        return presenterViewQualifiedName;
    }

    public void setPresenterViewQualifiedName(String presenterViewQualifiedName) {
        this.presenterViewQualifiedName = presenterViewQualifiedName;
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
        switch (viewType) {
            case ACTIVITY:
                decorator = new ActivityDecorator(this);
                break;
            case SUPPORT_ACTIVITY:
                decorator = new SupportActivityDecorator(this);
                break;
            case FRAGMENT:
                decorator = new FragmentDecorator(this);
                break;
            case SUPPORT_FRAGMENT:
                decorator = new SupportFragmentDecorator(this);
                break;
            case CUSTOM_VIEW:
                decorator = new CustomViewDecorator(this);
                break;
            case CONDUCTOR_CONTROLLER:
                decorator = new ConductorControllerDecorator(this);
                break;
        }
    }

    public void setResourceID(int resourceID) {
        this.resourceID = resourceID;
    }

    public void setPresenter(ClassName presenter) {
        this.presenterClass = presenter;
    }

    public void setViewPresenterField(String fieldName) {
        presenterFieldNameInView = fieldName;
    }

    @Override
    public JavaFile build() {
        TypeSpec.Builder result =
                TypeSpec.classBuilder(getClassName()).addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(
                                ParameterizedTypeName.get(VIEW_DELEGATE, viewClass,
                                                          getPresenterFactoryTypeName()));
        decorator.build(result);
        return JavaFile.builder(getPackageName(), result.build())
                .addFileComment("Generated class from EasyMVP. Do not modify!").build();
    }


    public void injectablePresenterInView(boolean injectable) {
        this.injectablePresenterInView = injectable;
    }

    private TypeName getPresenterFactoryTypeName() {
        return ParameterizedTypeName.get(PROVIDER, presenterClass);
    }

    public ClassName getPresenterClass() {
        return presenterClass;
    }

    public boolean isInjectablePresenterInView() {
        return injectablePresenterInView;
    }

    public ClassName getPresenterTypeInView() {
        return presenterTypeInView;
    }

    public void setPresenterTypeInView(String presenterTypeInView) {
        this.presenterTypeInView = ClassName.bestGuess(presenterTypeInView);
    }

    public String getPresenterFieldNameInView() {
        return presenterFieldNameInView;
    }

    public int getResourceID() {
        return resourceID;
    }
}
