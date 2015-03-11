/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package znn.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import sun.net.dns.ResolverConfiguration.Options;
import znn.compiler.utils.FileUtils;
import znn.compiler.utils.StringUtils;

/**
 *
 * @author Edgar Mesquita
 */
public class PlayerCompiler
{

    private static String rootDirectory;
    private static String compiledDirectory;
    private static String sourceDirectory;
    private static String libDirectory;
    private static String libs;
    
    private static final int BUFFER_SIZE = 10240;
    private static final String JAR_FILENAME = "ZannaPlayer.jar";
    private static final String YAP_FILENAME = "ZannaPlayer.yap";
    private static final String LIBRARIES_FOLDERNAME = "Libraries";
    private static final String SOURCE_FOLDERNAME = "ZannaPlayer";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        rootDirectory = System.getProperty("user.dir") + File.separator;
        compiledDirectory = rootDirectory + "compiled" + File.separator;
        libDirectory = rootDirectory + ".." + File.separator + LIBRARIES_FOLDERNAME + File.separator;
        sourceDirectory = rootDirectory + ".." + File.separator + SOURCE_FOLDERNAME + File.separator;

        libs = getClassPath(";", "");
        System.out.println(libs);

        ArrayList<File> files = new ArrayList<File>();
        FileUtils.listFiles(sourceDirectory, files, ".java");

        try
        {
            List<String> options = new ArrayList<String>();
            options.add("-d");
            options.add(compiledDirectory);
            options.add("-classpath");
            options.add(libs);

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(files);
            CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, compilationUnits);

            boolean success = task.call();
            fileManager.close();

            if (success)
            {
                createJarFile();
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(PlayerCompiler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static ArrayList<File> getLibFiles()
    {
        ArrayList<File> libFiles = new ArrayList<File>();
        FileUtils.listFiles(libDirectory, libFiles, ".jar");

        return libFiles;
    }

    private static ArrayList<File> getResourceFiles()
    {
        ArrayList<File> resourceFiles = new ArrayList<File>();
        FileUtils.listFiles(sourceDirectory + "src" + File.separator + "znn" + File.separator + "resources" + File.separator, resourceFiles, "");

        return resourceFiles;
    }

    private static String getClassPath(String joinStr, String pathReplace)
    {
        ArrayList<File> libFiles = getLibFiles();
        String[] libFileNames = new String[libFiles.size()];
        for (int i = 0; i < libFiles.size(); i++)
        {
            String fileName = libFiles.get(i).getAbsolutePath();

            if (!pathReplace.isEmpty())
            {
                fileName = fileName.replace(libDirectory, pathReplace);
            }
            libFileNames[i] = fileName;
        }
        return StringUtils.join(joinStr, libFileNames);
    }

    private static void copyLibsToCompiled()
    {
        ArrayList<File> libFiles = getLibFiles();
        for (int i = 0; i < libFiles.size(); i++)
        {
            File file = libFiles.get(i);

            try
            {
                Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(compiledDirectory + "lib" + File.separator + file.getName()), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (IOException ex)
            {
                Logger.getLogger(PlayerCompiler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void copyYapToCompiled()
    {
        try
        {
            Files.copy(Paths.get(sourceDirectory + YAP_FILENAME), Paths.get(compiledDirectory + YAP_FILENAME), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException ex)
        {
            Logger.getLogger(PlayerCompiler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void copyResourcesToCompiled()
    {
        ArrayList<File> resourceFiles = getResourceFiles();
        String source = sourceDirectory + "src";
        File resource = new File(compiledDirectory + "znn" + File.separator + "resources" + File.separator);
        File fonts = new File(compiledDirectory + "znn" + File.separator + "resources" + File.separator + "fonts" + File.separator);
        try
        {
            if (!resource.exists())
            {
                resource.mkdir();
            }
            if (!fonts.exists())
            {
                fonts.mkdir();
            }

            for (int i = 0; i < resourceFiles.size(); i++)
            {
                File file = resourceFiles.get(i);

                Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(file.getAbsolutePath().replace(source, compiledDirectory)), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);

            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(PlayerCompiler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void createJarFile()
    {
        try
        {
            copyLibsToCompiled();
            copyResourcesToCompiled();
            copyYapToCompiled();

            ArrayList<File> files = new ArrayList<File>();
            FileUtils.listFiles(compiledDirectory + "znn", files, "");

            File f = new File(compiledDirectory + JAR_FILENAME);
            f.createNewFile();

            File[] tobeJared = new File[files.size()];
            tobeJared = files.toArray(tobeJared);

            byte buffer[] = new byte[BUFFER_SIZE];
            // Open archive file
            FileOutputStream stream = new FileOutputStream(f);

            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

            String classPath = getClassPath(" ", "lib/");
            System.out.println(classPath);
            manifest.getMainAttributes().put(Attributes.Name.CLASS_PATH, classPath);

            manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "znn.Main");

            JarOutputStream out = new JarOutputStream(stream, manifest);

            for (int i = 0; i < tobeJared.length; i++)
            {
                if (tobeJared[i] == null || !tobeJared[i].exists()
                        || tobeJared[i].isDirectory())
                {
                    continue; // Just in case...
                }
                String fullFileName = tobeJared[i].getAbsolutePath().replace(compiledDirectory, "");
                System.out.println("Adding " + fullFileName);

                // Add archive entry
                JarEntry jarAdd = new JarEntry(fullFileName);
                jarAdd.setTime(tobeJared[i].lastModified());

                out.putNextEntry(jarAdd);

                // Write file to archive
                FileInputStream in = new FileInputStream(tobeJared[i]);
                while (true)
                {
                    int nRead = in.read(buffer, 0, BUFFER_SIZE);
                    if (nRead <= 0)
                    {
                        break;
                    }
                    out.write(buffer, 0, nRead);
                }
                in.close();
            }

            out.close();
            stream.close();
            System.out.println("Adding completed OK");

            FileUtils.deleteDirectory(new File(compiledDirectory + "znn"));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println("Error: " + ex.getMessage());
        }
    }
}