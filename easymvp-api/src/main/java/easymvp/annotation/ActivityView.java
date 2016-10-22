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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@code ActivityView} should be used on {@code android.support.v7.app.AppCompatActivity} classes
 * to enable usage of EasyMVP.
 * <p>
 *  Here is an example:
 *  <pre class="prettyprint">
 *  {@literal @}ActivityView(presenter = MyPresenter.class, layout = R.layout.my_activity)
 *  public class MyActivity extends AppCompatActivity implement MyView {
 *    //...
 *  }
 *  </pre>
 * @author Saeed Masoumi (saeed@6thsolution.com)
 * @see easymvp.Presenter
 */
@Retention(value = RUNTIME)
@Target(value = TYPE)
public @interface ActivityView {

    /**
     * @return the presenter class.
     */
    Class<? extends easymvp.Presenter> presenter();

    /**
     * The R.layout.* field which refer to the layout, so there is no need to call {@code
     * setContentView(R.layout.youLayout}.
     * <p>
     * By default content view will not be set.
     *
     * @return the id of the layout.
     */
    int layout() default -1;
}
