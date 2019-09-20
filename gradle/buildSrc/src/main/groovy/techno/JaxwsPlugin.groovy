package techno

import org.gradle.api.Plugin
import org.gradle.api.Project

class JaxwsPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.configurations.create('technoJaxws') {
            visible = true
            transitive = true
            description = "The JAXB XJC libraries to be used for this project."
        }
    }
}
