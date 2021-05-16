package com.KageMegami.personaMessenger;
import io.socket.client.IO;
import io.socket.client.Socket;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URI;
import java.net.URISyntaxException;

public class Messenger extends Fragment {
    private  LinearLayout messageContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messenger, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView convId = new TextView(getContext());
        String id = getArguments().getString("conversation_id");
        convId.setText(id);
        messageContainer = (LinearLayout)view.findViewById(R.id.messageList);
        messageContainer.addView(convId);
        view.findViewById(R.id.back).setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });
        view.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText box = ((EditText)view.findViewById(R.id.message));
                String message = box.getText().toString();
                box.setText("");
                TextView textView = new TextView(getContext());
                textView.setText(message);
                messageContainer.addView(textView);
                //send message to server
                MainActivity.mSocket.emit("new_message", message);
                new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            ScrollView sv = (ScrollView)view.findViewById(R.id.scrollMessages);
                            sv.fullScroll(View.FOCUS_DOWN);
                        }
                    }, 20
                );
            }
        });
        view.findViewById(R.id.message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            ScrollView sv = (ScrollView)view.findViewById(R.id.scrollMessages);
                            sv.fullScroll(View.FOCUS_DOWN);
                        }
                    }, 250
                );
            }
        });
        view.findViewById(R.id.signout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).signOut();
            }
        });
    }
}