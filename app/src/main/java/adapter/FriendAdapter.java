package adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import entity.User;

import com.KageMegami.personaMessenger.Data;
import com.KageMegami.personaMessenger.FriendsFragment;
import com.KageMegami.personaMessenger.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    public List<User> friends;
    private FriendsFragment fragment;
    private String uid;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image;
        public View request;
        public ImageView accept;
        public ImageView decline;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            image = view.findViewById(R.id.friend_image);
            request = view.findViewById(R.id.friend_request);
            accept = view.findViewById(R.id.accept);
            decline = view.findViewById(R.id.decline);


        }
    }

    public FriendAdapter(List<User> dataSet, FriendsFragment frag) {
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
        User friend = friends.get(position);
        Glide.with(fragment).load(friend.photoUrl).into(viewHolder.image);
        viewHolder.name.setText(friend.name);
        if (!Data.getInstance().isMyFriend(friend.id)) {
            viewHolder.request.setVisibility(View.VISIBLE);
            viewHolder.accept.setOnClickListener(v -> {
                fragment.acceptFriendRequest(friend.id);
            });
            viewHolder.decline.setOnClickListener(v -> {
                fragment.declineFriendRequest(friend);
            });
        } else
            viewHolder.request.setVisibility(View.GONE);
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
