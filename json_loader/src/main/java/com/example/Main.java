package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    // Class variables 
    public static String url = "jdbc:postgresql://localhost:5432/project_database";
    public static String user = "postgres";
    public static String password = "1234";
    public static Connection conn;

    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, user, password);
            if (conn != null) {
                competitions.populate();
                managers.populate();
                stadiums.populate();
                referees.populate();
                teams.populate();
                matches.populate();
                players.populate();
                events.populate();
                eventTypes.populate();
            } else {
                System.out.println("Failed to establish connection.");
            }
            conn.close();
        }
        catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}