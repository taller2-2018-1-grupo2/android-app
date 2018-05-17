package stories.app.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import stories.app.utils.Constants;

public class FriendshipRequestsService {

    private String URL = Constants.appServerURI;

    public ArrayList<String> getUsers(String partialUsername, String userID) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(URL + "/users/search/" + userID + "/" + partialUsername);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");

            client.connect();

            BufferedReader br;

            if (200 <= client.getResponseCode() && client.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(client.getErrorStream()));
            }

            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            String result = sb.toString();

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("found_users");

            ArrayList<String> usernames = new ArrayList<String>();

            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject itemObject = jsonArray.getJSONObject(i);
                // Pulling items from the array
                usernames.add(itemObject.getString("username"));
            }

            return usernames;

        } catch(MalformedURLException error) {
            //Handles an incorrectly entered URL
            ArrayList<String> result = new ArrayList<String>();
            result.add("MalformedURLException");
            return result;
        }
        catch(SocketTimeoutException error) {
            //Handles URL access timeout.
            ArrayList<String> result = new ArrayList<String>();
            result.add("SocketTimeoutException");
            return result;
        }
        catch (IOException error) {
            //Handles input and output errors
            ArrayList<String> result = new ArrayList<String>();
            result.add("IOException");
            return result;
        }
        catch (JSONException error) {
            //Handles input and output errors
            ArrayList<String> result = new ArrayList<String>();
            result.add("JSONException");
            return result;
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

    public Boolean createFriendshipRequest(String fromUsername, String toUsername) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(URL + "/friendship/request");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-Type", "application/json");
            client.setRequestProperty("Accept", "application/json");

            JSONObject credentials = new JSONObject();
            credentials.put("from_username",fromUsername);
            credentials.put("to_username", toUsername);

            OutputStream outputStream = client.getOutputStream();
            outputStream.write(credentials.toString().getBytes("UTF-8"));
            outputStream.close();

            client.connect();

            BufferedReader br;

            if (200 <= client.getResponseCode() && client.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(client.getErrorStream()));
            }

            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            String result = sb.toString();

            return result != "";

        } catch(MalformedURLException error) {
            //Handles an incorrectly entered URL
            return false;
        }
        catch(SocketTimeoutException error) {
            //Handles URL access timeout.
            return false;
        }
        catch (IOException error) {
            //Handles input and output errors
            return false;
        }
        catch (JSONException error) {
            //Handles input and output errors
            return false;
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

    public ArrayList<String> getFriendshipRequestsReceived(String toUsername) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(URL + "/friendship/request/received/" + toUsername);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");

            client.connect();

            BufferedReader br;

            if (200 <= client.getResponseCode() && client.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(client.getErrorStream()));
            }

            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            String result = sb.toString();

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("friendship_requests");

            ArrayList<String> friendship_requests = new ArrayList<String>();

            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject itemObject = jsonArray.getJSONObject(i);
                // Pulling items from the array
                friendship_requests.add(itemObject.getString("from_username"));
            }

            return friendship_requests;

        } catch(MalformedURLException error) {
            //Handles an incorrectly entered URL
            ArrayList<String> result = new ArrayList<String>();
            result.add("MalformedURLException");
            return result;
        }
        catch(SocketTimeoutException error) {
            //Handles URL access timeout.
            ArrayList<String> result = new ArrayList<String>();
            result.add("SocketTimeoutException");
            return result;
        }
        catch (IOException error) {
            //Handles input and output errors
            ArrayList<String> result = new ArrayList<String>();
            result.add("IOException");
            return result;
        }
        catch (JSONException error) {
            //Handles input and output errors
            ArrayList<String> result = new ArrayList<String>();
            result.add("JSONException");
            return result;
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

    public ArrayList<String> getFriendshipRequestsSent(String fromUsername) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(URL + "/friendship/request/sent/" + fromUsername);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");

            client.connect();

            BufferedReader br;

            if (200 <= client.getResponseCode() && client.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(client.getErrorStream()));
            }

            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            String result = sb.toString();

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("friendship_requests");

            ArrayList<String> friendship_requests = new ArrayList<String>();

            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject itemObject = jsonArray.getJSONObject(i);
                // Pulling items from the array
                friendship_requests.add(itemObject.getString("to_username"));
            }

            return friendship_requests;

        } catch(MalformedURLException error) {
            //Handles an incorrectly entered URL
            ArrayList<String> result = new ArrayList<String>();
            result.add("MalformedURLException");
            return result;
        }
        catch(SocketTimeoutException error) {
            //Handles URL access timeout.
            ArrayList<String> result = new ArrayList<String>();
            result.add("SocketTimeoutException");
            return result;
        }
        catch (IOException error) {
            //Handles input and output errors
            ArrayList<String> result = new ArrayList<String>();
            result.add("IOException");
            return result;
        }
        catch (JSONException error) {
            //Handles input and output errors
            ArrayList<String> result = new ArrayList<String>();
            result.add("JSONException");
            return result;
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

}

