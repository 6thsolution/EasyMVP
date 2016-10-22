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

package easymvp.compiler;

import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
final class Validator {

    static boolean isClass(Element element) {
        return element.getKind() == ElementKind.CLASS;
    }

    static boolean isAbstractClass(Element element) {
        return isClass(element) && getModifiers(element).contains(Modifier.ABSTRACT);
    }

    static boolean isNotAbstractClass(Element element) {
        return isClass(element) && !getModifiers(element).contains(Modifier.ABSTRACT);
    }

    static boolean isSubType(Element child, String parentCanonicalName,
                             ProcessingEnvironment procEnv) {
        return procEnv.getTypeUtils()
                .isSubtype(child.asType(), getTypeElement(procEnv, parentCanonicalName).asType());
    }

    static boolean isPrivate(Element element) {
        return getModifiers(element).contains(Modifier.PRIVATE);
    }

    static boolean isMethod(Element element) {
        return ElementKind.METHOD == element.getKind();
    }

    static TypeElement getTypeElement(ProcessingEnvironment procEnv, String canonicalName) {
        return procEnv.getElementUtils().getTypeElement(canonicalName);
    }

    static Set<Modifier> getModifiers(Element element) {
        return element.getModifiers();
    }

}
