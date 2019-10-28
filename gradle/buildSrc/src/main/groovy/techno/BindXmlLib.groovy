package techno

import org.gradle.api.AntBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.compile.JavaCompile

/*
 * Use XJC to compile XSD to XML/Java binding code and put result in library.
 */
public class BindXmlLib extends DefaultTask {
	def String jvmArg = '-Djavax.xml.accessExternalSchema=all'
    @InputFiles
    def FileCollection xsdFiles

    @OutputFile
    def File outputLib

	@Input
	def boolean extension = false

	@Optional
	@Input
	def String javaPackage


    @TaskAction
    def action() {
        def bindSrcDir = new File(temporaryDir, 'bindXmlSources')
        def bindBinDir = new File(temporaryDir, 'bindXmlClasses')
        def sourceLib = new File(outputLib.path.replaceFirst(/\.jar$/, '-sources.jar'))
        def configuration = project.configurations.getByName('technoJaxb')

        bindSrcDir.deleteDir()
        bindSrcDir.mkdirs()
        bindBinDir.deleteDir()
        bindBinDir.mkdirs()

        ant.taskdef(name:'xjc', classname: 'com.sun.tools.xjc.XJCTask', classpath: configuration.asPath)
        for (xsdFile in xsdFiles) {
            checkFileExists(xsdFile, "XSD")
            def bindFile = new File(xsdFile.path.replaceFirst(/\.xsd$/, '.xjb'))
            checkFileExists(bindFile, "binding")
            
            logger.lifecycle("  Generate bindings from 'XSD' '" + xsdFile.name + "'")
			if (javaPackage == null) {
				ant.xjc(destdir: bindSrcDir, schema: xsdFile, binding: bindFile, extension: extension)
			} else {
				ant.xjc(destdir: bindSrcDir, schema: xsdFile, binding: bindFile, extension: extension, package: javaPackage)
			}
        }
        logger.lifecycle("  Compile generated bindings")
        ant.javac(srcdir: bindSrcDir, destdir: bindBinDir, source: project.sourceCompatibility,
                  target: project.targetCompatibility, includeAntRuntime: false, classpath: configuration.asPath)

        logger.lifecycle("  Make XSD's part of jar")
        for (xsdFile in xsdFiles) {
            ant.copy(file: xsdFile, toDir: bindBinDir)
        }
        logger.lifecycle("  Create bindings class  JAR '" + outputLib + "'")
        ant.jar(jarfile: outputLib, basedir: bindBinDir)
        logger.lifecycle("  Create bindings source JAR '" + sourceLib + "'")
        ant.jar(jarfile: sourceLib, basedir: bindSrcDir)
        logger.lifecycle("  Completed binding generation succesfully")
    }
    
    def checkFileExists(File file, String fileType) {
        if (!file.exists()) {
            logger.error("  Can't find '${fileType}' file ${file}")
            throw new StopExecutionException("Can't find '${fileType}' file ${file}")
        }
    }
}
