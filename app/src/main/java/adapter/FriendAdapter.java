package adapter;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import entity.Conversation;
import entity.Friend;
import com.KageMegami.personaMessenger.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    public List<Friend> friends;
    private Fragment fragment;
    private String uid;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            image = view.findViewById(R.id.conversation_image);

        }
    }

    public FriendAdapter(List<Friend> dataSet, Fragment frag) {
        friends = dataSet;
        fragment = frag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.friend, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Friend friend = friends.get(position);
        Glide.with(fragment).load(friend.photoUrl).into(viewHolder.image);
        viewHolder.name.setText(friend.name);
      /*  viewHolder.itemView.setOnClickListener(v -> {
        });*/
    }

    @Override
    public int getItemCount() {
        if (friends == null)
            return 0;
        return friends.size();
    }
}
