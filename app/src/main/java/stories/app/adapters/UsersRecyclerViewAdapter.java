package stories.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import stories.app.R;
import stories.app.utils.Base64UtilityClass;

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Pair<String,String>> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private String type;

    // data is passed into the constructor
    public UsersRecyclerViewAdapter(Context context, ArrayList<Pair<String,String>> data, String type) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.type = type;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int recyclerRow;

        switch (type) {
            case "users":  recyclerRow = R.layout.recycler_row_users;
                break;
            case "friends":  recyclerRow = R.layout.recycler_row_friends;
                break;
            case "received":  recyclerRow = R.layout.recycler_row_friendship_requests_received;
                break;
            default: recyclerRow = R.layout.recycler_row_friendship_requests_sent;
                break;
        }

        View view = mInflater.inflate(recyclerRow, parent, false);
        return new UsersRecyclerViewAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String username = mData.get(position).first;
        holder.usernameTextView.setText(username);

        String profilePicString = mData.get(position).second;
        if (!profilePicString.isEmpty()) {
            holder.profilePicImageView.setImageBitmap(Base64UtilityClass.toBitmap(profilePicString));
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView profilePicImageView;
        TextView usernameTextView;
        ImageView actionButton;

        ViewHolder(View itemView) {
            super(itemView);
            profilePicImageView = itemView.findViewById(R.id.recycler_view_row_profile_pic);
            usernameTextView = itemView.findViewById(R.id.username);

            switch (type) {
                case "users":
                    actionButton = itemView.findViewById(R.id.add_user);
                    actionButton.setOnClickListener(this);
                    break;
                case "friends":
                    break;
                case "received":
                    actionButton = itemView.findViewById(R.id.accept_friendship_request);
                    actionButton.setOnClickListener(this);
                    break;
                default:
                    actionButton = itemView.findViewById(R.id.delete_friendship_request);
                    actionButton.setOnClickListener(this);
                    break;
            }
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Pair<String, String> getItem(int id) {
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
