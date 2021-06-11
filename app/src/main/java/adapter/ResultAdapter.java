package adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import entity.User;

import com.KageMegami.personaMessenger.AddFriendActivity;
import com.KageMegami.personaMessenger.MainActivity;
import com.KageMegami.personaMessenger.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    public List<User> results;
    private AddFriendActivity context;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView image;
        public ImageView add_friend;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            image = view.findViewById(R.id.friend_image);
            add_friend = view.findViewById(R.id.add_friend);
        }
    }

    public ResultAdapter(List<User> dataSet, AddFriendActivity context) {
        results = dataSet;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.result, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        User user = results.get(position);
        Glide.with(context).load(user.photoUrl).into(viewHolder.image);
        viewHolder.name.setText(user.name);
        viewHolder.add_friend.setOnClickListener(v -> {
            context.sendFriendRequest(user);
        });
    }

    @Override
    public int getItemCount() {
        if (results == null)
            return 0;
        return results.size();
    }
}
