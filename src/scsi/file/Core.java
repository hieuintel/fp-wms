package scsi.file;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Core {

    static java.util.List<File> listAllFile = new ArrayList<>();

    public static List<File> get_allFileinFolder(String directoryName) {
        java.io.File directory = new java.io.File(directoryName);
        //get all the files from a directory
        java.io.File[] fList = directory.listFiles();
        for (java.io.File file : fList) {
            if (file.isFile()) {
                //System.out.println(file.getAbsolutePath());
                listAllFile.add(file);
            } else if (file.isDirectory()) {
                get_allFileinFolder(file.getAbsolutePath());
            }
        }
        return listAllFile;
    }

    public static List<File> get_FileinFolder(String directoryName) {
        java.util.List<File> listFile = new ArrayList<>();
        java.io.File directory = new java.io.File(directoryName);
        //get all the files from a directory
        java.io.File[] fList = directory.listFiles();
        for (java.io.File file : fList) {
            if (file.isFile()) {
                //System.out.println(file.getAbsolutePath());
                listFile.add(file);
            }
        }
        return listFile;
    }

    public static void AutoCreatFolder(String path) {
        File dir = new File(path);//Format Path E:\A\B\C
        String stpath[] = dir.getAbsolutePath().trim().split("\\\\");
        String elementPath = stpath[0];//Local Disk
        for (int i = 1; i < stpath.length; i++) {
            elementPath = elementPath + "\\" + stpath[i];
            dir = new File(elementPath);
            dir.mkdir();
        }

    }

    public static boolean check_existFile(String pathFile) {
        File f = new File(pathFile);
        if (f.exists() && !f.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }

    public static void delete_File(String path) {
        File f = new File(path);
        f.delete();
    }

    public static void move_File(String Pathsrc, String Pathdst) {
        try {

            File afile = new File(Pathsrc);

            if (afile.renameTo(new File(Pathdst + afile.getName()))) {
                System.out.println("File" + afile.getName() + " is moved successful!");
            } else {
                System.out.println("File" + afile.getName() + " is failed to move!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void merge_Files(File[] files, File mergedFile) {

        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            fstream = new FileWriter(mergedFile, true);
            out = new BufferedWriter(fstream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        for (File f : files) {
            System.out.println("merging: " + f.getName());
            FileInputStream fis;
            try {
                fis = new FileInputStream(f);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));

                String aLine;
                while ((aLine = in.readLine()) != null) {
                    out.write(aLine);
                    out.newLine();
                }

                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void rename_File(String Pathsrc, String Pathdst, String newName) {
        File oldfile = new File(Pathsrc);
        String st[] = oldfile.getName().trim().split("\\.");
        newName = newName + "." + st[1];
        File newfile = new File(Pathdst + newName);
        if (oldfile.renameTo(newfile)) {
            System.out.println("Rename succesful");
        } else {
            System.out.println("Rename failed");
        }
    }

    public static void count_File(String path) {
        File f = new File(path);
        String[] a = f.list();
        int sofile = a.length;
        System.out.println(sofile);
    }

    public static String get_pathFile(String pathtofile) {
        File file = new File(pathtofile);
        String absolutePath = file.getAbsolutePath();
        String filePath = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
        return filePath;
    }

    public static String get_fileNameWithOutExt(String pathtofile) {
        File file = new File(pathtofile);
        String filename = file.getName().replaceFirst("[.][^.]+$", "");;
        return filename;
    }

    public static String get_fileNameWithOutExt(File f) {
        String fileNameWithOutExt = f.getName().replaceFirst("[.][^.]+$", "");
        return fileNameWithOutExt;
    }

    public static void write_File(File Filename, String text, Boolean OverWrite) {
        try (BufferedWriter fw = new BufferedWriter(new FileWriter(Filename, OverWrite))) {
            fw.append(text + "\n");
            fw.flush();
        } catch (IOException ex) {
        }
    }

}
