package com.KageMegami.personaMessenger;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Conversation;
import entity.Friend;
import entity.Message;
import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.util.Collections.singletonMap;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    public String idToken;
    public static Socket mSocket;
    public List<Conversation> conversations;
    public List<Friend> friendlist = null;
    //private String url = "http://192.168.200.156:3000";
    private String url = "https://salty-brushlands-38990.herokuapp.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conversations = new ArrayList<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                createSignInIntent();
                return;
            }
            firebaseAuth.getCurrentUser().getIdToken(false).addOnCompleteListener(task -> {
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
                        String content = ((JSONObject) message[0]).getString("message");
                        String convId = ((JSONObject) message[0]).getString("convId");
                        Conversation tmp = getConversation(convId);
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
                        tmp.messages.add(new Message(content, "aled"));
                        runOnUiThread(() -> {
                            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                            Fragment frag = navHostFragment.getChildFragmentManager().getFragments().get(0);
                            if (frag.getClass() == Messenger.class)
                                ((Messenger) frag).updateRecyclerView();
                        });
                    } catch (JSONException e) {
                    }
                });

                // get conversation list from api
                new Thread(() -> {
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    Request request = new Request.Builder()
                            .url(url + "/conversations")
                            .method("GET", null)
                            .addHeader("Authorization", "Bearer " + idToken)
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        if (response.isSuccessful()) {
                            JSONArray convs = new JSONObject(response.body().string()).getJSONArray("data");
                            for (int i = 0; i < convs.length(); i += 1) {
                                conversations.add(new Conversation(convs.getJSONObject(i)));
                            }
                        }
                    } catch (IOException | JSONException e) {}
                    while (friendlist == null) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {}
                    }
                    // notify ui data are ready
                    runOnUiThread(() -> {
                        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                        navHostFragment.getNavController().navigate(R.id.action_loadingFragment_to_homeFragment);
                    });

                    //fetch message for all conversations
                    conversations.forEach(conversation -> {
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
                    });
                }).start();

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
                        if (response.isSuccessful()) {
                            friendlist = new ArrayList<>();
                            JSONArray friends = new JSONObject(response.body().string()).getJSONArray("data");
                            for (int i = 0; i < friends.length(); i += 1) {
                                friendlist.add(new Friend(friends.getJSONObject(i)));
                            }
                        }
                    } catch (IOException | JSONException e) {
                    }
                }).start();
            });
        });
        if (auth.getCurrentUser() == null)
            createSignInIntent();
        setContentView(R.layout.activity_main);
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
        conversations.clear();
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        mSocket.disconnect();
                    }
                });
    }

    public void delete() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }
    public Conversation getConversation(String convId) {
        for (int i = 0; i < conversations.size(); i += 1) {
            String tmp = conversations.get(i).id;
            if (conversations.get(i).id.equals(convId))
                return conversations.get(i);
        }
        return null;
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
}