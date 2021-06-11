package com.KageMegami.personaMessenger;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import adapter.ConvAdapter;
import adapter.FriendAdapter;
import entity.Conversation;
import entity.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.pm.PackageManager.PERMISSION_DENIED;



public class FriendsFragment extends Fragment {
    protected RecyclerView mRecyclerView;
    protected FriendAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new FriendAdapter(Data.getInstance().getFriends(), this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set bottom bar actions
        BottomBarFragment bottom = (BottomBarFragment)getChildFragmentManager().getFragments().get(0);
        bottom.getView().findViewById(R.id.chats).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_friendsFragment_to_homeFragment);
        });
        view.findViewById(R.id.add).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddFriendActivity.class);
            startActivity(intent);
        });
    }

    public void acceptFriendRequest(String id){
        Data.getInstance().myFriends.add(id);
        mAdapter.notifyDataSetChanged();
        new Thread(() -> {
            JSONObject bodyjson = new JSONObject();
            try {
                bodyjson.put("id", id);
            } catch (JSONException e) { return; }
            RequestBody body = RequestBody.create(MainActivity.JSON, bodyjson.toString());
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(MainActivity.url + "/friends")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + MainActivity.idToken)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject body_res = new JSONObject(response.body().string());
                    if (body_res.has("data"))
                        Data.getInstance().getConversations().add(new Conversation(body_res.getJSONObject("data")));
                }
            } catch (IOException | JSONException e) {}
        }).start();
    }

    public void declineFriendRequest(User user){
        Data.getInstance().getFriends().remove(user);
        mAdapter.notifyDataSetChanged();
        new Thread(() -> {
            JSONObject bodyjson = new JSONObject();
            try {
                bodyjson.put("id", user.id);
            } catch (JSONException e) { return; }
            RequestBody body = RequestBody.create(MainActivity.JSON, bodyjson.toString());
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(MainActivity.url + "/friends/decline")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + MainActivity.idToken)
                    .build();
            try {
                Response response = client.newCall(request).execute();
            } catch (IOException e) {}
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}