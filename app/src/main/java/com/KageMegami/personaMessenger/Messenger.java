package com.KageMegami.personaMessenger;
import adapter.MessageAdapter;
import entity.Conversation;
import entity.Message;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

public class Messenger extends Fragment {
    private String convId;
    private Conversation conversation;
    protected RecyclerView mRecyclerView;
    protected MessageAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    private  LinearLayout messageContainer;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        convId = getArguments().getString("conversation_id");
        conversation = ((MainActivity)getActivity()).getConversation(convId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messenger, container, false);
        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new MessageAdapter(conversation.messages, this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // messageContainer = view.findViewById(R.id.messageList);

        //set listener for the back button
 /*       view.findViewById(R.id.back).setOnClickListener(v -> {
            hideKeyboard(getActivity());
            NavHostFragment.findNavController(this).navigateUp();
        });*/

        //set listener for the send button
        view.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText box = ((EditText)view.findViewById(R.id.message));
                String message = box.getText().toString();
                box.setText("");

                //send message to server
                JSONObject newMessage = new JSONObject();
                try {
                    newMessage.put("convId", convId);
                    newMessage.put("message", message);
                } catch (JSONException e) {}
                MainActivity.mSocket.emit("new_message", newMessage);
                conversation.messages.add(new Message(message, FirebaseAuth.getInstance().getCurrentUser().getUid()));
                mAdapter.notifyDataSetChanged();
                mLayoutManager.scrollToPosition(conversation.messages.size() - 1);
            }
        });

        //Set type box button listener to scroll up messages
        view.findViewById(R.id.message).setOnClickListener(v -> mRecyclerView.postDelayed(
                () -> mRecyclerView.scrollToPosition(conversation.messages.size() - 1), 200));

        //Set sign out button listener
    /*    view.findViewById(R.id.signout).setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_messenger_to_loadingFragment);
            MainActivity act = ((MainActivity)getActivity());
            NavHostFragment.findNavController(this).navigateUp();
            act.signOut();
        });*/
    }
    public void updateRecyclerView () {
        mAdapter.notifyDataSetChanged();
        mLayoutManager.scrollToPosition(conversation.messages.size() - 1);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}