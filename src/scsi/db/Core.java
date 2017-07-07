/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scsi.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HieuIntel
 */
public class Core {

    public static boolean execute_SQL(Connection conn, String sql) {
        try {
            Statement stmt = conn.createStatement();
            boolean check =  stmt.execute(sql);
            return check;

        } catch (SQLException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
            return true;
        }
        
    }

    public static ResultSet run_SQL(Connection conn, String sql) {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
//            while (rs.next()) {
//                System.out.println(rs.getString("to_regclass"));
//            }

        } catch (SQLException ex) {
            Logger.getLogger(Core.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
