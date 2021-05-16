package com.KageMegami.personaMessenger;

import android.net.Uri;
import android.os.Bundle;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public JSONObject[] conversations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                createSignInIntent();
                return;
            }
            firebaseAuth.getCurrentUser().getIdToken(false).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    idToken = task.getResult().getToken();
                     IO.Options options = IO.Options.builder()
                            .setAuth(singletonMap("idToken", idToken))
                            .build();
                    try {
                        mSocket = IO.socket("http://192.168.200.156:3000", options);
                    } catch (URISyntaxException e) {}
                    mSocket.connect();


                    new Thread(() -> {
                        OkHttpClient client = new OkHttpClient().newBuilder()
                                .build();
                        Request request = new Request.Builder()
                                .url("http://192.168.200.156:3000/conversations")
                                .method("GET", null)
                                .addHeader("Authorization", "Bearer " + idToken)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            if (response.isSuccessful()) {
                                JSONArray convs = new JSONObject(response.body().string()).getJSONArray("data");
                                conversations = new JSONObject[convs.length()];
                                for (int i = 0; i < convs.length(); i += 1)
                                    conversations[i] = convs.getJSONObject(i);
                            }
                        } catch (IOException | JSONException e) {
                        }
                        runOnUiThread(() -> {
                            Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                            HomeFragment fragment = (HomeFragment)(navHostFragment.getChildFragmentManager().getFragments().get(0));
                            fragment.updateRecyclerView();
                        });
                    }).start();


                } else {
                }
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
}