package Utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DBNAME = "U075sF";
    private static final String URL = "jdbc:mysql://3.227.166.251/" + DBNAME;
    private static final String USERNAME = "U075sF";
    private static final String PASSWORD = "53688949716";
    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static Connection connection;

    public static void connect() {

        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connection successful!");

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void disconnect() {

        try {
            connection.close();
        } catch (SQLException e) {
            e.getMessage();
        }
        System.out.println("Connection Closed!");


    }

}
