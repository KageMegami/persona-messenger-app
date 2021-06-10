package com.KageMegami.personaMessenger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import entity.Conversation;
import entity.User;

public class Data {
    private static Data data = null;
    private List<Conversation> conversations;
    private List<User> friends;
    public User user;


    private Data() {
        conversations = new ArrayList<>();
        friends = new ArrayList<>();
    }

    public static Data getInstance() {
        if (data == null)
            data = new Data();
        return data;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public List<User> getFriends() {
        return friends;
    }

    public User getUser() {
        return user;
    }

    public Conversation getConversation(String convId) {

        for (int i = 0; i < conversations.size(); i += 1) {
            if (conversations.get(i).id.equals(convId))
                return conversations.get(i);
        }
        return null;
    }

    public User getFriend(String id) {

        for (int i = 0; i < friends.size(); i += 1) {
            if (friends.get(i).id.equals(id))
                return friends.get(i);
        }
        return null;
    }

    public void setUser(JSONObject obj) {
        try {
            user = new User(obj);
        } catch (JSONException e) {}
    }
}
