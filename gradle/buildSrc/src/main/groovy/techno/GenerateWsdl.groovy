package techno

import org.gradle.api.DefaultTask
import org.gradle.api.AntBuilder
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/*
 * Generate the WSDL from the service implementation guided by the web annotations
 */
public class GenerateWsdl extends DefaultTask {
    @Input
    def String seiClassName
    
    // Provide this variable so it can be overridden when needed by user of task
     @InputFiles
    def FileCollection seiClassPath = project.sourceSets.main.runtimeClasspath
    
    @OutputDirectory
    def File wsdlOutputDir
    
    @TaskAction
    def action() {
        def wsdlSrcDir = new File(temporaryDir, 'genWsdlSources')
        def wsdlBinDir = new File(temporaryDir, 'genWsdlClasses')
        def configuration = project.configurations.getByName('technoJaxws')
        
        wsdlSrcDir.deleteDir()
        wsdlSrcDir.mkdirs()
        wsdlBinDir.deleteDir()
        wsdlBinDir.mkdirs()
        println "  Generating WSDL from class '" + seiClassName + "'\n  in directory '" + wsdlOutputDir + "'." 
        ant.taskdef(name: 'wsgen', classname: 'com.sun.tools.ws.ant.WsGen', classpath: configuration.asPath)
        ant.wsgen(genwsdl: true, keep: false, sei: seiClassName, classpath: seiClassPath.asPath,
                  sourcedestdir: wsdlSrcDir, destdir: wsdlBinDir, resourcedestdir: wsdlOutputDir)
    }
}
