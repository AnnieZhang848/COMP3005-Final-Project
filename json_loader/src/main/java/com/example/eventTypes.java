package com.example;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class eventTypes {
    public static HashMap<String, String[]> types = new HashMap<>();

    public static void setUp(){
        types.put("Block", new String[] {"event_id", "offensive", "deflection"});
        types.put("Carry", new String[] {"event_id", "end_location_x", "end_location_y"});
        types.put("Clearance", new String[] {"event_id", "aerial_won", "body_part"});
        types.put("Dribble", new String[] {"event_id", "outcome", "overrun", "nutmeg", "no_touch"});
        types.put("Duel", new String[] {"event_id", "counterpress", "type", "outcome"});
        types.put("Interception", new String[] {"event_id", "outcome"});
        types.put("Miscontrol", new String[] {"event_id", "aerial_won"});
        types.put("Pass", new String[] {"event_id", "recipient", "length", "angle", "height", 
        "end_location_x", "end_location_y", "body_part", "outcome", "cross", "switch", "deflected",
        "miscommunication", "cut_back", "shot_assist", "goal_assist", "through_ball", "type", "technique"});
        types.put("Shot", new String[] {"event_id", "end_location_x", "end_location_y",
        "technique", "statsbomb_xg", "body_part", "type", "outcome", "aerial_won",
        "follows_dribble", "first_time", "open_goal", "deflected"});
        types.put("Substitution", new String[] {"event_id", "replacement", "outcome"});
    }  

    public static void parse(String id, String name, JSONObject t){
        String[] headers = types.get(name);
        String[] info = new String[headers.length];
        info[0] = id;
        switch(name) {
            case "Block":
                block(headers, info, t);
                break;
            case "Carry":
                carry(headers, info, t);
                break;
            case "Clearance":
                clearance(headers, info, t);
                break;   
            case "Dribble":
                dribble(headers, info, t);
                break;
            case "Duel":
                duel(headers, info, t);
                break;
            case "Interception":
                interception(headers, info, t);
                break;
            case "Miscontrol":
                miscontrol(headers, info, t);
                break;
            case "Pass":
                passes(headers, info, t);
                break;
            case "Shot":
                shot(headers, info, t);
                break;
            case "Substitution":
                substitution(headers, info, t);
                break;
            default:
        }
    }

    public static void populate(){
        Object o;
        String[] ids = events.getIDs();
        eventTypes.setUp();
        for(int i=0; i<ids.length; i++){
            try {
                o = new JSONParser().parse(new FileReader(ids[i]));
                JSONArray j = (JSONArray) o;
                JSONObject n;
                for(int k=0; k<j.size(); k++){
                    n = (JSONObject) j.get(k);
                    String id = String.valueOf(n.get("id"));
                    String type = String.valueOf(((JSONObject) n.get("type")).get("name"));
                    if((JSONObject) n.get(type.toLowerCase()) != null){
                        parse(id, type, (JSONObject) n.get(type.toLowerCase()));
                    }
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static void carry (String[] headers, String[] info, JSONObject t){
        JSONArray location = (JSONArray) t.get("end_location");
        info[1] = String.valueOf(location.get(0));
        info[2] = String.valueOf(location.get(1));

        if(info[0] != null){
            try {
                Statement stmt = Main.conn.createStatement();

                String insertSQL = "INSERT INTO carries"; 
                insertSQL += "(event_id, end_location_x, end_location_y) VALUES (";
                for(int i=0; i<info.length-1; i++){
                    insertSQL += "'" + info[i] + "',";
                }
                insertSQL += "'" + info[info.length-1] + "')";
                insertSQL += "ON CONFLICT DO NOTHING;"; 
                stmt.executeUpdate(insertSQL);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void block (String[] headers, String[] info, JSONObject t){
        for(int i=1; i<headers.length; i++){
            if(t.get(headers[i]) != null){
                info[i] = String.valueOf(t.get(headers[i]));
            } else {
                info[i] = "false";
            }
        }

        if(info[0] != null){
            try {
                Statement stmt = Main.conn.createStatement();

                String insertSQL = "INSERT INTO blocks"; 
                insertSQL += "(event_id, offensive, deflection) VALUES (";
                for(int i=0; i<info.length-1; i++){
                    insertSQL += "'" + info[i] + "',";
                }
                insertSQL += "'" + info[info.length-1] + "')";
                insertSQL += "ON CONFLICT DO NOTHING;"; 
                stmt.executeUpdate(insertSQL);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void clearance (String[] headers, String[] info, JSONObject t){
        if(t.get(headers[1]) != null){
            info[1] = String.valueOf(t.get(headers[1]));
        } else {
            info[1] = "false";
        }
        JSONObject bodyPart = (JSONObject) t.get(headers[2]);
        info[2] = String.valueOf(bodyPart.get("name"));

        if(info[0] != null){
            try {
                Statement stmt = Main.conn.createStatement();

                String insertSQL = "INSERT INTO clearances"; 
                insertSQL += "(event_id, aerial_won, body_part) VALUES (";
                for(int i=0; i<info.length-1; i++){
                    insertSQL += "'" + info[i] + "',";
                }
                insertSQL += "'" + info[info.length-1] + "')";
                insertSQL += "ON CONFLICT DO NOTHING;"; 
                stmt.executeUpdate(insertSQL);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void dribble (String[] headers, String[] info, JSONObject t){
        System.out.println(String.valueOf(t));

        JSONObject outcome = (JSONObject) t.get(headers[1]);
        info[1] = String.valueOf(outcome.get("name"));
        
        for(int i=2; i<headers.length; i++){
            if(t.get(headers[i]) != null){
                info[i] = String.valueOf(t.get(headers[i]));
            } else {
                info[i] = "false";
            }
        }

        if(info[0] != null){
            try {
                Statement stmt = Main.conn.createStatement();

                String insertSQL = "INSERT INTO dribbles"; 
                insertSQL += "(event_id, outcome, overrun, nutmeg, no_touch) VALUES (";
                for(int i=0; i<info.length-1; i++){
                    insertSQL += "'" + info[i] + "',";
                }
                insertSQL += "'" + info[info.length-1] + "')";
                insertSQL += "ON CONFLICT DO NOTHING;"; 
                stmt.executeUpdate(insertSQL);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void duel (String[] headers, String[] info, JSONObject t){
        if(t.get(headers[1]) != null){
            info[1] = String.valueOf(t.get(headers[1]));
        } else {
            info[1] = "false";
        }
        
        for(int i=2; i<headers.length; i++){
            if(t.get(headers[i]) != null){
                JSONObject outcome = (JSONObject) t.get(headers[i]);
                info[i] = String.valueOf(outcome.get("name"));
            }
        }

        if(info[0] != null){
            try {
                Statement stmt = Main.conn.createStatement();

                String insertSQL = "INSERT INTO duels"; 
                insertSQL += "(event_id, counterpress, type, outcome) VALUES (";
                for(int i=0; i<info.length-1; i++){
                    if(info[i].equals("null")){
                        insertSQL += "null,";
                    } else {
                        insertSQL += "'" + info[i] + "',";
                    }
                }
                insertSQL += "'" + info[info.length-1] + "')";
                insertSQL += "ON CONFLICT DO NOTHING;"; 
                stmt.executeUpdate(insertSQL);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void interception (String[] headers, String[] info, JSONObject t){
        JSONObject outcome = (JSONObject) t.get(headers[1]);
        info[1] = String.valueOf(outcome.get("name"));

        if(info[0] != null){
            try {
                Statement stmt = Main.conn.createStatement();

                String insertSQL = "INSERT INTO interceptions"; 
                insertSQL += "(event_id, outcome) VALUES (";
                for(int i=0; i<info.length-1; i++){
                    insertSQL += "'" + info[i] + "',";
                }
                insertSQL += "'" + info[info.length-1] + "')";
                insertSQL += "ON CONFLICT DO NOTHING;"; 
                stmt.executeUpdate(insertSQL);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void miscontrol (String[] headers, String[] info, JSONObject t){
        if(t.get(headers[1]) != null){
            info[1] = String.valueOf(t.get(headers[1]));
        } else {
            info[1] = "false";
        }

        if(info[0] != null){
            try {
                Statement stmt = Main.conn.createStatement();

                String insertSQL = "INSERT INTO miscontrols"; 
                insertSQL += "(event_id, aerial_won) VALUES (";
                for(int i=0; i<info.length-1; i++){
                    insertSQL += "'" + info[i] + "',";
                }
                insertSQL += "'" + info[info.length-1] + "')";
                insertSQL += "ON CONFLICT DO NOTHING;"; 
                stmt.executeUpdate(insertSQL);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void passes (String[] headers, String[] info, JSONObject t){
        if(t.get(headers[1]) != null){
            JSONObject player = (JSONObject) t.get(headers[1]);
            info[1] = String.valueOf(player.get("id"));
        }
        for(int i=2; i<headers.length; i++){
            if((i == 4 || i == 7 || i == 8 || i == 17 || i == 18) && t.get(headers[i]) != null){
                JSONObject n = (JSONObject) t.get(headers[i]);
                info[i] = String.valueOf(n.get("name"));
            } else if (i == 5) {
                JSONArray n = (JSONArray) t.get("end_location");
                info[i] = String.valueOf(n.get(0));
            } else if (i == 6) {
                JSONArray n = (JSONArray) t.get("end_location");
                info[i] = String.valueOf(n.get(1));
            } else if (8 < i && i < 17) {
                if(t.get(headers[i]) != null){
                    info[i] = String.valueOf(t.get(headers[i]));
                } else {
                    info[i] = "false";
                }
            } else {
                info[i] = String.valueOf(t.get(headers[i]));
            }
        }

        if(info[0] != null){
            try {
                Statement stmt = Main.conn.createStatement();

                String insertSQL = "INSERT INTO passes"; 
                insertSQL += "(event_id, recipient, length, angle, height, end_location_x, end_location_y,";
                insertSQL += "body_part, outcome, crossed, switch, deflected, miscommunication,";
                insertSQL +=  "cut_back, shot_assist, goal_assist, through_ball, type, technique) VALUES (";
                for(int i=0; i<info.length-1; i++){
                    if(info[i] == null){
                        insertSQL += "null,";
                    } else {
                        insertSQL += "'" + info[i] + "',";
                    }
                }
                insertSQL += "'" + info[info.length-1] + "')";
                insertSQL += "ON CONFLICT DO NOTHING;"; 
                stmt.executeUpdate(insertSQL);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void shot (String[] headers, String[] info, JSONObject t){
        for(int i=1; i<headers.length; i++){
            if((i == 3 || i == 5 || i == 6 || i == 7) && t.get(headers[i]) != null){
                JSONObject n = (JSONObject) t.get(headers[i]);
                info[i] = String.valueOf(n.get("name"));
            } else if (i == 1) {
                JSONArray n = (JSONArray) t.get("end_location");
                info[i] = String.valueOf(n.get(0));
            } else if (i == 2) {
                JSONArray n = (JSONArray) t.get("end_location");
                info[i] = String.valueOf(n.get(1));
            } else if (7 < i && i < 13) {
                if(t.get(headers[i]) != null){
                    info[i] = String.valueOf(t.get(headers[i]));
                } else {
                    info[i] = "false";
                }
            } else {
                info[i] = String.valueOf(t.get(headers[i]));
            }
        }

        if(info[0] != null){
            try {
                Statement stmt = Main.conn.createStatement();

                String insertSQL = "INSERT INTO shots"; 
                insertSQL += "(event_id, end_location_x, end_location_y, technique, statsbomb_xg, body_part, ";
                insertSQL += "type, outcome, aerial_won, follows_dribble, first_time, open_goal, deflected) VALUES (";
                for(int i=0; i<info.length-1; i++){
                    if(info[i] == null){
                        insertSQL += "null,";
                    } else {
                        insertSQL += "'" + info[i] + "',";
                    }
                }
                insertSQL += "'" + info[info.length-1] + "')";
                insertSQL += "ON CONFLICT DO NOTHING;"; 
                stmt.executeUpdate(insertSQL);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void substitution (String[] headers, String[] info, JSONObject t){
        JSONObject player = (JSONObject) t.get(headers[1]);
        info[1] = String.valueOf(player.get("id"));
        JSONObject outcome = (JSONObject) t.get(headers[2]);
        info[2] = String.valueOf(outcome.get("name"));

        if(info[0] != null){
            try {
                Statement stmt = Main.conn.createStatement();

                String insertSQL = "INSERT INTO substitutions"; 
                insertSQL += "(event_id, replacement, outcome) VALUES (";
                for(int i=0; i<info.length-1; i++){
                    insertSQL += "'" + info[i] + "',";
                }
                insertSQL += "'" + info[info.length-1] + "')";
                insertSQL += "ON CONFLICT DO NOTHING;"; 
                stmt.executeUpdate(insertSQL);
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
