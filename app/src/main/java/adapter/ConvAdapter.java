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

public class ConvAdapter extends RecyclerView.Adapter<ConvAdapter.ViewHolder> {

    public List<Conversation> conversations;
    public List<Friend> users;
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

    public ConvAdapter(List<Conversation> dataSet, List<Friend> users, Fragment frag) {
        conversations = dataSet;
        fragment = frag;
        this.users = users;
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.conv, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Conversation conv = conversations.get(position);
        if (!conv.isGroup) {
            Friend friend = getFriend(conv);
            if (friend == null)
                return;
            Glide.with(fragment).load(friend.photoUrl).into(viewHolder.image);
            viewHolder.name.setText(friend.name);
        } else {
            Glide.with(fragment).load(conv.photoUrl).into(viewHolder.image);
            viewHolder.name.setText(conv.name);
        }
        viewHolder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("conversation_id", conv.id);
            Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_messenger, bundle);
        });
    }

    @Override
    public int getItemCount() {
        if (conversations == null)
            return 0;
        return conversations.size();
    }

    public Friend getFriend(Conversation conv) {
        String friendId = conv.users[0].equals(uid) ? conv.users[1] : conv.users[0];
        for (int i = 0; i < users.size(); i += 1) {
            if (users.get(i).id.equals(friendId))
                return users.get(i);
        }
        return null;
    }
}
