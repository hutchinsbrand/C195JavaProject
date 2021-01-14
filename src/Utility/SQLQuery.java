package Utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLQuery {

    private static PreparedStatement preparedStatement;

    //Method creates a preparedStatement for accessing database
    public static void setPreparedStatement(Connection connection, String sqlQuery) {

        try {
            preparedStatement = connection.prepareStatement(sqlQuery);
        }
        catch (SQLException e) {
            System.out.println("Setting preparedStatement is " + e.getMessage());
        }
    }

    //Method returns prepared statement
    public static PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }
}
