package stories.app.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;

import stories.app.R;
import stories.app.models.Story;
import stories.app.services.StoryService;
import stories.app.utils.LocalStorage;

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
        holder.description.setVisibility(story.description.length() > 0 ? View.VISIBLE : View.GONE);

        holder.setLikeCount(story);
        holder.updateLikeButton(story);
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
        TextView likeCount;

        ImageView likeButton;
        ImageView commentsButton;

        ViewHolder(View itemView) {
            super(itemView);
            this.storyImage = itemView.findViewById(R.id.stories_item_fileUrl);
            this.storyUserImage = itemView.findViewById(R.id.stories_item_user_pic);
            this.username =itemView.findViewById(R.id.stories_item_user_name);
            this.title = itemView.findViewById(R.id.stories_item_title);
            this.description = itemView.findViewById(R.id.stories_item_description);
            this.likeCount = itemView.findViewById(R.id.stories_item_like_count);

            this.likeButton = itemView.findViewById(R.id.stories_item_like_button);
        }

        private void setLikeCount(Story story) {
            String likesText = "No likes";

            if (story.likes.length() == 1) {
                likesText = "1 like";
            }

            if (story.likes.length() > 1) {
                likesText = story.likes.length() + " likes";
            }

            this.likeCount.setText(likesText);
        }

        private void updateLikeButton(Story story) {
            String currentUserId = LocalStorage.getUser().id;

            // If the story belongs to the user, disable the button
            if (story.userId.equals(currentUserId)) {
                this.likeButton.setEnabled(false);
                this.likeButton.setImageResource(R.drawable.ic_story_like_disabled);
                return;
            }

            // Change the resource depending if the story was liked by the user or not
            // Otherwise, mark it
            int likeIcon = story.isLikedByUser(currentUserId)
                    ? R.drawable.ic_story_like_liked
                    : R.drawable.ic_story_like_likeable;

            this.likeButton.setImageResource(likeIcon);
            this.likeButton.setEnabled(true);
            this.likeButton.setOnClickListener(new LikeButtonOnClickHandler(story, this));
        }
    }

    protected class LikeButtonOnClickHandler implements View.OnClickListener {
        private Story story;
        private StoriesAdapter.ViewHolder holder;

        LikeButtonOnClickHandler(Story story, StoriesAdapter.ViewHolder holder) {
            this.story = story;
            this.holder = holder;
        }

        public void onClick(View v) {
            String currentUserId = LocalStorage.getUser().id;
            String operation = "";
            if (this.story.isLikedByUser(currentUserId)) {
                this.story.removeLike(currentUserId);
                operation = "remove";
            } else {
                this.story.addLike(currentUserId);
                operation = "add";
            }

            // Update story in the backend
            new UpdateStoryLikesToUserTask(this.holder).execute(this.story.id, currentUserId, operation);
        }
    }

    protected class UpdateStoryLikesToUserTask extends AsyncTask<String, Void, Story> {
        private StoryService storyService;
        private StoriesAdapter.ViewHolder holder;

        public UpdateStoryLikesToUserTask(StoriesAdapter.ViewHolder holder) {
            this.storyService = new StoryService();
            this.holder = holder;
        }

        protected void onPreExecute() {
            // Disable click handler in Like button
            this.holder.likeButton.setImageResource(R.drawable.ic_story_like_disabled);
            this.holder.likeButton.setEnabled(false);
        }

        protected Story doInBackground(String... params) {
            return storyService.updateLikes(params[0], params[1], params[2]);
        }

        protected void onPostExecute(Story newStory) {

            // Update like button (UI) with new story
            this.holder.setLikeCount(newStory);
            this.holder.updateLikeButton(newStory);
        }
    }
}
