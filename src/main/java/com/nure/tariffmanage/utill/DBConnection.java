package com.nure.tariffmanage.utill;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;
public final class DBConnection {
    private static Connection connection;
    private static boolean isDriverLoaded = false;
    static{
        try{
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Driver Loaded");
            isDriverLoaded = true;
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private final static String DBName = "ORCL";
    private final static String url="jdbc:oracle:thin:@localhost:1521:"+DBName;
    private final static String user="manager";
    private final static String password="manager";

    public static Connection getConnection() throws SQLException{
        if(connection != null){
            return connection;
        }
        if(isDriverLoaded){
            connection = DriverManager.getConnection(url,user,password);
            System.out.println("Connection established");
            return connection;
        }
        System.err.println("No driver loaded!");
        throw new SQLException();
    }


    public static void closeConnection(Connection con) throws SQLException{
        if(con!=null){
            con.close();
            System.out.println("connection closed");
        }
    }
}