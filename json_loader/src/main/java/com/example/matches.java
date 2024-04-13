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

public class matches {
    public static String[] headers = {"match_id", "match_date", "kick_off", "competition_id", "season_id", "home_team", "away_team", "home_score", "away_score", "last_updated", "match_week", "competition_stage", "stadium_id", "referee_id"};

    static String[] getIDs(){
        try { 
            Statement stmt = Main.conn.createStatement();
            String[] matches = new String[4];
            ResultSet rs = stmt.executeQuery("SELECT competition_id, season_id FROM competitions");
            int x = 0;
            while(rs.next()){
                matches[x] = "C:/Users/annie/OneDrive/Documents/GitHub/open-data/data/matches/" + rs.getString("competition_id") + "/" + rs.getString("season_id") + ".json";
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
                String insertSQL = "INSERT INTO matches"; 
                insertSQL += "(match_id, match_date, kick_off, competition_id, season_id, home_team, away_team, home_score, away_score, last_updated, match_week, competition_stage, stadium_id, referee_id) VALUES (";
                for(int i=0; i<11; i++){
                    insertSQL += "'" + info[i] + "',";
                }
                insertSQL += "'" + info[11] + "',";
                if(info[12] == "null"){
                    insertSQL += "null,";
                } else {
                    insertSQL += "'" + info[12] + "',";
                }
                if(info[13] == "null"){
                    insertSQL += "null)";
                } else {
                    insertSQL += "'" + info[13] + "')";
                }
                insertSQL += "ON CONFLICT DO NOTHING;"; 
                stmt.executeUpdate(insertSQL);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void populate() {
        Object o;
        String[] ids = matches.getIDs();
        for(int i=0; i<ids.length; i++){
            try {
                o = new JSONParser().parse(new FileReader(ids[i]));
                JSONArray j = (JSONArray) o;
                JSONObject n;
                for(int k=0; k<j.size(); k++){
                    n = (JSONObject) j.get(k);
                    String[] info = new String[14];
                    for(int l=0; l<headers.length; l++){
                        if(l == 3){
                            JSONObject m = (JSONObject) n.get("competition");
                            info[l] = String.valueOf(m.get(headers[l]));
                        } else if(l == 4){
                            JSONObject m = (JSONObject) n.get("season");
                            info[l] = String.valueOf(m.get(headers[l]));
                        } else if(l == 5){
                            JSONObject m = (JSONObject) n.get("home_team");
                            info[l] = String.valueOf(m.get("home_team_id"));
                        } else if(l == 6){
                            JSONObject m = (JSONObject) n.get("away_team");
                            info[l] = String.valueOf(m.get("away_team_id"));
                        } else if(l == 11){
                            JSONObject m = (JSONObject) n.get("competition_stage");
                            info[l] = String.valueOf(m.get("name"));
                        } else if(l == 12 & (JSONObject) n.get("stadium") != null){
                            JSONObject m = (JSONObject) n.get("stadium");
                            info[l] = String.valueOf(m.get("id"));
                        } else if(l == 13 & (JSONObject) n.get("referee") != null){
                            JSONObject m = (JSONObject) n.get("referee");
                            info[l] = String.valueOf(m.get("id"));
                        } else {
                            info[l] = String.valueOf(n.get(headers[l]));
                        }
                    }
                    addOne(info);
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
            
        }
    }
}
