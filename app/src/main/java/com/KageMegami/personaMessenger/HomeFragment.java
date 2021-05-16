package com.KageMegami.personaMessenger;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HomeFragment extends Fragment {

    private LinearLayout friendsContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateList();
    }
    public void updateList() {
        friendsContainer = (LinearLayout)getView().findViewById(R.id.friendsList);
        JSONArray friends = ((MainActivity)getActivity()).friends;
        if (friends == null)
            return;
        for (int i = 0; i < friends.length(); i += 1) {
            try {
                JSONObject tmp = (JSONObject) friends.get(i);
                TextView name = new TextView(getContext());
                name.setText(tmp.getString("name"));
                friendsContainer.addView(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}