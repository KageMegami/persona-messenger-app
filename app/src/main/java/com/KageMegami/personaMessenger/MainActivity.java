package com.KageMegami.personaMessenger;

import android.os.Bundle;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

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

import io.socket.client.IO;
import io.socket.client.Socket;

import static java.util.Collections.singletonMap;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    public String idToken;
    public static Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(firebaseAuth -> {
            if (auth.getCurrentUser() == null) {
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