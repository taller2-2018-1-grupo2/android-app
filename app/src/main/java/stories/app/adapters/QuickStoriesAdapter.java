package stories.app.adapters;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import stories.app.R;
import stories.app.models.Story;
import stories.app.utils.FileDialog;
import stories.app.utils.FileUtils;

public class QuickStoriesAdapter extends RecyclerView.Adapter<QuickStoriesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Story> mData;
    private LayoutInflater mInflater;

    public QuickStoriesAdapter(Context context, ArrayList<Story> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public QuickStoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.stories_item_quick, parent, false);
        return new QuickStoriesAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return this.mData.size();
    }

    @Override
    public void onBindViewHolder(QuickStoriesAdapter.ViewHolder holder, int position) {
        Story story = this.mData.get(position);

        if (story.fileUrl != null && story.fileUrl.length() > 0 && story.fileUrl.endsWith(".mp4")) {
            FileUtils.setImageFromVideoUrl(story.fileUrl, holder.storyImageView);
        } else {
            // It's an image
            FileUtils.setImageFromUrl(story.fileUrl, holder.storyImageView, R.drawable.story_image_placeholder);
        }

        holder.setStory(story);
        holder.storyImageView.setOnClickListener(holder);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView storyImageView;
        Story story;

        ViewHolder(View itemView) {
            super(itemView);
            this.storyImageView = (ImageView)itemView.findViewById(R.id.stories_item_quick_fileUrl);
        }

        private void setStory(Story story) {
            this.story = story;
        }

        @Override
        public void onClick(View view) {
            if (story.fileUrl != null && story.fileUrl.length() > 0 && story.fileUrl.endsWith(".mp4")) {
                FileDialog.showVideoDialog(view.getContext(), story.fileUrl);
            } else {
                FileDialog.showImageDialog(view.getContext(), storyImageView.getDrawable());
            }
        }
    }
}

