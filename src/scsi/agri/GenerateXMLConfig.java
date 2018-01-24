/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scsi.agri;

import java.awt.Color;
import scsi.db.Connect;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import static scsi.agri.Main.appendToPane;
import static scsi.agri.Main.getCurrentTimeStamp;
import scsi.db.Config;
import static scsi.db.Core.run_SQL;
import static scsi.file.Core.write_File;
import static scsi.text.Core.read_TextFile;

/**
 *
 * @author HieuIntel
 */
public class GenerateXMLConfig {

    /**
     * @param args the command line arguments
     */
    public static String path_storeXMLconfig = "C:/AgriXMLCNtest/";

    //Test code
    public static void main(String[] args) throws SQLException, IOException {
        // TODO code application logic here
//        for (int i = 0; i < 16; i++) {
//            int year = 2000 + i;
//            String y = String.valueOf(year);
//            render_imagemosaicjdbcxml(y);
//        }

//        for (int i = 0; i < 16; i++) {
//            int year = 2000 + i;
//            String y = String.valueOf(year);
//            render_imagemosaicjdbcxmlbyRegion(y,"china");
//        }
    }

    /**
     *
     * @param dbname
     * @throws SQLException
     * @throws IOException Generate XML config file for GeoServer using running
     * code directly
     * @ Ignore this function when using UI mode
     */
    public static void render_imagemosaicjdbcxml(String dbname) throws SQLException, IOException {
        String connectURLPostgreSQL = Config.connectURLPG + dbname;
        Connection connectPostgreSQL = Connect.getConnectPostgreSQL(connectURLPostgreSQL, Config.userPG, Config.pwPG);
        String sql = "select DISTINCT name from mosaic order by name";
        ResultSet res = run_SQL(connectPostgreSQL, sql);
        String pathstore = path_storeXMLconfig + dbname;
        scsi.file.Core.AutoCreatFolder(pathstore);

        String sourcepath = new java.io.File(".").getCanonicalPath();
        System.out.println(sourcepath);
        String xmlstring = read_TextFile(sourcepath + "\\src\\scsi\\agri\\imagemosaicjdbc.xml");

        xmlstring = xmlstring.replaceAll("dbname", dbname);

        while (res.next()) {
            String name = res.getString("name");
            String newxml = xmlstring.replaceAll("tblname", name);
            scsi.file.Core.write_File(new File(pathstore + "/" + name + ".xml"), newxml, Boolean.FALSE);
            System.out.println("render " + pathstore + "/" + name + ".xml");
        }
        //For GeoServer
        String pathdatageoserver = path_storeXMLconfig + "data/" + dbname;
        scsi.file.Core.AutoCreatFolder(pathdatageoserver);
        xmlstring = read_TextFile(sourcepath + "\\src\\scsi\\agri\\connect.pgraster.xml.inc");

        xmlstring = xmlstring.replaceAll("ur", Config.userPG);
        String[] st = xmlstring.split("pw");
        xmlstring = st[0] + Config.pwPG + st[1];
        xmlstring = xmlstring.replaceAll("host", Config.hostPG);
        xmlstring = xmlstring.replaceAll("port", Config.portPG);
        xmlstring = xmlstring.replaceAll("dbname", dbname);
        write_File(new File(pathdatageoserver + "/" + "connect.pgraster.xml.inc"), xmlstring, Boolean.FALSE);

        xmlstring = read_TextFile(sourcepath + "\\src\\scsi\\agri\\mapping.pgraster.xml.inc");
        write_File(new File(pathdatageoserver + "/" + "mapping.pgraster.xml.inc"), xmlstring, Boolean.FALSE);

    }

    /**
     * Generate XML config file for GeoServer using running code directly This
     * is a extention of function above when we set a specific region Ignore
     * this function when using UI mode
     */
    public static void render_imagemosaicjdbcxmlbyRegion(String dbname, String region) throws SQLException, IOException {
        String connectURLPostgreSQL = Config.connectURLPG + dbname;
        Connection connectPostgreSQL = Connect.getConnectPostgreSQL(connectURLPostgreSQL, Config.userPG, Config.pwPG);
        String sql = "select DISTINCT name from mosaic where name like '" + region + "%' order by name";
        ResultSet res = run_SQL(connectPostgreSQL, sql);
        String pathstore = path_storeXMLconfig + dbname;
        scsi.file.Core.AutoCreatFolder(pathstore);

        String sourcepath = new java.io.File(".").getCanonicalPath();
        System.out.println(sourcepath);
        String xmlstring = read_TextFile(sourcepath + "\\src\\scsi\\agri\\imagemosaicjdbc.xml");

        xmlstring = xmlstring.replaceAll("dbname", dbname);

        while (res.next()) {
            String name = res.getString("name");
            String newxml = xmlstring.replaceAll("tblname", name);
            scsi.file.Core.write_File(new File(pathstore + "/" + name + ".xml"), newxml, Boolean.FALSE);
            System.out.println("render " + pathstore + "/" + name + ".xml");
        }
        //For GeoServer
        String pathdatageoserver = path_storeXMLconfig + "data/" + dbname;
        scsi.file.Core.AutoCreatFolder(pathdatageoserver);
        xmlstring = read_TextFile(sourcepath + "\\src\\scsi\\agri\\connect.pgraster.xml.inc");

        xmlstring = xmlstring.replaceAll("ur", Config.userPG);
        String[] st = xmlstring.split("pw");
        xmlstring = st[0] + Config.pwPG + st[1];
        xmlstring = xmlstring.replaceAll("host", Config.hostPG);
        xmlstring = xmlstring.replaceAll("port", Config.portPG);
        xmlstring = xmlstring.replaceAll("dbname", dbname);
        write_File(new File(pathdatageoserver + "/" + "connect.pgraster.xml.inc"), xmlstring, Boolean.FALSE);

        xmlstring = read_TextFile(sourcepath + "\\src\\scsi\\agri\\mapping.pgraster.xml.inc");
        write_File(new File(pathdatageoserver + "/" + "mapping.pgraster.xml.inc"), xmlstring, Boolean.FALSE);

    }

    /**
     * Generate XML config file for GeoServer using UI
     *
     * @param dbname
     * @param region
     * @param pathXMLtemplate
     * @param pathTosave
     * @throws SQLException
     * @throws IOException
     */
    public static void render_imagemosaicjdbcxmlbyRegionUImode(String dbname, String region, String pathXMLtemplate, String pathTosave) throws SQLException, IOException {

        String connectURLPostgreSQL = Config.connectURLPG + dbname;
        Connection connectPostgreSQL = Connect.getConnectPostgreSQL(connectURLPostgreSQL, Config.userPG, Config.pwPG);
        if (connectPostgreSQL == null) {
            try {
                appendToPane(Main.tbgenerateXMLinfor, "Could not connect to PostgreSQL" + "\n", Color.RED);
            } catch (Exception e) {
            }
        } else {
            String sql = "select DISTINCT name from mosaic where name like '" + region + "%' order by name";
            ResultSet res = run_SQL(connectPostgreSQL, sql);
            String pathstore = pathTosave + "\\" + dbname;
            scsi.file.Core.AutoCreatFolder(pathstore);

            String xmlstring = read_TextFile(pathXMLtemplate + "\\imagemosaicjdbc.xml");

            xmlstring = xmlstring.replaceAll("dbname", dbname);

            while (res.next()) {
                String name = res.getString("name");
                String newxml = xmlstring.replaceAll("tblname", name);
                scsi.file.Core.write_File(new File(pathstore + "\\" + name + ".xml"), newxml, Boolean.FALSE);
                //System.out.println("Created " + pathstore + "\\" + name + ".xml");
                try {
                    appendToPane(Main.tbgenerateXMLinfor, "Created " + pathstore + "\\" + name + ".xml " + getCurrentTimeStamp() + "\n", Color.BLACK);
                } catch (Exception e) {
                }
            }
            //For GeoServer
            String pathdatageoserver = pathTosave + "\\data\\" + dbname;
            scsi.file.Core.AutoCreatFolder(pathdatageoserver);
            xmlstring = read_TextFile(pathXMLtemplate + "\\connect.pgraster.xml.inc");

            xmlstring = xmlstring.replaceAll("ur", Config.userPG);
            String[] st = xmlstring.split("pw");
            xmlstring = st[0] + Config.pwPG + st[1];
            xmlstring = xmlstring.replaceAll("host", Config.hostPG);
            xmlstring = xmlstring.replaceAll("port", Config.portPG);
            xmlstring = xmlstring.replaceAll("dbname", dbname);

            write_File(new File(pathdatageoserver + "\\connect.pgraster.xml.inc"), xmlstring, Boolean.FALSE);

            xmlstring = read_TextFile(pathXMLtemplate + "\\mapping.pgraster.xml.inc");
            write_File(new File(pathdatageoserver + "\\mapping.pgraster.xml.inc"), xmlstring, Boolean.FALSE);
            connectPostgreSQL.close();
        }
        
    }
}
