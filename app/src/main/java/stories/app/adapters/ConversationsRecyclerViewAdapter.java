package stories.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import stories.app.R;
import stories.app.models.Message;

public class ConversationsRecyclerViewAdapter extends RecyclerView.Adapter<ConversationsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Message> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private String type;
    private static String DATE_FORMAT = "dd MMM yyyy HH:mm";

    // data is passed into the constructor
    public ConversationsRecyclerViewAdapter(Context context, ArrayList<Message> data, String type) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.type = type;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int recyclerRow;

        if (type == "chat") {
            recyclerRow = R.layout.recycler_row_messages;
        } else {
            recyclerRow = R.layout.recycler_row_conversations;
        }

        View view = mInflater.inflate(recyclerRow, parent, false);
        return new ConversationsRecyclerViewAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String username, message;
        Long timestamp;

        if (type == "chat") {
            username = mData.get(position).from_username;
            message = mData.get(position).message;
            timestamp = mData.get(position).timestamp;
        } else {
            username = mData.get(position).to_username;
            message = mData.get(position).message;
            timestamp = mData.get(position).timestamp;
        }

        holder.usernameTextView.setText(username);
        holder.messageTextView.setText(message);
        holder.timestampTextView.setText(new SimpleDateFormat(DATE_FORMAT).format(new Date(timestamp)));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView usernameTextView;
        TextView messageTextView;
        TextView timestampTextView;
        LinearLayout conversationRow;

        ViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.message_item_title);
            messageTextView = itemView.findViewById(R.id.message_item_text);
            timestampTextView = itemView.findViewById(R.id.message_item_timestamp);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Message getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
