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

package easymvp.internal;

/**
 * Internal usage!
 * <p>
 * It's a delegate between the Presenter and the View.
 * The annotation processor will use this interface to generate delegate classes.
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface ViewDelegate<V, P> {

    void initialize(V view, P presenterFactory);

    void initialize(V view);

    void attachView(V view);

    void detachView();

}
