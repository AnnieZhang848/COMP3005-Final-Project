package com.example;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class stadiums {
    public static String[] headers = {"id", "name", "country"};

    static void addOne(String [] info){
        if(info[0] != null){
            try {
                Statement stmt = Main.conn.createStatement();

                // Fill out the INSERT statement using the user input
                String insertSQL = "INSERT INTO stadiums"; 
                insertSQL += "(stadium_id, name, country) VALUES (";
                insertSQL += "'" + info[0] + "',";
                insertSQL += "'" + info[1] + "',";
                insertSQL += "'" + info[2] + "')";
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
                    String[] info = new String[3];
                    for(int l=0; l<headers.length; l++){
                        if((JSONObject) n.get("stadium") != null){
                            JSONObject m = (JSONObject) n.get("stadium");
                            if(l == 2){
                                JSONObject c = (JSONObject) m.get("country");
                                info[l] = String.valueOf(c.get("name"));
                            } else {
                                info[l] = String.valueOf(m.get(headers[l]));
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
