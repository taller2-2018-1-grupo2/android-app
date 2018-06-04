package stories.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import stories.app.R;
import stories.app.models.Story;

public class StoriesAdapter extends ArrayAdapter<Story> {
   public StoriesAdapter(Context context, ArrayList<Story> stories) {
        super(context, R.layout.stories_item, stories);
   }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.stories_item, parent, false);

            Story story = this.getItem(position);

            TextView title = (TextView) convertView.findViewById(R.id.stories_item_title);
            title.setText(story.title);

            TextView description = (TextView) convertView.findViewById(R.id.stories_item_description);
            description.setText(story.description);
        }

        return convertView;
    }
}