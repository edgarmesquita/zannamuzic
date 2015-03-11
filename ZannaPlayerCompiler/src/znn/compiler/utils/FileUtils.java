/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package znn.compiler.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Edgar Mesquita
 */
public class FileUtils
{
    public static void listFiles(String directoryName, ArrayList<File> files, String extension)
    {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList)
        {
            if (file.isFile() && (extension == "" || file.getName().toLowerCase().endsWith(extension)))
            {
                files.add(file);
            }
            else if (file.isDirectory())
            {
                listFiles(file.getAbsolutePath(), files, extension);
            }
        }
    }
    
    public static void deleteDirectory(File file)
            throws IOException
    {

        if (file.isDirectory())
        {
            if (file.list().length == 0)
            {
                file.delete();
                System.out.println("Directory is deleted : "
                        + file.getAbsolutePath());

            }
            else
            {

                //list all the directory contents
                String files[] = file.list();

                for (String temp : files)
                {
                    //construct the file structure
                    File fileDelete = new File(file, temp);

                    //recursive delete
                    deleteDirectory(fileDelete);
                }

                //check the directory again, if empty then delete it
                if (file.list().length == 0)
                {
                    file.delete();
                    System.out.println("Directory is deleted : "
                            + file.getAbsolutePath());
                }
            }

        }
        else
        {
            //if file, then delete it
            file.delete();
            System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }
}
