/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scsi.db;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import scsi.agri.Main;
import static scsi.agri.Main.appendToPane;
import static scsi.agri.Main.getCurrentTimeStamp;

//Module kết nối tới các hệ quản trị 
public class Connect {

    //Connect to PostgreSQL
    public static Connection getConnectPostgreSQL(String connectString, String userName, String passWord) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(connectString, userName, passWord);
            System.out.println("Connected to PostgreSQL");
        } catch (SQLException ex) {
            System.err.println("Could not connect to PostgreSQL");
        }
        return connection;
    }

}
