package techno

import org.gradle.api.Plugin
import org.gradle.api.Project


class JaxbPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.configurations.create('technoJaxb') {
            visible = true
            transitive = true
            description = "The JAXB XJC libraries to be used for this project."
        }
    }
}
