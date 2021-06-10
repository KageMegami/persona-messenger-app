package com.KageMegami.personaMessenger;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapter.ResultAdapter;
import entity.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddFriendActivity extends AppCompatActivity {
    protected RecyclerView mRecyclerView;
    protected ResultAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private List<User> results;
    private ImageView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        results = new ArrayList<>();
        setContentView(R.layout.activity_add_friend);
        mRecyclerView = findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ResultAdapter(results, this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        EditText editText = findViewById(R.id.search_users);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(editText.getText().toString().trim());
                return true;
            }
            return false;
        });
        loading = findViewById(R.id.loadingGif);
        Glide.with(this).load(R.drawable.loading).into(loading);
        loading.setVisibility(View.GONE);
    }

    public void sendFriendRequest(User user){

        new Thread(() -> {
            JSONObject bodyjson = new JSONObject();
            try {
                bodyjson.put("id", user.id);
            } catch (JSONException e) { return; }
            RequestBody body = RequestBody.create(MainActivity.JSON, bodyjson.toString());
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(MainActivity.url + "/friends/request")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + MainActivity.idToken)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Data.getInstance().myFriends.add(user.id);
                        results.remove(user);
                        mAdapter.notifyDataSetChanged();
                        mLayoutManager.scrollToPosition(0);
                        Toast toast = Toast.makeText(this, "An invitation has been send to " + user.name, Toast.LENGTH_LONG);
                        toast.show();
                    });
                }
            } catch (IOException e) {}
        }).start();
    }

    public void performSearch(final String input) {
        if (input.length() == 0)
                return;
        results.clear();
        mAdapter.notifyDataSetChanged();
        loading.setVisibility(View.VISIBLE);
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(MainActivity.url + "/users/?q=" + input)
                    .method("GET", null)
                    .addHeader("Authorization", "Bearer " + MainActivity.idToken)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONArray users = new JSONObject(response.body().string()).getJSONArray("data");
                    for (int i = 0; i < users.length(); i += 1) {
                        JSONObject result = users.getJSONObject(i);
                        if (!Data.getInstance().isMyFriend(result.getString("id")))
                            results.add(new User(users.getJSONObject(i)));
                    }
                }
            } catch (IOException | JSONException e) {}
            runOnUiThread(() -> {
                loading.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
                mLayoutManager.scrollToPosition(0);
            });
        }).start();
    }

}