package com.example;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class teams {
    public static String[] headers = {"id", "name", "gender", "country", "manager_id"};

    static void addOne(String [] info){
        if(info[0] != null){
            try {
                Statement stmt = Main.conn.createStatement();

                // Fill out the INSERT statement using the user input
                String insertSQL = "INSERT INTO teams"; 
                insertSQL += "(team_id, name, gender, country, manager_id) VALUES (";
                insertSQL += "'" + info[0] + "',";
                insertSQL += "'" + info[1] + "',";
                insertSQL += "'" + info[2] + "',";
                insertSQL += "'" + info[3] + "',";
                if(info[4] == "null"){
                    insertSQL += "null)";
                } else {
                    insertSQL += "'" + info[4] + "')";
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
                    String[] info = new String[5];
                    for(int l=0; l<headers.length; l++){
                        if((JSONObject) n.get("home_team") != null){
                            JSONObject m = (JSONObject) n.get("home_team");
                            if(l == 3){
                                JSONObject c = (JSONObject) m.get("country");
                                info[l] = String.valueOf(c.get("name"));
                            } else if (l == 4 & (JSONArray) m.get("managers") != null){
                                JSONObject c = (JSONObject) ((JSONArray) m.get("managers")).get(0);
                                info[l] = String.valueOf(c.get("id"));
                            } else {
                                info[l] = String.valueOf(m.get("home_team_" + headers[l]));
                            }
                        }
                    }
                    addOne(info);
                    for(int l=0; l<headers.length; l++){
                        if((JSONObject) n.get("away_team") != null){
                            JSONObject m = (JSONObject) n.get("away_team");
                            if(l == 3){
                                JSONObject c = (JSONObject) m.get("country");
                                info[l] = String.valueOf(c.get("name"));
                            } else if (l == 4 & (JSONArray) m.get("managers") != null){
                                JSONObject c = (JSONObject) ((JSONArray) m.get("managers")).get(0);
                                info[l] = String.valueOf(c.get("id"));
                            } else {
                                info[l] = String.valueOf(m.get("away_team_" + headers[l]));
                            }
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
