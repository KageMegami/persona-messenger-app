package adapter;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import entity.Message;
import com.KageMegami.personaMessenger.R;

import java.nio.file.attribute.AttributeView;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public ArrayList<Message> messages;
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
        ViewGroup viewGroup = (ViewGroup)viewHolder.view.getParent();
        viewGroup.removeAllViews();
        viewHolder.view = new View(viewGroup.getContext());
        viewHolder.view.inflate(viewGroup.getContext(), R.layout.test, viewGroup);
        //viewHolder.view.inflate(viewGroup.getContext(), R.layout.test2, viewGroup);
        viewGroup.addView(viewHolder.view);
        ((TextView)viewGroup.findViewById(R.id.text)).setText(messages.get(position).message);
    }

    @Override
    public int getItemCount() {
        if (messages == null)
            return 0;
        return messages.size();
    }
}

