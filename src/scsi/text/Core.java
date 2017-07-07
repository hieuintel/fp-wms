/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scsi.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;

/**
 *
 * @author HieuIntel
 */
public class Core {

    public static String read_TextFile(String pathFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(pathFile));
        String everything;
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            everything = sb.toString();
        } finally {
            br.close();
        }
        return everything;
    }

    public static void write_TextFile(File Filename, String text, Boolean OverWrite) {
        try (BufferedWriter fw = new BufferedWriter(new FileWriter(Filename, OverWrite))) {
            fw.append(text + "\n");
            fw.flush();
        } catch (IOException ex) {
        }
    }

    public static boolean check_meaningLine(String line, String commentcharacter) {
        if (line == null) {
            return false;
        }
        if ("".equals(line)) {
            return false;
        }
        line = line.trim();
        if (line.compareTo("") == 0) {// Kiểm tra dòng trống
            return false;
        }
        if (line.substring(0, commentcharacter.length()).equals(commentcharacter)) {
            return false;
        }
        return true;
    }

    @SuppressWarnings("empty-statement")
    public static int count_Lines(File aFile) throws IOException {
        try (LineNumberReader reader = new LineNumberReader(new FileReader(aFile))) {
            while ((reader.readLine()) != null);
            return reader.getLineNumber();
        } catch (Exception ex) {
            return -1;
        }
    }
    public static void remove_FirstLine(String fileName) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        //Initial write position                                             
        long writePosition = raf.getFilePointer();
        raf.readLine();
        // Shift the next lines upwards.                                      
        long readPosition = raf.getFilePointer();

        byte[] buff = new byte[1024];
        int n;
        while (-1 != (n = raf.read(buff))) {
            raf.seek(writePosition);
            raf.write(buff, 0, n);
            readPosition += n;
            writePosition += n;
            raf.seek(readPosition);
        }
        raf.setLength(writePosition);
        raf.close();
    }

    public static void remove_LastLine(String fileName) throws IOException {
        RandomAccessFile f = new RandomAccessFile(fileName, "rw");
        long length = f.length() - 1;
        byte b;
        do {
            length -= 1;
            f.seek(length);
            b = f.readByte();
        } while (b != 10);
        f.setLength(length + 1);
        f.close();
    }
    

  
}
