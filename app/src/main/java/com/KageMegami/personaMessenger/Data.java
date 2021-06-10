package com.KageMegami.personaMessenger;

import org.json.JSONArray;
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
    private User me;
    public List<String> myFriends;

    private Data() {
        conversations = new ArrayList<>();
        friends = new ArrayList<>();
        myFriends = new ArrayList<>();
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
        return me;
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
            me = new User(obj);
            if (obj.has("friends")) {
                JSONArray tmp = obj.getJSONArray("friends");
                for (int i = 0; i < tmp.length(); i += 1) {
                    myFriends.add(tmp.getString(i));
                }
            }
        } catch (JSONException e) {}
    }

    public boolean isMyFriend(final String id) {
        for (int i = 0; i < myFriends.size(); i += 1) {
            if (myFriends.get(i).equals(id))
                return true;
        }
        return false;
    }
}
