package stories.app.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import stories.app.R;

public class FileDialog {

    public static void showImageDialog(Context context, Drawable drawable) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View storyDialog = inflater.inflate(R.layout.stories_dialog, null);
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar);

        VideoView videoView = (VideoView)storyDialog.findViewById(R.id.stories_dialog_video);
        ImageView imageView = (ImageView)storyDialog.findViewById(R.id.stories_dialog_image);

        imageView.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.INVISIBLE);

        imageView.setImageDrawable(drawable);
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                dialog.cancel();
            }
        });

        dialog.setContentView(storyDialog);
        dialog.show();
    }

    public static void showVideoDialog(Context context, String videoUrl) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View storyDialog = inflater.inflate(R.layout.stories_dialog, null);
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar);

        VideoView videoView = (VideoView)storyDialog.findViewById(R.id.stories_dialog_video);
        ImageView imageView = (ImageView)storyDialog.findViewById(R.id.stories_dialog_image);

        imageView.setVisibility(View.INVISIBLE);
        videoView.setVisibility(View.VISIBLE);

        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse(videoUrl));
        videoView.start();

        videoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                dialog.cancel();
            }
        });

        dialog.setContentView(storyDialog);
        dialog.show();
    }
}
