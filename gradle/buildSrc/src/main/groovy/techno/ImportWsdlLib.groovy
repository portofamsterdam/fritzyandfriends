package techno

import org.gradle.api.DefaultTask
import org.gradle.api.AntBuilder
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.compile.JavaCompile

/*
 * Use WS-import to to compile WSDL to Java stub code and put result in library.
 */
public class ImportWsdlLib extends DefaultTask {

	/** All WSDL files as are to be imported and are put in the JAR under resource directory 'META-INF/wsdl' */
    @InputFiles
    def FileCollection wsdlFiles

	/** [OPTIONAL] All XSD files as are included in de jar; NOTE all WSDL imports must be local. */
    @Optional
    @InputFiles
    def FileCollection xsdFiles

	/** [OPTIONAL] Package to use for for generated classes; use 'autoNameResolution = true' to avoid name clashes.  */
    @Optional
    @Input
    def String javaPackage

	/** [OPTIONAL] Binding file used to adjust the generated WSDL code.  */
    @Optional
    @InputFile
    def File bindingsFile

	/** The output file being the JAR containing the generated code.  */
    @OutputFile
    def File outputLib
    
	/** [OPTIONAL] Allow the use of JAXWS Vendor Extensions.  */
    @Optional
    @Input
    def Boolean extension
    
    /** [OPTIONAL] Generate code as per the given JAXWS specification version; "2.0" will generate artifacts that run with JAX-WS 2.0 runtime. */
    @Optional
    @Input
    def String target

	/** [OPTIONAL] Maps headers not bound to the request or response messages to Java method parameters. */
    @Optional
    @Input
    def Boolean xadditionalHeaders
    
	/** [OPTIONAL] Handles any collisions in naming by making them unique again; usefull in case all is put one single 'javaPackage'. */
    @Optional
    @Input
    def Boolean autoNameResolution

	/** [DEFAULT 'META-INF/wsdl'] Location in JAR where WSDL and XSD files are put. */
    @Optional
    @Input
    def String resourceDestDir = 'META-INF/wsdl'

    @TaskAction
    def action() {
        def importSrcDir = new File(temporaryDir, 'importWsdlSources')
        def importBinDir = new File(temporaryDir, 'importWsdlClasses')
        def sourceLib = new File(outputLib.path.replaceFirst(/\.jar$/, '-sources.jar'))
        def configuration = project.configurations.getByName('technoJaxws')
        
        importSrcDir.deleteDir()
        importSrcDir.mkdirs()
        importBinDir.deleteDir()
        importBinDir.mkdirs()

        for (xsdFile in xsdFiles) {
            checkFileExists(xsdFile, "XSD")
        }
        // NOTE: Set property for wsimport to get access to all external schema's (XSD's and so on) under Java 8 
        def oldAccessValue = System.getProperty('javax.xml.accessExternalSchema')
        System.setProperty('javax.xml.accessExternalSchema', 'all')
        ant.taskdef(name:'wsimport', classname: 'com.sun.tools.ws.ant.WsImport', classpath: configuration.asPath)

        def resourceDir = resourceDestDir ?: ''
        def resourceLocation = resourceDir.startsWith('/') ? resourceDir : '/' + resourceDir;
        resourceLocation = resourceLocation.endsWith('/') ? resourceLocation : resourceLocation + '/'
        
        for (wsdlFile in wsdlFiles) {
            checkFileExists(wsdlFile, "WSDL")
            logger.lifecycle("  Generate stubs from 'WSDL' '" + wsdlFile.name + "'")
                        
            // NOTE: WSDL location configures where WSDL can be found (root of jar); needed for client to verify
            def wsimportArgs = [
                sourcedestdir: importSrcDir, 
                wsdl: wsdlFile, 
                xnocompile: true, 
                keep: true,
                wsdllocation: resourceLocation + wsdlFile.name
            ]
            if (bindingsFile != null) {
                wsimportArgs['binding'] = bindingsFile
            }
            if (javaPackage != null) {
                wsimportArgs['package'] = javaPackage
            }
            if (extension != null) {
                wsimportArgs['extension'] = extension
            }
            if (target != null) {
                wsimportArgs['target'] = target
            }
            if (xadditionalHeaders != null) {
                wsimportArgs['xadditionalHeaders'] = xadditionalHeaders
            }
            if (autoNameResolution != null) {
                ant.wsimport(wsimportArgs) {
                    xjcarg(value: "-XautoNameResolution")
                }
            } else {
                ant.wsimport(wsimportArgs)
            }
            
        }
        // Restore earlier set property to original value
        if (oldAccessValue != null) {
            System.setProperty('javax.xml.accessExternalSchema', oldAccessValue)
        } else {
            System.clearProperty('javax.xml.accessExternalSchema')
        }
        logger.lifecycle("  Compile WSDL stubs ")
        ant.javac(srcdir: importSrcDir, destdir: importBinDir, source: project.sourceCompatibility,
                  target: project.targetCompatibility, includeAntRuntime: false, classpath: configuration.asPath)

        // NOTE: Earlier configured WSDL location expects WSDL to be in root of jar
        logger.lifecycle("  Make WSDL/XSD's part of jar")
        for (wsdlFile in wsdlFiles) {
            ant.copy(file: wsdlFile, toDir: new File(importBinDir, resourceDir))
        }
        // NOTE: assuming that XSD reference is adjusted in WSDL to be found at same level (=> XSD import without path) 
        for (xsdFile in xsdFiles) {
            ant.copy(file: xsdFile, toDir: new File(importBinDir, resourceDir))
        }
        logger.lifecycle("  Create WSDL classes JAR '" + outputLib + "'")
        ant.jar(jarfile: outputLib, basedir: importBinDir)
        logger.lifecycle("  Create WSDL source JAR '" + sourceLib + "'")
        ant.jar(jarfile: sourceLib, basedir: importSrcDir)
        logger.lifecycle("  Completed WSDL import JAR generation succesfully")
    }

    def checkFileExists(File file, String fileType) {
        if (!file.exists()) {
            logger.error("  Can't find '${fileType}' file ${file}")
            throw new StopExecutionException("Can't find '${fileType}' file ${file}")
        }
    }

}
