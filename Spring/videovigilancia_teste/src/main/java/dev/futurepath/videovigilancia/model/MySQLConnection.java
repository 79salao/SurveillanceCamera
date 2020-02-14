package dev.futurepath.videovigilancia.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver" ;
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static final String DATABASE = "surveillance";
    private static final String SERVER = "localhost";
    private static final String PORT = "3306";
    
    public static Connection getConnection(){
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(
                    "jdbc:mysql://" + SERVER + ":" + PORT + "/" + DATABASE, USERNAME, PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e);
        }
        return null;
    }
}
