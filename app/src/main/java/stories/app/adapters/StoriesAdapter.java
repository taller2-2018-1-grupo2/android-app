package stories.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import stories.app.R;
import stories.app.activities.LogInActivity;
import stories.app.models.Story;
import stories.app.models.User;
import stories.app.models.responses.ServiceResponse;
import stories.app.services.StoryService;
import stories.app.utils.FileDialog;
import stories.app.utils.FileUtils;
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
        final Story story = this.mData.get(position);

        if (story.fileUrl != null && story.fileUrl.length() > 0 && story.fileUrl.endsWith(".mp4")) {
            FileUtils.setImageFromVideoUrl(story.fileUrl, holder.storyImage);
            holder.storyImage.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     FileDialog.showVideoDialog(view.getContext(), story.fileUrl);
                 }
            });
        } else {
            FileUtils.setImageFromUrl(story.fileUrl, holder.storyImage, R.drawable.story_image_placeholder);
        }

        FileUtils.setImageFromBase64(story.profilePic, holder.storyUserImage, R.drawable.profile_placeholder);

        holder.username.setText(story.username + ": ");
        holder.title.setText(story.title);

        holder.description.setText(story.description);
        holder.description.setVisibility(story.description.length() > 0 ? View.VISIBLE : View.GONE);

        // Reactions
        holder.updateReactions(story);

        // Comments
        holder.updateComments(story.comments);

        // New comment
        User currentUser = LocalStorage.getUser();
        FileUtils.setImageFromBase64(currentUser.profilePic, holder.newCommentUserPic, R.drawable.profile_placeholder);
        holder.postCommentButton.setOnClickListener(new PostCommentButtonOnClickHandler(story, holder));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView storyImage;
        ImageView storyUserImage;
        TextView username;
        TextView title;
        TextView description;

        ImageView likeButton;
        TextView likeCount;
        ImageView dislikeButton;
        TextView dislikeCount;
        ImageView funButton;
        TextView funCount;
        ImageView boringButton;
        TextView boringCount;

        TextView comments;
        ImageView newCommentUserPic;
        TextView newComment;
        ImageView postCommentButton;

        ViewHolder(View itemView) {
            super(itemView);
            this.storyImage = itemView.findViewById(R.id.stories_item_fileUrl);
            this.storyUserImage = itemView.findViewById(R.id.stories_item_user_pic);
            this.username =itemView.findViewById(R.id.stories_item_user_name);
            this.title = itemView.findViewById(R.id.stories_item_title);
            this.description = itemView.findViewById(R.id.stories_item_description);

            this.likeButton = itemView.findViewById(R.id.stories_item_like_button);
            this.likeCount = itemView.findViewById(R.id.stories_item_like_count);
            this.dislikeButton = itemView.findViewById(R.id.stories_item_dislike_button);
            this.dislikeCount = itemView.findViewById(R.id.stories_item_dislike_count);
            this.funButton = itemView.findViewById(R.id.stories_item_fun_button);
            this.funCount = itemView.findViewById(R.id.stories_item_fun_count);
            this.boringButton = itemView.findViewById(R.id.stories_item_boring_button);
            this.boringCount = itemView.findViewById(R.id.stories_item_boring_count);

            this.comments = itemView.findViewById(R.id.stories_item_comments);
            this.newCommentUserPic = itemView.findViewById(R.id.stories_item_new_comment_userpic);
            this.newComment = itemView.findViewById(R.id.stories_item_new_comment_text);
            this.postCommentButton = itemView.findViewById(R.id.stories_item_new_comment_button);
        }

        public void updateReactions(Story story) {
            try {
                String currentUserId = LocalStorage.getUser().id;
                Integer likes = 0;
                Integer dislikes = 0;
                Integer funs = 0;
                Integer borings = 0;
                boolean userLiked = false;
                boolean userDisliked = false;
                boolean userSetFun = false;
                boolean userSetBoring = false;

                for (int i = 0 ; i < story.reactions.length(); i++) {
                    JSONObject reaction = story.reactions.getJSONObject(i);
                    String reactionUserId = reaction.getString("user_id");
                    String reactionType = reaction.getString("type");

                    switch(reactionType) {
                        case "like":
                            userLiked = userLiked || reactionUserId.equals(currentUserId);
                            likes += 1;
                            break;
                        case "dislike":
                            userDisliked = userDisliked || reactionUserId.equals(currentUserId);
                            dislikes += 1;
                            break;
                        case "fun":
                            userSetFun = userSetFun || reactionUserId.equals(currentUserId);
                            funs += 1;
                            break;
                        case "boring":
                        default:
                            userSetBoring = userSetBoring || reactionUserId.equals(currentUserId);
                            borings += 1;
                            break;
                    }
                }

                // Like
                this.likeCount.setText(likes.toString());
                if (story.userId.equals(currentUserId)) {
                    this.likeButton.setEnabled(false);
                    this.likeButton.setImageResource(R.drawable.ic_story_like_disabled);
                } else {
                    this.likeButton.setEnabled(true);
                    this.likeButton.setImageResource(userLiked ? R.drawable.ic_story_like_set : R.drawable.ic_story_like);
                    this.likeButton.setOnClickListener(new ReactionButtonClickListener("like", userLiked ? "remove" : "add",this, story, currentUserId));
                }

                // Dislike
                this.dislikeCount.setText(dislikes.toString());
                if (story.userId.equals(currentUserId)) {
                    this.dislikeButton.setEnabled(false);
                    this.dislikeButton.setImageResource(R.drawable.ic_story_dislike_disabled);
                } else {
                    this.dislikeButton.setEnabled(true);
                    this.dislikeButton.setImageResource(userDisliked ? R.drawable.ic_story_dislike_set : R.drawable.ic_story_dislike);
                    this.dislikeButton.setOnClickListener(new ReactionButtonClickListener("dislike", userDisliked ? "remove" : "add",this, story, currentUserId));
                }

                // Fun
                this.funCount.setText(funs.toString());
                if (story.userId.equals(currentUserId)) {
                    this.funButton.setEnabled(false);
                    this.funButton.setImageResource(R.drawable.ic_story_fun_disabled);
                } else {
                    this.funButton.setEnabled(true);
                    this.funButton.setImageResource(userSetFun ? R.drawable.ic_story_fun_set : R.drawable.ic_story_fun);
                    this.funButton.setOnClickListener(new ReactionButtonClickListener("fun", userSetFun ? "remove" : "add",this, story, currentUserId));
                }

                // Boring
                this.boringCount.setText(borings.toString());
                if (story.userId.equals(currentUserId)) {
                    this.boringButton.setEnabled(false);
                    this.boringButton.setImageResource(R.drawable.ic_story_boring_disabled);
                } else {
                    this.boringButton.setEnabled(true);
                    this.boringButton.setImageResource(userSetBoring ? R.drawable.ic_story_boring_set : R.drawable.ic_story_boring);
                    this.boringButton.setOnClickListener(new ReactionButtonClickListener("boring", userSetBoring ? "remove" : "add",this, story, currentUserId));
                }
            }
             catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void updateComments(JSONArray comments) {
            String postComments = "";
            for(int i = 0; i < comments.length(); i++) {
                try {
                    JSONObject comment = comments.getJSONObject(i);
                    String username = comment.getString("username");
                    String text = comment.getString("text");

                    postComments += "<b>" + username + ":</b> " + text;

                    if (i + 1 < comments.length()) {
                        postComments += "<br>";
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            this.comments.setVisibility(comments.length() > 0 ? View.VISIBLE : View.GONE);
            this.comments.setText(Html.fromHtml(postComments));
        }
    }

    protected class ReactionButtonClickListener implements View.OnClickListener {
        String type;
        String operation;
        ViewHolder holder;
        Story story;
        String currentUserId;

        ReactionButtonClickListener(String type, String operation, ViewHolder holder, Story story, String currentUserId) {
            this.type = type;
            this.operation = operation;
            this.holder = holder;
            this.story = story;
            this.currentUserId = currentUserId;
        }

        @Override
        public void onClick(View view) {

            // Disable click handler in reaction button
            ImageView button = (ImageView) view;
            button.setEnabled(false);

            new UpdateReactionTask(holder, view.getContext()).execute(type, this.story.id, this.currentUserId, this.operation);
        }
    }

    protected class UpdateReactionTask extends AsyncTask<String, Void, ServiceResponse<Story>> {
        private StoryService storyService;
        private StoriesAdapter.ViewHolder holder;
        private Context context;

        public UpdateReactionTask(StoriesAdapter.ViewHolder holder, Context context) {
            this.storyService = new StoryService();
            this.holder = holder;
            this.context = context;
        }

        protected ServiceResponse<Story> doInBackground(String... params) {
            // String type, String storyId, String userId, String operation
            return storyService.updateReaction(params[0], params[1], params[2], params[3]);
        }

        protected void onPostExecute(ServiceResponse<Story> response) {
            ServiceResponse.ServiceStatusCode statusCode = response.getStatusCode();
            if (statusCode == ServiceResponse.ServiceStatusCode.UNAUTHORIZED) {
                Intent navigationIntent = new Intent(context, LogInActivity.class);
                context.startActivity(navigationIntent);
                return;
            }

            Story newStory = response.getServiceResponse();
            this.holder.updateReactions(newStory);
        }
    }

    protected class PostCommentButtonOnClickHandler implements View.OnClickListener {
        private Story story;
        private StoriesAdapter.ViewHolder holder;

        PostCommentButtonOnClickHandler(Story story, StoriesAdapter.ViewHolder holder) {
            this.story = story;
            this.holder = holder;
        }

        public void onClick(View v) {
            User currentUser = LocalStorage.getUser();
            String commentText = this.holder.newComment.getText().toString();

            if (commentText.length() > 0) {
                // Update story in the backend
                new AddStoryCommentTask(this.holder, v.getContext()).execute(this.story.id, currentUser, commentText);
            }
        }
    }

    protected class AddStoryCommentTask extends AsyncTask<Object, Void, ServiceResponse<Story>> {
        private StoryService storyService;
        private StoriesAdapter.ViewHolder holder;
        private Context context;

        public AddStoryCommentTask(StoriesAdapter.ViewHolder holder, Context context) {
            this.storyService = new StoryService();
            this.holder = holder;
            this.context = context;
        }

        protected void onPreExecute() {
            // Disable Post comment elements in the UI
            this.holder.newComment.setEnabled(false);
            this.holder.postCommentButton.setEnabled(false);
            this.holder.postCommentButton.setImageResource(R.drawable.ic_story_comment_disabled);
        }

        protected ServiceResponse<Story> doInBackground(Object... params) {
            String storyId = (String)params[0];
            User user = (User)params[1];
            String commentText = (String)params[2];
            return storyService.addComment(storyId, user, commentText);
        }

        protected void onPostExecute(ServiceResponse<Story> response) {
            ServiceResponse.ServiceStatusCode statusCode = response.getStatusCode();

            if (statusCode == ServiceResponse.ServiceStatusCode.SUCCESS){
                Story newStory = response.getServiceResponse();
                this.holder.updateComments(newStory.comments);

                // Enable Post comment elements in the UI and clean up the field
                this.holder.newComment.setText("");
                this.holder.newComment.setEnabled(true);
                this.holder.postCommentButton.setEnabled(true);
                this.holder.postCommentButton.setImageResource(R.drawable.ic_story_comment);
            } else if (statusCode == ServiceResponse.ServiceStatusCode.UNAUTHORIZED) {
                Intent navigationIntent = new Intent(context, LogInActivity.class);
                context.startActivity(navigationIntent);
            }
        }
    }
}
