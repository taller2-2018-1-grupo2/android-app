package stories.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import stories.app.R;
import stories.app.models.Story;

public class QuickStoriesAdapter extends RecyclerView.Adapter<QuickStoriesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Story> mData;
    private LayoutInflater mInflater;
    private QuickStoriesAdapter.ItemClickListener mClickListener;

    // Data is passed into the constructor
    public QuickStoriesAdapter(Context context, ArrayList<Story> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int recyclerRow = R.layout.stories_item_quick;
        View view = mInflater.inflate(recyclerRow, parent, false);
        return new QuickStoriesAdapter.ViewHolder(view);
    }


    // Binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(QuickStoriesAdapter.ViewHolder holder, int position) {
        Story story = mData.get(position);
        if (story.fileUrl != null && story.fileUrl.length() > 0) {
            try {
                Picasso
                    .get()
                    .load(story.fileUrl)
                    .placeholder(R.drawable.story_image_quick_placeholder)
                    .into(holder.storyImageView);
            } catch (Exception e) {
                // The FileUri cannot be parsed.
                // Swallow the exception and load a placeholder
                Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(holder.storyImageView);
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return this.mData.size();
    }

    // convenience method for getting data at click position
    public Story getItem(int id) {
        return this.mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView storyImageView;

        ViewHolder(View itemView) {
            super(itemView);
            this.storyImageView = (ImageView)itemView.findViewById(R.id.stories_item_quick_fileUrl);
        }

        @Override
        public void onClick(View view) {
            // do nothing
        }
    }
}

