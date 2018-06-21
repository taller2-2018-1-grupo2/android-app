package stories.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import stories.app.R;
import stories.app.models.Story;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Story> mData;
    private LayoutInflater mInflater;

    public StoriesAdapter(Context context, ArrayList<Story> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public StoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.stories_item, parent, false);
        return new StoriesAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return this.mData.size();
    }

    @Override
    public void onBindViewHolder(StoriesAdapter.ViewHolder holder, int position) {
        Story story = this.mData.get(position);

        this.setImageFromUrl(story.fileUrl, holder.storyImage, R.drawable.story_image_placeholder);
        this.setImageFromUrl(story.fileUrl, holder.storyUserImage, R.drawable.profile_placeholder);
        holder.username.setText(story.username + ": ");
        holder.title.setText(story.title);
        holder.description.setText(story.description);
    }

    private void setImageFromUrl(String url, ImageView imageView, int placeholderResId) {
        try {

            if (url != null && url.length() > 0) {
                Picasso
                    .get()
                    .load(url)
                    .placeholder(placeholderResId)
                    .into(imageView);
            } else {
                // Load the placeholder instead
                Picasso
                    .get()
                    .load(placeholderResId)
                    .placeholder(placeholderResId)
                    .into(imageView);
            }
        } catch (Exception e) {
            // The FileUri cannot be parsed.
            // Swallow the exception and load a placeholder
            Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView);
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView storyImage;
        ImageView storyUserImage;
        TextView username;
        TextView title;
        TextView description;

        ViewHolder(View itemView) {
            super(itemView);
            this.storyImage = itemView.findViewById(R.id.stories_item_fileUrl);
            this.storyUserImage = itemView.findViewById(R.id.stories_item_user_pic);
            this.username =itemView.findViewById(R.id.stories_item_user_name);
            this.title = itemView.findViewById(R.id.stories_item_title);
            this.description = itemView.findViewById(R.id.stories_item_description);
        }
    }
}
