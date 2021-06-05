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
        public View content;
        public View link;

        public ViewHolder(View view) {
            super(view);
            this.content = view.findViewById(R.id.content);
            this.link = view.findViewById(R.id.link);
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
        int newPosition = messages.size() - position - 1;
        Message message = messages.get(newPosition);
        ViewGroup viewGroup = (ViewGroup)viewHolder.content.getParent();
        viewGroup.removeAllViews();
        viewHolder.link = new View(viewGroup.getContext());
        viewHolder.content = new View(viewGroup.getContext());

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //add link
        viewHolder.link.inflate(viewGroup.getContext(), R.layout.link, viewGroup);
        viewGroup.addView(viewHolder.link);
        ImageView link = ((ImageView)viewGroup.findViewById(R.id.link));
        Glide.with(fragment).load(getLink(message, uid, newPosition)).into(link);


        viewHolder.content.inflate(viewGroup.getContext(), selectMessageLayout(message, uid), viewGroup);

        viewGroup.addView(viewHolder.content);
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


    public int selectMessageLayout(Message message, String uid) {
        if (message.senderId.equals(uid)) {
            if (message.message.length() <= 6)
                return R.layout.small_message_right;
            if (message.message.length() <= 20)
                return R.layout.medium_message_right;
            return R.layout.long_message_right;
         }
        if (message.message.length() <= 5)
           return R.layout.small_message_left;
        if (message.message.length() <= 17)
            return R.layout.medium_message_left;
        return R.layout.long_message_left;
    }

    public int getLink(Message message, String uid, int position) {
        if (position == 0) {
            //tmp
            if (message.senderId.equals(uid))
                return R.drawable.link_left_to_right;
            return R.drawable.link_right_to_left;
        }
        Message prev = messages.get(position - 1);
        if (message.senderId.equals(uid) && !prev.senderId.equals(uid))
            return R.drawable.link_left_to_right;
        if (!message.senderId.equals(uid) && prev.senderId.equals(uid))
            return  R.drawable.link_right_to_left;
        if (!message.senderId.equals(uid) && !prev.senderId.equals(uid)){
            if (position % 2 == 0)
                return R.drawable.link_left3;
            return R.drawable.link_left2;
        }
        // mort a link_left2
        if (position % 2 == 0)
            return R.drawable.link_right1;
        return R.drawable.link_right2;
    }

    public String getUrl(String uid) {
        for (int i = 0; i < friends.size(); i += 1) {
            if (friends.get(i).id.equals(uid))
                return friends.get(i).photoUrl;
        }
        return null;
    }

}

