package com.KageMegami.personaMessenger;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Conversation;
import entity.User;
import entity.Message;
import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.util.Collections.singletonMap;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    static public String idToken;
    public static Socket mSocket;
    FirebaseAuth auth;
    FirebaseStorage storage;
    //public static final String url = "http://192.168.200.156:3000";
    public static final String url = "https://salty-brushlands-38990.herokuapp.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        auth.addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                createSignInIntent();
                return;
            }
            FirebaseUser user = firebaseAuth.getCurrentUser();
            user.getIdToken(false).addOnCompleteListener(task -> {
                if (!task.isSuccessful())
                    return;
                idToken = task.getResult().getToken();
                IO.Options options = IO.Options.builder()
                        .setAuth(singletonMap("idToken", idToken))
                        .build();
                try {
                    mSocket = IO.socket(url, options);

                } catch (URISyntaxException e) {
                }

                //connect socket and set new_message handler
                mSocket.connect();
                mSocket.on("new_message", (message) -> {
                    try {
                        String content = ((JSONObject)message[0]).getString("message");
                        String convId = ((JSONObject)message[0]).getString("convId");
                        String sender = ((JSONObject)message[0]).getString("sender");
                        Conversation tmp = Data.getInstance().getConversation(convId);
                        if (tmp == null)
                            return;
                        if (!MyApplication.isActivityVisible()) {
                            try {
                                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                r.play();
                            } catch (Exception e) {
                            }
                        }
                        tmp.messages.add(new Message(content, sender));
                        runOnUiThread(() -> {
                            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                            Fragment frag = navHostFragment.getChildFragmentManager().getFragments().get(0);
                            if (frag.getClass() == MessengerFragment.class)
                                ((MessengerFragment) frag).updateRecyclerView();
                        });
                    } catch (JSONException e) {
                    }
                });
                getUserInfo(user);
            });
        });
        if (auth.getCurrentUser() == null)
            createSignInIntent();
        setContentView(R.layout.activity_main);
    }

    private void getUserInfo(FirebaseUser user) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(url + "/user")
                    .method("GET", null)
                    .addHeader("Authorization", "Bearer " + idToken)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject body = new JSONObject(response.body().string());
                    if (body.getBoolean("exist") == true) {
                        //set user
                        Data.getInstance().setUser(body.getJSONObject("data"));
                        loadData();
                    } else
                        newUser(user);
                }
            } catch (IOException | JSONException e) {}
        }).start();
    }
    private void newUser(FirebaseUser user) {
        runOnUiThread(()-> {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            navHostFragment.getNavController().navigate(R.id.action_loadingFragment_to_welcomeFragment);
        });
        new Thread(() -> {
            JSONObject bodyjson = new JSONObject();
            try {
                bodyjson.put("id", user.getUid());
                bodyjson.put("name", user.getDisplayName());
                if (user.getPhotoUrl() != null)
                    bodyjson.put("photoUrl", user.getPhotoUrl());
                else
                    bodyjson.put("photoUrl", "https://i1.sndcdn.com/artworks-000023237585-jphshz-t500x500.jpg");
                //set user
                Data.getInstance().setUser(bodyjson);
            } catch (JSONException e) { return; }
            RequestBody body = RequestBody.create(JSON, bodyjson.toString());
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(url + "/users")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + idToken)
                    .build();
            try {
                Response response = client.newCall(request).execute();
            } catch (IOException e) {}
        }).start();
    }

    public void loadData(){
        // get friend list from api
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(url + "/friends")
                    .method("GET", null)
                    .addHeader("Authorization", "Bearer " + idToken)
                    .build();
            try {
                Response response = client.newCall(request).execute();

                List<User> friends_list = Data.getInstance().getFriends();
                if (response.isSuccessful()) {
                    JSONArray friends = new JSONObject(response.body().string()).getJSONArray("data");
                    for (int i = 0; i < friends.length(); i += 1) {
                        friends_list.add(new User(friends.getJSONObject(i)));
                    }
                }
            } catch (IOException | JSONException e) {}
        }).start();

        // get conversation list from api
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(url + "/conversations")
                    .method("GET", null)
                    .addHeader("Authorization", "Bearer " + idToken)
                    .build();
            List<Conversation> conversations = Data.getInstance().getConversations();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONArray convs = new JSONObject(response.body().string()).getJSONArray("data");
                    for (int i = 0; i < convs.length(); i += 1) {
                        conversations.add(new Conversation(convs.getJSONObject(i)));
                    }
                }
            } catch (IOException | JSONException e) {}
            // notify ui data are ready
            runOnUiThread(() -> {
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                navHostFragment.getNavController().navigate(R.id.action_loadingFragment_to_homeFragment);
            });

            //fetch message for all conversations
            Iterator<Conversation> it = conversations.iterator();
            while (it.hasNext()) {
                Conversation conversation = it.next();
                new Thread(() -> {
                    OkHttpClient clientConv = new OkHttpClient().newBuilder()
                            .build();
                    Request requestConv = new Request.Builder()
                            .url(url + "/conversations/" + conversation.id + "/messages")
                            .method("GET", null)
                            .addHeader("Authorization", "Bearer " + idToken)
                            .build();
                    try {
                        Response response = clientConv.newCall(requestConv).execute();
                        if (response.isSuccessful()) {
                            JSONArray messages = new JSONObject(response.body().string()).getJSONArray("data");
                            for (int i = 0; i < messages.length(); i += 1) {
                                JSONObject tmp = messages.getJSONObject(i);
                                conversation.messages.add(new Message(tmp.getString("messageContent"), tmp.getString("sender"), tmp.getJSONObject("date")));
                            }
                        }
                    } catch (IOException | JSONException e) {}
                }).start();
            }
        }).start();
    }

    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public void signOut() {
        List<Conversation> conversations = Data.getInstance().getConversations();
        List<User> friendlist = Data.getInstance().getFriends();
        if (conversations != null)
            conversations.clear();
        if (friendlist != null)
            friendlist.clear();
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> mSocket.disconnect());
    }

    public void delete() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(task -> {
                    // ...
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    @Override
    public void onBackPressed() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment.getChildFragmentManager().getFragments().get(0).getClass() == MessengerFragment.class)
            super.onBackPressed();
        else
            moveTaskToBack(false);
    }

}