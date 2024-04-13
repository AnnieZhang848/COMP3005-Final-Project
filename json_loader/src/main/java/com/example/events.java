package com.example;

import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class events {
    public static String[] headers = {"id", "index", "period", "timestamp", "minute", 
    "second", "type", "possession", "possession_team", "play_pattern", "team", "player", 
    "position", "location_x", "location_y", "duration", "under_pressure", "off_camera", "out", "match_id"};

    static String[] getIDs(){
        try { 
            Statement stmt = Main.conn.createStatement();
            String[] matches = {};
            ResultSet count = stmt.executeQuery("SELECT COUNT(match_id) FROM matches");
            while(count.next()){
                matches = new String[count.getInt("count")];
            }
            ResultSet rs = stmt.executeQuery("SELECT match_id FROM matches");
            int x = 0;
            while(rs.next()){
                matches[x] = "C:/Users/annie/OneDrive/Documents/GitHub/open-data/data/events/" + rs.getString("match_id") + ".json";
                x ++;
            }
            return matches;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    static String addOne(String [] info){
        if(info[0] != null){
            String insertSQL = "(";
            for(int i=0; i<19; i++){
                if(info[i] == "null" || info[i] == null){
                    insertSQL += "null,";
                } else {
                    insertSQL += "'" + info[i] + "',";
                }
            }
            insertSQL += "'" + info[19] + "')";
            return (insertSQL);
        } else {
            return ("");
        }
    }

    public static void populate() {
        Object o;
        String[] ids = getIDs();
        for(int i=0; i<ids.length; i++){
            String insertSQL = "INSERT INTO events (event_id, index, period, timestamp, minute, second,";
                insertSQL +=  "type, possession, possession_team, play_pattern, team, player, position,";
                insertSQL +=  "location_x, location_y, duration, under_pressure, off_camera, out, match_id) VALUES";
                
            try {
                o = new JSONParser().parse(new FileReader(ids[i]));
                JSONArray j = (JSONArray) o;
                JSONObject n;
                for(int k=0; k<j.size(); k++){
                    n = (JSONObject) j.get(k);
                    String[] info = new String[20];
                    for(int l=0; l<headers.length-1; l++){
                        if(l == 6 || l == 9 || l == 12){
                            if((JSONObject) n.get(headers[l]) != null){
                                JSONObject m = (JSONObject) n.get(headers[l]);
                                info[l] = String.valueOf(m.get("name"));
                            }
                        } else if(l == 8 || l == 10 || l == 11){
                            if((JSONObject) n.get(headers[l]) != null){
                                JSONObject m = (JSONObject) n.get(headers[l]);
                                info[l] = String.valueOf(m.get("id"));
                            }
                        } else {
                            info[l] = String.valueOf(n.get(headers[l]));
                        }
                    }
                    info[headers.length-1] = (ids[i].split("/")[9].split("\\.")[0]);;
                    insertSQL += addOne(info);
                    if(k<j.size()-1){
                        insertSQL += ",";
                    }
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }  
            
            insertSQL += " ON CONFLICT DO NOTHING;";
            try {
                Statement stmt = Main.conn.createStatement();
                stmt.executeUpdate(insertSQL);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }    
}
