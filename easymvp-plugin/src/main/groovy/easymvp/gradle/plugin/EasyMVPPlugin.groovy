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

package easymvp.gradle.plugin

import com.neenbedankt.gradle.androidapt.AndroidAptPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import weaver.plugin.WeaverPlugin

import static easymvp.gradle.plugin.Version.GROUP
import static easymvp.gradle.plugin.Version.VERSION

class EasyMVPPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def hasPlugin = { String name -> project.plugins.hasPlugin(name) }
        def hasConfiguration = { String name -> project.configurations.hasProperty(name) }
        def isLibrary = false
        if (hasPlugin("com.android.application") || hasPlugin("android") ||
                hasPlugin("com.android.test")) {
            isLibrary = false
        } else if (hasPlugin("com.android.library") || hasPlugin("android-library")) {
            isLibrary = true
        } else {
            throw new ProjectConfigurationException("The android/android-library plugin must be applied to this project", null)
        }

        boolean isKotlinProject = hasPlugin("kotlin-android")

        if (!hasConfiguration("annotationProcessor") && !hasPlugin("com.neenbedankt.android-apt") && !isKotlinProject) {
            project.plugins.apply(AndroidAptPlugin)
        }

        if (!hasPlugin('weaver')) {
            project.plugins.apply(WeaverPlugin)
        }

        project.dependencies {
            compile "$GROUP:easymvp-api:$VERSION"
            weaver "$GROUP:easymvp-weaver:$VERSION"
        }

        if (isKotlinProject) {
            project.dependencies.add("kapt", "$GROUP:easymvp-compiler:$VERSION")
        } else if (hasConfiguration("annotationProcessor") && !hasPlugin("com.neenbedankt.android-apt")) {
            project.dependencies.add("annotationProcessor", "$GROUP:easymvp-compiler:$VERSION")
        } else {
            project.dependencies.add("apt", "$GROUP:easymvp-compiler:$VERSION")
        }

        project.configurations.all {
            resolutionStrategy {
                force 'com.google.code.findbugs:jsr305:1.3.9', 'com.google.code.findbugs:jsr305:2.0.1'
            }
        }

        project.android {
            packagingOptions {
                exclude 'META-INF/LICENSE'
            }
        }
    }
}
