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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
public class UpdateIndexTable {

    /**
     * @param args the command line arguments
     */
    //Test code
    public static void main(String[] args) throws ParseException, SQLException {
        // TODO code application logic here
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
//        Date date = formatter.parse("20000101");
//        int countryid = 213;
//        int year = 2000;
//        String[] listtype = {"max", "min", "t2m", "pre", "sho"};
//        int[] listyear = {2000, 2001, 2002, 2003, 2004, 2005, 2006, 2008, 2009, 2011, 2012, 2013, 2014};
//        for (int y = 0; y < listyear.length; y++) {
//            update_IndexbyYearCountry(listyear[y], countryid, type);
//        }

//        for (int t = 0; t < listtype.length; t++) {
//            update_IndexbyYearCountry(2014, countryid, listtype[t]);
//        }
//        for (int i = 0; i < 15; i++) {
//            year = 2000 + i;
//            update_IndexbyYearCountry(year, countryid, type);
//        }
//        update_IndexbyYearCountry(2002, 213, "t2m");
    }

    //Áp dụng cho tính giá trị của một nước trong một năm
    public static void update_IndexbyYearCountry(int year, int id_0, String type) throws ParseException, SQLException {
        String connectURLPostgreSQL = Config.connectURLPG + year;
        Connection connectPostgreSQL = Connect.getConnectPostgreSQL(connectURLPostgreSQL, Config.userPG, Config.pwPG);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
        String connectURLIndextable = Config.connectURLPG + "db_Agri";
        Connection connectIndextable = Connect.getConnectPostgreSQL(connectURLIndextable, Config.userPG, Config.pwPG);

        List<Date> listdate = get_listDate(year + "0101", year + "1231");
        for (int i = 0; i < listdate.size(); i++) {
            Date date = listdate.get(i);
            System.out.println(formatter2.format(date) + "_" + type);
            String countryname = "korea";
            if (id_0 == 213) {
                countryname = "korea";
            }
            if (id_0 == 49) {
                countryname = "china";
            }
            if (id_0 == 244) {
                countryname = "usa";
            }
            //Compute value by input parameter
            String sql = "select id_1 from region_level1 where id_0='" + id_0 + "' ORDER BY id_1";
            ResultSet res = run_SQL(connectIndextable, sql);
            while (res.next()) {
                int id_1 = res.getInt("id_1");
                //System.out.println(id_1);
                String sql_getvalue = " SELECT "
                        + " Min((gv).val) As Min,"
                        + " Max((gv).val) As Max,"
                        + " Sum(\"public\".ST_Area((gv).geom) * (gv).val) / Sum(\"public\".ST_Area((gv).geom)) as Mean"
                        + " FROM ("
                        + " SELECT \"public\".ST_DumpAsPolygons(\"public\".ST_Clip(" + countryname + ".\"" + formatter.format(date) + "_" + type + "\".rast, 1, region_level1.geom, true)) AS gv"
                        + " FROM region_level1," + countryname + ".\"" + formatter.format(date) + "_" + type + "\""
                        + " WHERE  \"public\".region_level1.id_0=" + id_0 + " and \"public\".region_level1.id_1=" + id_1 + ") AS foo ";
                try {
                    ResultSet rescheck = run_SQL(connectPostgreSQL, sql_getvalue);
                    while (rescheck.next()) {
                        Double max = rescheck.getDouble("Max");
                        Double min = rescheck.getDouble("Min");
                        Double mean = rescheck.getDouble("Mean");
                        String code = formatter.format(date) + type + id_0 + id_1;
                        String sql_insert = "INSERT INTO tbl_index (code,id_0, id_1, date,type,min,max,mean) "
                                + " VALUES ('" + code + "','" + id_0 + "','" + id_1 + "','" + formatter2.format(date) + "','" + type + "','" + min + "','" + max + "','" + mean + "')"
                                + " ON CONFLICT (code) DO UPDATE "
                                + " SET min = '" + min + "', "
                                + " max = '" + max + "', "
                                + " mean = '" + mean + "'";
                        //System.out.println(sql_insert);
                        execute_SQL(connectIndextable, sql_insert);
                    }
                } catch (Exception e) {
                    System.out.println(countryname + ".\"" + formatter.format(date) + "_" + type + " error");
                }

            }
        }
        connectPostgreSQL.close();
        connectIndextable.close();
    }

    //Áp dụng cho tính giá trị của một tỉnh/bang trong một năm
    public static void update_IndexbyYear(int year, int id_0, int id_1, String type, Boolean overwrite) throws ParseException, SQLException {
        String connectURLPostgreSQL = Config.connectURLPG + year;
        Connection connectIndextable;
        try (Connection connectDatatable = Connect.getConnectPostgreSQL(connectURLPostgreSQL, Config.userPG, Config.pwPG)) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String connectURLIndextable = Config.connectURLPG + "db_Agri";
            connectIndextable = Connect.getConnectPostgreSQL(connectURLIndextable, Config.userPG, Config.pwPG);
            List<Date> listdate = get_listDate(year + "0101", (year + 1) + "0101");
            for (int i = 0; i < listdate.size(); i++) {
                Date date = listdate.get(i);
                String code = formatter.format(date) + type + id_0 + id_1;
                //Check overwite at this day
                if (overwrite) {
                    get_valueIndex(connectDatatable, connectIndextable, date, id_0, id_1, type);
                } else {
                    //Nếu không ghi đè cần check xem đã tồn tại chưa trước khi tiếp tục
                    String sqlcheck = "select count(*) from tbl_index where code='" + code + "'";
                    ResultSet rescheck = run_SQL(connectIndextable, sqlcheck);
                    while (rescheck.next()) {
                        Double count = rescheck.getDouble("count");
                        if (count > 0) {
                            //Exist. Do nothing
                            System.out.println(code + " is exist");
                            try {
                                appendToPane(Main.tbcreateindexinfor, code + " is exist " + getCurrentTimeStamp() + "\n", Color.ORANGE);
                            } catch (Exception e) {
                            }
                        } else {
                            get_valueIndex(connectDatatable, connectIndextable, date, id_0, id_1, type);
                        }
                    }
                }

            }
        }
        connectIndextable.close();
    }

    //Áp dụng cho tính giá trị của một county trong một năm
    public static void update_IndexbyYear(int year, int id_0, int id_1, int id_2, String type, Boolean overwrite) throws SQLException, SQLException, ParseException {
        String connectURLPostgreSQL = Config.connectURLPG + year;
        Connection connectDatatable = Connect.getConnectPostgreSQL(connectURLPostgreSQL, Config.userPG, Config.pwPG);
        if (connectDatatable == null) {
            connectDatatable = Connect.getConnectPostgreSQL(connectURLPostgreSQL, Config.userPG, Config.pwPG);
            try {
                appendToPane(Main.tbcreateindexinfor, "Could not connect to PostgreSQL" + "\n", Color.RED);
            } catch (Exception e) {
            }
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String connectURLIndextable = Config.connectURLPG + "db_Agri";
            Connection connectIndextable = Connect.getConnectPostgreSQL(connectURLIndextable, Config.userPG, Config.pwPG);
            List<Date> listdate = get_listDate(year + "0101", (year + 1) + "0101");
            for (int i = 0; i < listdate.size(); i++) {
                Date date = listdate.get(i);
                String code = formatter.format(date) + type + id_0 + id_1 + id_2;
                //Check overwite at this day
                if (overwrite) {
                    get_valueIndex(connectDatatable, connectIndextable, date, id_0, id_1, id_2, type);
                } else {
                    //Nếu không ghi đè cần check xem đã tồn tại chưa trước khi tiếp tục
                    String sqlcheck = "select count(*) from tbl_index2 where code='" + code + "'";
                    ResultSet rescheck = run_SQL(connectIndextable, sqlcheck);
                    while (rescheck.next()) {
                        Double count = rescheck.getDouble("count");
                        if (count > 0) {
                            //Exist. Do nothing
                            System.out.println(code + " is exist");
                            try {
                                appendToPane(Main.tbcreateindexinfor, code + " is exist " + getCurrentTimeStamp() + "\n", Color.ORANGE);
                            } catch (Exception e) {
                            }
                        } else {
                            get_valueIndex(connectDatatable, connectIndextable, date, id_0, id_1, id_2, type);
                        }
                    }
                }
            }
            
            connectIndextable.close();
            if (!connectIndextable.isClosed()) {
                connectIndextable.close();
            }
            connectDatatable.close();
            if (!connectDatatable.isClosed()) {
                connectDatatable.close();
            }
        }
    }

    //Áp dụng cho việc lấy giá trị của một tỉnh/bang tại một ngày cụ thể
    public static void get_valueIndex(Connection connectDatatable, Connection connectIndextable, Date date, int id_0, int id_1, String type) throws SQLException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");

        String countryname = "korea";
        if (id_0 == 213) {
            countryname = "korea";
        }
        if (id_0 == 49) {
            countryname = "china";
        }
        if (id_0 == 244) {
            countryname = "usa";
        }
        String code = formatter.format(date) + type + id_0 + id_1;
        String rastertable = "";
        if (id_0 == 244 && "ndvi".equals(type)) {
            if (id_1 == 14) {
                rastertable = countryname + ".\"" + formatter.format(date) + "_" + type + "_il\"";
            } else if (id_1 == 16) {
                rastertable = countryname + ".\"" + formatter.format(date) + "_" + type + "_ia\"";
            }
        } else {
            rastertable = countryname + ".\"" + formatter.format(date) + "_" + type + "\"";
        }
        //Compute value by input parameter
        String sql_getvalue = " SELECT "
                + " Min((gv).val) As Min,"
                + " Max((gv).val) As Max,"
                + " AVG((gv).val) AS Mean"
                + " FROM ("
                + " SELECT \"public\".ST_DumpAsPolygons(\"public\".ST_Clip(" + rastertable + ".rast, 1, region_level1.geom, true)) AS gv"
                + " FROM region_level1," + rastertable
                + " WHERE  \"public\".region_level1.id_0=" + id_0 + " and \"public\".region_level1.id_1=" + id_1
                + " AND \"public\".st_intersects(" + rastertable + ".rast,region_level1.geom)"
                + ") AS foo ";
        if (null != type) {
            switch (type) {
                case "ndvi":
                    sql_getvalue += " where (gv).val >=- 1 AND (gv).val <= 1";
                    break;
                case "pre":
                    sql_getvalue += " where (gv).val >=0";
                    break;
                case "sho":
                    sql_getvalue += " where (gv).val >=0";
                    break;
                default:
                    sql_getvalue += " where (gv).val >=- 1000 AND (gv).val <= 1000";
                    break;
            }
        }
        //System.out.println(sql_getvalue);
        ResultSet rescheck = run_SQL(connectDatatable, sql_getvalue);
        if (null == rescheck) {
            appendToPane(Main.tbcreateindexinfor, "Could not get data for " + code + " " + getCurrentTimeStamp() + "\n", Color.RED);
        } else {
            while (rescheck.next()) {
                Double max = rescheck.getDouble("Max");
                Double min = rescheck.getDouble("Min");
                Double mean = rescheck.getDouble("Mean");

                String sql_insert = "INSERT INTO tbl_index (code,id_0, id_1, date,type,min,max,mean) "
                        + " VALUES ('" + code + "','" + id_0 + "','" + id_1 + "','" + formatter2.format(date) + "','" + type + "','" + min + "','" + max + "','" + mean + "')"
                        + " ON CONFLICT (code) DO UPDATE "
                        + " SET min = '" + min + "', "
                        + " max = '" + max + "', "
                        + " mean = '" + mean + "'";
                execute_SQL(connectIndextable, sql_insert);
                System.out.println("Updated index for " + code);
                try {
                    appendToPane(Main.tbcreateindexinfor, "Updated index for " + code + " " + getCurrentTimeStamp() + "\n", Color.BLACK);
                } catch (Exception e) {
                }
            }
        }

    }

    //Áp dụng cho việc lấy giá trị của một tỉnh tại một ngày cụ thể
    public static void get_valueIndex(Connection connectDatatable, Connection connectIndextable, Date date, int id_0, int id_1, int id_2, String type) throws SQLException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");

        String countryname = "korea";
        if (id_0 == 213) {
            countryname = "korea";
        }
        if (id_0 == 49) {
            countryname = "china";
        }
        if (id_0 == 244) {
            countryname = "usa";
        }
        String code = formatter.format(date) + type + id_0 + id_1 + id_2;
        String rastertable = "";
        if (id_0 == 244 && "ndvi".equals(type)) {
            if (id_1 == 14) {
                rastertable = countryname + ".\"" + formatter.format(date) + "_" + type + "_il\"";
            } else if (id_1 == 16) {
                rastertable = countryname + ".\"" + formatter.format(date) + "_" + type + "_ia\"";
            }
        } else {
            rastertable = countryname + ".\"" + formatter.format(date) + "_" + type + "\"";
        }
        //Compute value by input parameter
        String sql_getvalue = " SELECT "
                + " Min((gv).val) As Min,"
                + " Max((gv).val) As Max,"
                + " AVG((gv).val) AS Mean"
                + " FROM ("
                + " SELECT \"public\".ST_DumpAsPolygons(\"public\".ST_Clip(" + rastertable + ".rast, 1, region_level2.geom, true)) AS gv"
                + " FROM region_level2," + rastertable
                + " WHERE  \"public\".region_level2.id_0=" + id_0 + " and \"public\".region_level2.id_1=" + id_1 + " and \"public\".region_level2.id_2=" + id_2
                + " AND \"public\".st_intersects(" + rastertable + ".rast,region_level2.geom)"
                + ") AS foo ";
        if (null != type) {
            switch (type) {
                case "ndvi":
                    sql_getvalue += " where (gv).val >=- 1 AND (gv).val <= 1";
                    break;
                case "pre":
                    sql_getvalue += " where (gv).val >=0";
                    break;
                case "sho":
                    sql_getvalue += " where (gv).val >=0";
                    break;
                default:
                    sql_getvalue += " where (gv).val >=- 1000 AND (gv).val <= 1000";
                    break;
            }
        }
        //System.out.println(sql_getvalue);
        ResultSet rescheck = run_SQL(connectDatatable, sql_getvalue);
        if (null == rescheck) {
            appendToPane(Main.tbcreateindexinfor, "Could not get data for " + code + " " + getCurrentTimeStamp() + "\n", Color.RED);
        } else {
            while (rescheck.next()) {
                Double max = rescheck.getDouble("Max");
                Double min = rescheck.getDouble("Min");
                Double mean = rescheck.getDouble("Mean");

                String sql_insert = "INSERT INTO tbl_index2 (code,id_0, id_1,id_2, date,type,min,max,mean) "
                        + " VALUES ('" + code + "','" + id_0 + "','" + id_1 + "','" + id_2 + "','" + formatter2.format(date) + "','" + type + "','" + min + "','" + max + "','" + mean + "')"
                        + " ON CONFLICT (code) DO UPDATE "
                        + " SET min = '" + min + "', "
                        + " max = '" + max + "', "
                        + " mean = '" + mean + "'";
                execute_SQL(connectIndextable, sql_insert);
                System.out.println("Updated index for " + code);
                try {
                    appendToPane(Main.tbcreateindexinfor, "Updated index for " + code + " " + getCurrentTimeStamp() + "\n", Color.BLACK);
                } catch (Exception e) {
                }
            }
        }

    }

    public static List<Date> get_listDate(String datestart, String dateend) throws ParseException {
        List<Date> listdate = new ArrayList();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

        Calendar start = Calendar.getInstance();
        start.setTime(formatter.parse(datestart));
        Calendar end = Calendar.getInstance();
        end.setTime(formatter.parse(dateend));

        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
            // Do your job here with `date`.
            listdate.add(date);
        }

        return listdate;
    }

}
