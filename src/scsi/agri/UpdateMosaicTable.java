/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scsi.agri;

import java.awt.Color;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import static scsi.agri.Main.appendToPane;
import static scsi.agri.Main.getCurrentTimeStamp;
import scsi.db.Config;
import scsi.db.Connect;
import static scsi.db.Core.execute_SQL;
import static scsi.db.Core.run_SQL;

/**
 *
 * @author HieuIntel
 */
public class UpdateMosaicTable {

    /**
     * @param args the command line arguments
     */
    //Test code
    public static void main(String[] args) throws SQLException {
        // TODO code application logic here
//        for (int i = 0; i < 16; i++) {
//            int year = 2000 + i;
//            String y = String.valueOf(year);
//            update_MosaicTable(y, "korea");
//            update_MosaicTable(y, "china");
//            update_MosaicTable(y, "usa");
//        }
    }

    /**
     * Update MosaicTable in PostGIS to support publish a layer to GeoServer
     * @param dbname
     * @param shmname_coutry
     * @throws SQLException 
     */
    public static void update_MosaicTable(String dbname, String shmname_coutry) throws SQLException {
        String connectURLPostgreSQL = Config.connectURLPG + dbname;
        Connection connectPostgreSQL = Connect.getConnectPostgreSQL(connectURLPostgreSQL, Config.userPG, Config.pwPG);

        String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = '" + shmname_coutry + "'";
        ResultSet res = run_SQL(connectPostgreSQL, sql);
        while (res.next()) {
            //Check exist in table
            String fulltblname = shmname_coutry + ".\"" + res.getString("table_name") + "\"";
            String grouptablename = res.getString("table_name").replaceAll("o_2_", "");
            grouptablename = grouptablename.replaceAll("o_4_", "");
            grouptablename = grouptablename.replaceAll("o_6_", "");
            String tblname = shmname_coutry + "." + grouptablename;
            sql = "Select count(*) as count from mosaic where name ='" + tblname + "' and tiletable='" + fulltblname + "'";
            //   sql = "Select count(*) from " + "\"" + shmname + "\"" + ".\"" + tablname + "\"" + " where name ='" + tablname + "'";
            ResultSet rescheck = run_SQL(connectPostgreSQL, sql);
            if (rescheck == null) {
                try {
                    appendToPane(Main.tbupdatemosaicinfor, "Could not check " + fulltblname + " in mosaic table " + getCurrentTimeStamp() + "\n", Color.BLACK);
                } catch (Exception e) {
                }
            } else {
                while (rescheck.next()) {
                    if (rescheck.getInt("count") == 0) {
                        String insert = "INSERT INTO public.mosaic (name, tiletable) VALUES ('" + tblname + "', '" + fulltblname + "')";
                        boolean check = execute_SQL(connectPostgreSQL, insert);
                        if (!check) {
                            System.out.println(fulltblname + " was inserted into mosaic table");
                            try {
                                appendToPane(Main.tbupdatemosaicinfor, fulltblname + " was inserted into mosaic table " + getCurrentTimeStamp() + "\n", Color.BLACK);
                            } catch (Exception e) {
                            }
                            //Render xml file
                        }
                    } else {
                        System.out.println(fulltblname + " is exists in mosaic table");
                        try {
                            appendToPane(Main.tbupdatemosaicinfor, fulltblname + " is exists in mosaic table " + getCurrentTimeStamp() + "\n", Color.BLACK);
                        } catch (Exception e) {
                        }
                    }
                }
            }

        }
    }

}
