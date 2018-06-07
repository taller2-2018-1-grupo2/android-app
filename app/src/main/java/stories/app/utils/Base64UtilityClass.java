package stories.app.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Base64UtilityClass {

    public static String toBase64String(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String resultingString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return resultingString;
    }

    public static Bitmap toBitmap(String base64String) {
        byte[] imageBytes = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap resultingBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return resultingBitmap;
    }
}
