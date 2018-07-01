package stories.app.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import stories.app.R;

public class FileDialog {

    public static void showImageDialog(View view, Drawable drawable) {
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        View storyDialog = inflater.inflate(R.layout.stories_dialog, null);
        final Dialog dialog = new Dialog(view.getContext(), android.R.style.Theme_Black_NoTitleBar);

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

    public static void showVideoDialog(View view, String videoUrl) {
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        View storyDialog = inflater.inflate(R.layout.stories_dialog, null);
        final Dialog dialog = new Dialog(view.getContext(), android.R.style.Theme_Black_NoTitleBar);

        final VideoView videoView = (VideoView)storyDialog.findViewById(R.id.stories_dialog_video);
        ImageView imageView = (ImageView)storyDialog.findViewById(R.id.stories_dialog_image);

        imageView.setVisibility(View.INVISIBLE);
        videoView.setVisibility(View.VISIBLE);

        MediaController mediaController = new MediaController(view.getContext());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse(videoUrl));


        videoView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                dialog.cancel();
            }
        });

        final Snackbar snackbar = Snackbar.make(storyDialog, "Cargando el video...", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        dialog.show();
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                snackbar.dismiss();

            }
        });

        dialog.setContentView(storyDialog);
        snackbar.show();
        dialog.show();
        videoView.start();
    }
}
