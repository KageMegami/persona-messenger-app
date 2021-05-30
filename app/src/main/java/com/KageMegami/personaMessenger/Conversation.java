package com.KageMegami.personaMessenger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Conversation {
    public String id;
    public String name;
    public String photoUrl;
    public ArrayList<Message> messages;
    public boolean isGroup;
    public String[] users;
    public boolean isUptodate = false;



    public Conversation(JSONObject conv) throws JSONException {
        id = conv.getString("id");
        name = conv.getString("name");
        photoUrl = conv.getString("photoUrl");
        messages = new ArrayList<>();
        JSONArray tmp = conv.getJSONArray("users");
        users = new String[tmp.length()];
        for (int i = 0; i < tmp.length(); i += 1) {
            users[i] = tmp.getString(i);
        }
        isGroup = conv.getBoolean("group");
    }
}
