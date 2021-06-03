package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import entity.Message;
import com.KageMegami.personaMessenger.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public ArrayList<Message> messages;
    private Fragment fragment;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView content;

        public ViewHolder(View view) {
            super(view);
            content = view.findViewById(R.id.content);
        }
    }

    public MessageAdapter(ArrayList<Message> dataSet, Fragment frag) {
        messages = dataSet;
        fragment = frag;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.content.setText(messages.get(position).message);
    }

    @Override
    public int getItemCount() {
        if (messages == null)
            return 0;
        return messages.size();
    }
}

