package Utility;

import java.io.*;
import java.sql.Timestamp;

public class UserLog {

    private static final String USER_LOG_FILE = "log.txt";

    //Method logs both successful and failed access attempts by users from the login screen and prints to log.txt in the src folder
    public static void log(String userName, boolean match) throws IOException {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        FileWriter fileWriter = new FileWriter(USER_LOG_FILE, true);
        PrintWriter writer = new PrintWriter(fileWriter);

        if (match) {
            writer.println(userName + " logged in at " + timestamp);
        } else {
            writer.println(userName + " failed to log in at " + timestamp);
        }
        writer.close();
    }
}
