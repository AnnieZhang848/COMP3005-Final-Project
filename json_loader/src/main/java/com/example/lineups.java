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

public class lineups {
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
                matches[x] = "C:/Users/annie/OneDrive/Documents/GitHub/open-data/data/lineups/" + rs.getString("match_id") + ".json";
                x ++;
            }
            return matches;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    static void addOne(String [] info){
        if(info[0] != null){
            try {
                Statement stmt = Main.conn.createStatement();

                // Fill out the INSERT statement using the user input
                String insertSQL = "INSERT INTO lineups (match_id, team_id";
                for(int i=1; i<=16; i++){
                    insertSQL += ", player_" + i;
                }
                insertSQL += ") VALUES (";
                for(int i=0; i<info.length-2; i++){
                    insertSQL += "'" + info[i] + "',";
                }
                insertSQL += "'" + info[info.length-1] + "') ON CONFLICT DO NOTHING;"; 
                stmt.executeUpdate(insertSQL);
            }
            catch (SQLException e) {
                //e.printStackTrace();
                System.out.println(info[0] + " " + info.length);
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
                        String[] info = new String[m.size()+2];
                        info[0] = (ids[i].split("/")[9].split("\\.")[0]);
                        info[1] = String.valueOf(n.get("team_id"));
                        for(int l=0; l<m.size(); l++){
                            info[l+2] = String.valueOf(((JSONObject) m.get(l)).get("player_id"));
                            //System.out.println(info[0] + " - player " + l + ": " + info[l+2]);
                        }
                        addOne(info);
                    }
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            
        }
    }
}
