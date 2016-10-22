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

package easymvp.annotation;

import android.os.Bundle;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to inject the presenter that already defined in a {@link ActivityView#presenter()},
 * a {@link FragmentView#presenter()} or a {@link CustomView#presenter()}.
 * <p>
 * It must be applied into the view implementation classes (<code>Activity</code>, <code>Fragment</code> or <code>Custom View</code>),
 * otherwise it won't work.
 * <p>
 * You have access to the presenter instance after super call in {@link android.app.Activity#onCreate(Bundle)},
 * {@link android.app.Fragment#onActivityCreated(Bundle)} and {@link View#onAttachedToWindow} methods.
 * Also during configuration changes, same instance of presenter will be injected.
 * <p>
 * Here is an example:
 * <pre class="prettyprint">
 *  {@literal @}FragmentView(presenter = MyPresenter.class)
 *  public class MyFragment extends Fragment{
 *      {@literal @}Presenter
 *      MyPresenter presenter;
 *
 *  }
 *
 *  public class MyPresenter extends AbstractPresenter&lt;MyView&gt;{
 *
 *  public MyPresenter(){
 *
 *  }
 * }
 *
 * interface MyView{
 *
 *  }
 * </pre>
 * <p>
 *  By default you can't pass any objects to the constructor of presenters,
 *  but you can use <a href="https://google.github.io/dagger/">Dagger</a> to inject presenters.
 *  <p>
 *  Here is an example of using dagger:
 * <pre class="prettyprint">
 *  {@literal @}FragmentView(presenter = MyPresenter.class)
 *  public class MyFragment extends Fragment{
 *
 *      {@literal @}Inject
 *      {@literal @}Presenter
 *      MyPresenter presenter;
 *
 *      {@literal @}Override
 *      void onCreate(Bundle b){
 *          SomeComponent.injectTo(this);
 *      }
 *  }
 *
 *  public class MyPresenter extends AbstractPresenter&lt;MyView&gt;{
 *
 *  public MyPresenter(){
 *
 *  }
 * }
 * interface MyView{
 *
 *  }
 * </pre>
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@Retention(value = RUNTIME)
@Target(value = FIELD)
public @interface Presenter {
}
