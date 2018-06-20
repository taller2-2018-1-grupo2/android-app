package stories.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import stories.app.R;
import stories.app.models.Story;

public class QuickStoriesAdapter extends RecyclerView.Adapter<QuickStoriesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Story> mData;
    private LayoutInflater mInflater;
    private QuickStoriesAdapter.ItemClickListener mClickListener;

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
        this.setImageFromUrl(story.fileUrl, holder.storyImageView, R.drawable.story_image_quick_placeholder);
    }

    private void setImageFromUrl(String url, ImageView imageView, int placeholderResId) {
        try {
            if (url != null && url.length() > 0) {
                Picasso.get().load(url).placeholder(placeholderResId).into(imageView);
            } else {
                // Load the placeholder instead
                Picasso.get().load(placeholderResId).placeholder(placeholderResId).into(imageView);
            }
        } catch (Exception e) {
            // The FileUri cannot be parsed.
            // Swallow the exception and load a placeholder
            Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView);
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    public Story getItem(int id) {
        return this.mData.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

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

