package Models;

import Utility.DatabaseConnection;
import Utility.SQLQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {

    private static int userId;
    private String username;
    private String password;
    private boolean active;

    private static User currentUser;

    private static final ObservableList<User> allUsers = FXCollections.observableArrayList();
    private static final ObservableList<String> allUsernames = FXCollections.observableArrayList();
    private static final ObservableList<String> allPasswords = FXCollections.observableArrayList();

    public User(String username, String password) {

        this.username = username;
        this.password = password;


    }

    public static ObservableList<User> getAllUsers() {

        Connection connection = DatabaseConnection.getConnection();
        String allUsersSelect = "SELECT userName, password FROM user";

        SQLQuery.setPreparedStatement(connection, allUsersSelect);
        PreparedStatement ps = SQLQuery.getPreparedStatement();

        try {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getString(1), rs.getString(2));
                allUsers.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return allUsers;
    }

    public static ObservableList<String> getAllUsernames() {

        ObservableList<User> allUsers = User.getAllUsers();

        for (User user : allUsers) {
            String username = user.getUsername();
            allUsernames.add(username);
        }
        return allUsernames;
    }

    public static ObservableList<String> getAllPasswords() {

        ObservableList<User> allUsers = User.getAllUsers();

        for (User user : allUsers) {
            String password = user.getPassword();
            allPasswords.add(password);
        }
        return allPasswords;
    }

    public static void addUser(User newUser) {
        allUsers.add(newUser);
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static int getUserId(String userName) {

        Connection connection = DatabaseConnection.getConnection();
        String userIdSelect = "SELECT userId FROM user WHERE userName = ?";

        SQLQuery.setPreparedStatement(connection, userIdSelect);
        PreparedStatement ps = SQLQuery.getPreparedStatement();

        try {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            rs.next();
            userId = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
