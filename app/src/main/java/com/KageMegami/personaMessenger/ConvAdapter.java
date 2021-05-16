package com.KageMegami.personaMessenger;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class ConvAdapter extends RecyclerView.Adapter<ConvAdapter.ViewHolder> {

    public JSONObject[] conversations;
    private Fragment fragment;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            image = view.findViewById(R.id.conversationImage);

        }
    }

    public ConvAdapter(JSONObject[] dataSet, Fragment frag) {
        conversations = dataSet;
        fragment = frag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.conv, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        try {
            viewHolder.name.setText(conversations[position].getString("name"));
            Glide.with(fragment).load(conversations[position].getString("photoUrl")).into(viewHolder.image);
            viewHolder.itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                try {
                    bundle.putString("conversation_id", conversations[position].getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_messenger, bundle);
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (conversations == null)
            return 0;
        return conversations.length;
    }
}
