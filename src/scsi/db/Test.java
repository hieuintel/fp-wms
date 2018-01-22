/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scsi.db;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import static scsi.db.Connect.getConnectPostgreSQL;

/**
 *
 * @author HieuIntel
 */
public class Test {

    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException
     * @throws java.io.UnsupportedEncodingException
     */
    public static void main(String[] args) throws SQLException, UnsupportedEncodingException {
        // TODO code application logic here
        Connection connectPostgreSQL = getConnectPostgreSQL(Config.connectURLPG + "2000", Config.userPG, Config.pwPG);
        System.err.println(connectPostgreSQL);

//        String sql = "select * from tbl_test where gid=1";
//
//        ResultSet res = null;
//        res = run_SQL(connectPostgreSQL, sql);
//        while (res.next()) {
//            System.out.println(res.getString("gid"));
//        }

    }

}
