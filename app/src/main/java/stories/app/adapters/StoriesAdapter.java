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

            ImageView imageView = (ImageView) convertView.findViewById(R.id.stories_item_fileUrl);
            Story story = this.getItem(position);

            if (story.fileUrl != null && story.fileUrl.length() > 0) {

                try {
                    Picasso.get().load(story.fileUrl).into(imageView);
                } catch (Exception e) {
                    // The FileUri cannot be parsed.
                    // Swallow the exception and load a placeholder
                    Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView);
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }

            TextView title = (TextView) convertView.findViewById(R.id.stories_item_title);
            title.setText(story.title);

            TextView description = (TextView) convertView.findViewById(R.id.stories_item_description);
            description.setText(story.description);
        }

        return convertView;
    }
}
