package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import entity.Friend;
import entity.Message;
import com.KageMegami.personaMessenger.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public List<Message> messages;
    public List<Friend> friends;
    private Fragment fragment;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        //public TextView content;


        public ViewHolder(View view) {
            super(view);
            this.view = view.findViewById(R.id.empty);
            //content = view.findViewById(R.id.content);
        }
    }

    public MessageAdapter(List<Message> dataSet, List<Friend> friends, Fragment frag) {
        messages = dataSet;
        fragment = frag;
        this.friends = friends;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Message message = messages.get(position);
        ViewGroup viewGroup = (ViewGroup)viewHolder.view.getParent();
        viewGroup.removeAllViews();
        viewHolder.view = new View(viewGroup.getContext());
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (message.senderId.equals(uid)) {
            if (message.message.length() <= 6)
                viewHolder.view.inflate(viewGroup.getContext(), R.layout.small_message_right, viewGroup);
            else if (message.message.length() <= 20)
                viewHolder.view.inflate(viewGroup.getContext(), R.layout.medium_message_right, viewGroup);
            else
                viewHolder.view.inflate(viewGroup.getContext(), R.layout.long_message_right, viewGroup);
        } else {
            if (message.message.length() <= 5)
                viewHolder.view.inflate(viewGroup.getContext(), R.layout.small_message_left, viewGroup);
            else if (message.message.length() <= 17)
                viewHolder.view.inflate(viewGroup.getContext(), R.layout.medium_message_left, viewGroup);
            else
                viewHolder.view.inflate(viewGroup.getContext(), R.layout.long_message_left, viewGroup);
        }
        viewGroup.addView(viewHolder.view);
        ((TextView)viewGroup.findViewById(R.id.text)).setText(message.message);
        if (message.senderId.equals(uid))
                return;
        String photoUrl = getUrl(message.senderId);
        if (photoUrl == null)
            return;
        ImageView avatar = ((ImageView)viewGroup.findViewById(R.id.avatar));
        Glide.with(fragment).load(photoUrl).into(avatar);
    }

    @Override
    public int getItemCount() {
        if (messages == null)
            return 0;
        return messages.size();
    }

    public String getUrl(String uid) {
        for (int i = 0; i < friends.size(); i += 1) {
            if (friends.get(i).id.equals(uid))
                return friends.get(i).photoUrl;
        }
        return null;
    }

}

