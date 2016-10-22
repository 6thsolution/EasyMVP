package easymvp.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

import static easymvp.gradle.plugin.Version.GROUP
import static easymvp.gradle.plugin.Version.VERSION

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

class EasyMVPRxPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.dependencies {
            compile "$GROUP:easymvp-rx-api:$VERSION"
        }
    }
}