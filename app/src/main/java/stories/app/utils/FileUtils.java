package stories.app.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

import stories.app.activities.CreateStoryActivity;

public class FileUtils {

    public enum SUPPORTED_IMAGE_FORMATS { jpeg, jpg, png }
    public enum SUPPORTED_VIDEO_FORMATS { mov, mp4 }
    public static final int MAX_FILE_SIZE_MB = 15;

    public static String getFilePathFromUri(Context context, Uri contentURI){
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentURI, proj, null, null, null);
        if(cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public static boolean isSupportedFormat(String fileExtension){
        return isSupportedImage(fileExtension) || isSupportedVideo(fileExtension);
    }

    public static boolean isSupportedImage(String fileExtension) {
        for (SUPPORTED_IMAGE_FORMATS imageFormat : SUPPORTED_IMAGE_FORMATS.values()) {
            if (imageFormat.name().equals(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSupportedVideo(String fileExtension) {
        for (SUPPORTED_VIDEO_FORMATS videoFormat : SUPPORTED_VIDEO_FORMATS.values()) {
            if (videoFormat.name().equals(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSizeValid(String filePath){
        File file = new File(filePath);
        double fileBytes = file.length();
        double fileMegabytes = (fileBytes / (1024 * 1024));
        return fileMegabytes <= MAX_FILE_SIZE_MB;
    }

    public static void setImageFromVideoUrl(String fileUrl, ImageView imageView) {
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14) {
                mediaMetadataRetriever.setDataSource(fileUrl, new HashMap<String, String>());
            } else {
                mediaMetadataRetriever.setDataSource(fileUrl);
            }

            Bitmap bitmap  = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
            Bitmap.createScaledBitmap(bitmap, 50, 50, false);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
    }

    public static void setImageFromUrl(String url, ImageView imageView, int placeholderResId) {
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

    public static void setImageFromBase64(String base64, ImageView imageView, int placeholderResId) {

        if (base64.length() == 0) {
            imageView.setImageResource(placeholderResId);
            return;
        }

        byte[] decodedProfilePic = Base64.decode(base64, Base64.DEFAULT);
        Bitmap bitmapProfilePic = BitmapFactory.decodeByteArray(decodedProfilePic, 0, decodedProfilePic.length);
        imageView.setImageBitmap(bitmapProfilePic);
    }
}
