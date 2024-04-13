package com.example;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class players {
    public static String[] headers = {"player_id", "player_name", "player_nickname", "jersey_number", "country"};

    static void addOne(String [] info){
        if(info[0] != null){
            try {
                Statement stmt = Main.conn.createStatement();

                // Fill out the INSERT statement using the user input
                String insertSQL = "INSERT INTO players (player_id, name, nickname, jersey, country";
                insertSQL += ") VALUES (";
                for(int i=0; i<4; i++){
                    insertSQL += "'" + info[i].replace("'", "''") + "',";
                }
                insertSQL += "'" + info[4].replace("'", "''") + "') ON CONFLICT DO NOTHING;"; 
                stmt.executeUpdate(insertSQL);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void populate() {
        Object o;
        String[] ids = lineups.getIDs();
        for(int i=0; i<ids.length; i++){
            try {
                o = new JSONParser().parse(new FileReader(ids[i]));
                JSONArray j = (JSONArray) o;
                JSONObject n;
                for(int k=0; k<j.size(); k++){
                    n = (JSONObject) j.get(k);
                    if((JSONArray) n.get("lineup") != null){
                        JSONArray m = (JSONArray) n.get("lineup");
                        String[] info = new String[m.size()];
                        for(int l=0; l<m.size(); l++){
                            for(int x=0; x<headers.length; x++){
                                if(x==4){
                                    info[x] = String.valueOf(((JSONObject) ((JSONObject) m.get(l)).get(headers[x])).get("name"));
                                } else {
                                    info[x] = String.valueOf(((JSONObject) m.get(l)).get(headers[x]));
                                }
                            }
                            addOne(info);
                        }
                    }
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            
        }
    }
}
