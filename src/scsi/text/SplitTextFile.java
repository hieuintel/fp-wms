/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scsi.text;

import java.io.*;

/**
 *
 * @author HieuIntel
 */
public class SplitTextFile {

    /**
     * the maximum size of each file "chunk" generated, in bytes
     */
    public static long chunkSize = 102400000; //Byte
    public static String pathFile = "E:\\Projects\\Yonsei\\Wonseob\\DMP_FLOW_POP_SEOUL_201402.txt";
    public static String pathSave = "";

    public static void main(String[] args) throws Exception {
        //System.out.println(Core.fileNameWithOutExt(pathFile));
        split(pathFile);
    }

    /**
     * split the file specified by filename into pieces, each of size chunkSize
     * except for the last one, which may be smaller
     */
    public static void split(String filename) throws FileNotFoundException, IOException {
        // open the file
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(filename));

        // get the file length
        File f = new File(filename);
        pathSave = scsi.file.Core.get_pathFile(pathFile) + "/" + scsi.file.Core.get_fileNameWithOutExt(pathFile);
        scsi.file.Core.AutoCreatFolder(pathSave);
        String fileNameWithOutExt = f.getName().replaceFirst("[.][^.]+$", "");
        long fileSize = f.length();

        // loop for each full chunk
        int subfile;
        for (subfile = 0; subfile < fileSize / chunkSize; subfile++) {
            // open the output file
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(pathSave + "/" + fileNameWithOutExt + "_" + subfile + ".txt"));

            // write the right amount of bytes
            for (int currentByte = 0; currentByte < chunkSize; currentByte++) {
                // load one byte from the input file and write it to the output file
                out.write(in.read());
            }

            // close the file
            out.close();
            Core.remove_FirstLine(pathSave + "/" + fileNameWithOutExt + "_" + subfile + ".txt");
            Core.remove_LastLine(pathSave + "/" + fileNameWithOutExt + "_" + subfile + ".txt");
        }

        // loop for the last chunk (which may be smaller than the chunk size)
        if (fileSize != chunkSize * (subfile - 1)) {
            // open the output file
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(pathSave + "/" + fileNameWithOutExt + "_" + subfile + ".txt"));

            // write the rest of the file
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }

            // close the file
            out.close();
            Core.remove_FirstLine(pathSave + "/" + fileNameWithOutExt + "_" + subfile + ".txt");
            Core.remove_LastLine(pathSave + "/" + fileNameWithOutExt + "_" + subfile + ".txt");
        }

        // close the file
        in.close();
    }

    

}
