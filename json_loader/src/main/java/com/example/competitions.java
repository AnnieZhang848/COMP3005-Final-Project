package com.example;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class competitions {
    public static String[] headers = {"competition_id", "season_id", "country_name", "competition_name", "competition_gender", "competition_youth", "competition_international", "season_name", "match_updated", "match_available"};
    
    static void addOne(String [] info){
        try {
            Statement stmt = Main.conn.createStatement();

            // Fill out the INSERT statement using the user input
            String insertSQL = " INSERT INTO competitions"; 
            insertSQL += "(competition_id, season_id, country_name, competition_name, competition_gender, competition_youth, competition_international, season_name, match_updated, match_available";
            insertSQL += ") VALUES (";
            for(int i=0; i<9; i++){
                insertSQL += "'" + info[i] + "',";
            }
            insertSQL += "'" + info[9] + "')";
            insertSQL += "ON CONFLICT DO NOTHING;";
            stmt.executeUpdate(insertSQL);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void populate() {
            Object o;
            try {
                o = new JSONParser().parse(new FileReader("C:/Users/annie/OneDrive/Documents/GitHub/open-data/data/competitions.json"));
                JSONArray j = (JSONArray) o;
                JSONObject n;
                for(int i=0; i<j.size(); i++){
                    n = (JSONObject) j.get(i);
                    String[] info = new String[10];
                    for(int k=0; k<headers.length; k++){
                        info[k] = String.valueOf(n.get(headers[k]));
                    }
                    if(info[3].equals("La Liga")){
                        if(info[7].equals("2020/2021") | info[7].equals("2019/2020") | info[7].equals("2018/2019")){
                            addOne(info);
                        }
                    } else if(info[3].equals("Premier League") & info[7].equals("2003/2004")){
                        addOne(info);
                    }
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }

    }
}
