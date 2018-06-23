package stories.app.services;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import stories.app.exceptions.UserUnauthorizedException;
import stories.app.models.Story;
import stories.app.models.responses.ServiceResponse;
import stories.app.utils.Constants;
import stories.app.utils.LocalStorage;

public class FileService{

    private HttpURLConnection client;
    private DataOutputStream request;
    private final String boundary =  "*****";
    private final String crlf = "\r\n";
    private final String twoHyphens = "--";

    private void addFormField(String name, String value)throws IOException  {
        request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"" + name + "\""+ this.crlf);
        request.writeBytes("Content-Type: text/plain; charset=UTF-8" + this.crlf);
        request.writeBytes(this.crlf);
        request.writeBytes(value+ this.crlf);
        request.flush();
    }

    private byte[] loadFileAsBytesArray(File file) throws Exception {
        int length = (int) file.length();
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
        byte[] bytes = new byte[length];
        reader.read(bytes, 0, length);
        reader.close();
        return bytes;
    }

    private void addFilePart(String fieldName, File uploadFile)
            throws Exception {
        String fileName = uploadFile.getName();
        request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"" +
                fieldName + "\";filename=\"" +
                fileName + "\"" + this.crlf);
        request.writeBytes(this.crlf);

        byte[] bytes = loadFileAsBytesArray(uploadFile);
        request.write(bytes);
    }

    private ServiceResponse<Story> finish() throws Exception {
        String response ="";

        request.writeBytes(this.crlf);
        request.writeBytes(this.twoHyphens + this.boundary +
                this.twoHyphens + this.crlf);

        request.flush();
        request.close();

        // checks server's status code first
        int status = client.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            InputStream responseStream = new
                    BufferedInputStream(client.getInputStream());

            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            response = stringBuilder.toString();
            client.disconnect();
        } else if (status == HttpURLConnection.HTTP_UNAUTHORIZED) {
            throw new UserUnauthorizedException("The user token has expired");
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }

        JSONObject jsonResponse = new JSONObject(response);
        Story newStory = Story.fromJsonObject(jsonResponse);
        return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.SUCCESS, newStory);
    }

    public ServiceResponse<Story> uploadFileToStory(String storyId, File file){
        this.client = null;

        try {
            URL url = new URL(Constants.appServerURI + "/stories/" + storyId + "/files");
            this.client = (HttpURLConnection) url.openConnection();

            this.client.setUseCaches(false);
            this.client.setDoOutput(true); // indicates POST method
            this.client.setDoInput(true);

            this.client.setRequestMethod("POST");
            String token = String.format("Bearer %s", LocalStorage.getToken());
            this.client.setRequestProperty("Authorization", token);
            this.client.setRequestProperty("Connection", "Keep-Alive");
            this.client.setRequestProperty("Cache-Control", "no-cache");
            this.client.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + this.boundary);

            request = new DataOutputStream(this.client.getOutputStream());

            addFormField("filename", file.getName());
            addFilePart("file", file);
            return finish();
        } catch (UserUnauthorizedException exception) {
            Log.e(exception.getClass().getName(), exception.getMessage(), exception);
            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.UNAUTHORIZED);
        } catch (Exception exception) {
            Log.e(exception.getClass().getName(), exception.getMessage(), exception);
            return new ServiceResponse<>(ServiceResponse.ServiceStatusCode.ERROR);
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
    }
}
