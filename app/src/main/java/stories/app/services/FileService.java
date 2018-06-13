package stories.app.services;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import stories.app.models.Story;
import stories.app.utils.Constants;

public class FileService extends BaseService {

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

    private Story finish() throws Exception {
        request.writeBytes(this.crlf);
        request.writeBytes(this.twoHyphens + this.boundary +
                this.twoHyphens + this.crlf);

        request.flush();
        request.close();

        JSONObject result = this.getResponseResult(client);

        if (result == null) {
            return null;
        }

        Story newStory = Story.fromJsonObject(result);
        return newStory;
    }

    public Story uploadFileToStory(String storyId, File file){
        this.client = null;

        try {
            URL url = new URL(Constants.appServerURI + "/stories/" + storyId + "/files");
            this.client = (HttpURLConnection) url.openConnection();

            this.client.setUseCaches(false);
            this.client.setDoOutput(true); // indicates POST method
            this.client.setDoInput(true);

            this.client.setRequestMethod("POST");
            this.client.setRequestProperty("Connection", "Keep-Alive");
            this.client.setRequestProperty("Cache-Control", "no-cache");
            this.client.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + this.boundary);

            request =  new DataOutputStream(this.client.getOutputStream());

            addFormField("filename",file.getName());
            addFilePart("file", file);
            return finish();
        } catch (Exception exception) {
            return null;
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
    }
}
