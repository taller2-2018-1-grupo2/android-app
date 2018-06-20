package stories.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import stories.app.R;
import stories.app.models.Story;

public class StoriesAdapter extends ArrayAdapter<Story> {

    private Context context;

    public StoriesAdapter(Context context, ArrayList<Story> stories) {
        super(context, R.layout.stories_item, stories);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.stories_item, parent, false);
            Story story = this.getItem(position);

            ImageView storyImage = (ImageView) convertView.findViewById(R.id.stories_item_fileUrl);
            this.setImageFromUrl(story.fileUrl, storyImage, R.drawable.story_image_placeholder);

            ImageView storyUserImage = (ImageView) convertView.findViewById(R.id.stories_item_user_pic);
            this.setImageFromUrl(story.profilePic, storyUserImage, R.drawable.profile_placeholder);

            TextView username = (TextView) convertView.findViewById(R.id.stories_item_user_name);
            username.setText(story.username + ": ");

            TextView title = (TextView) convertView.findViewById(R.id.stories_item_title);
            title.setText(story.title);

            TextView description = (TextView) convertView.findViewById(R.id.stories_item_description);
            description.setText(story.description);
        }

        return convertView;
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
}
