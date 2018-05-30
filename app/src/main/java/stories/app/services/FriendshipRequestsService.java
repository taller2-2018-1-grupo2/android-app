package stories.app.services;

import android.util.Pair;

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

    public ArrayList<Pair<String, String>> getUsers(String partialUsername, String userID) {
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

            ArrayList<Pair<String,String>> users = new ArrayList<Pair<String,String>>();

            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject itemObject = jsonArray.getJSONObject(i);
                // Pulling items from the array
                users.add(Pair.create(itemObject.getString("username"), itemObject.getString("profile_pic")));
            }

            return users;

        } catch(MalformedURLException error) {
            //Handles an incorrectly entered URL
            ArrayList<Pair<String,String>> result = new ArrayList<Pair<String,String>>();
            result.add(Pair.create("MalformedURLException",""));
            return result;
        }
        catch(SocketTimeoutException error) {
            //Handles URL access timeout.
            ArrayList<Pair<String,String>> result = new ArrayList<Pair<String,String>>();
            result.add(Pair.create("SocketTimeoutException",""));
            return result;
        }
        catch (IOException error) {
            //Handles input and output errors
            ArrayList<Pair<String,String>> result = new ArrayList<Pair<String,String>>();
            result.add(Pair.create("IOException",""));
            return result;
        }
        catch (JSONException error) {
            //Handles input and output errors
            ArrayList<Pair<String,String>> result = new ArrayList<Pair<String,String>>();
            result.add(Pair.create("JSONException",""));
            return result;
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

    public String createFriendshipRequest(String fromUsername, String toUsername) {
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

            return toUsername;

        } catch(MalformedURLException error) {
            //Handles an incorrectly entered URL
            return "";
        }
        catch(SocketTimeoutException error) {
            //Handles URL access timeout.
            return "";
        }
        catch (IOException error) {
            //Handles input and output errors
            return "";
        }
        catch (JSONException error) {
            //Handles input and output errors
            return "";
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

    public ArrayList<Pair<String,String>> getFriendshipRequestsReceived(String toUsername) {
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

            ArrayList<Pair<String,String>> friendship_requests = new ArrayList<>();

            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject itemObject = jsonArray.getJSONObject(i);
                // Pulling items from the array
                friendship_requests.add(Pair.create(itemObject.getString("from_username"), itemObject.getString("profile_pic")));
            }

            return friendship_requests;

        } catch(MalformedURLException error) {
            //Handles an incorrectly entered URL
            ArrayList<Pair<String,String>> result = new ArrayList<>();
            result.add(Pair.create("MalformedURLException", ""));
            return result;
        }
        catch(SocketTimeoutException error) {
            //Handles URL access timeout.
            ArrayList<Pair<String,String>> result = new ArrayList<>();
            result.add(Pair.create("SocketTimeoutException", ""));
            return result;
        }
        catch (IOException error) {
            //Handles input and output errors
            ArrayList<Pair<String,String>> result = new ArrayList<>();
            result.add(Pair.create("IOException", ""));
            return result;
        }
        catch (JSONException error) {
            //Handles input and output errors
            ArrayList<Pair<String,String>> result = new ArrayList<>();
            result.add(Pair.create("JSONException", ""));
            return result;
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

    public ArrayList<Pair<String,String>> getFriendshipRequestsSent(String fromUsername) {
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

            ArrayList<Pair<String,String>> friendship_requests = new ArrayList<>();

            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject itemObject = jsonArray.getJSONObject(i);
                // Pulling items from the array
                friendship_requests.add(Pair.create(itemObject.getString("to_username"), itemObject.getString("profile_pic")));
            }

            return friendship_requests;

        } catch(MalformedURLException error) {
            //Handles an incorrectly entered URL
            ArrayList<Pair<String,String>> result = new ArrayList<>();
            result.add(Pair.create("MalformedURLException", ""));
            return result;
        }
        catch(SocketTimeoutException error) {
            //Handles URL access timeout.
            ArrayList<Pair<String,String>> result = new ArrayList<>();
            result.add(Pair.create("SocketTimeoutException", ""));
            return result;
        }
        catch (IOException error) {
            //Handles input and output errors
            ArrayList<Pair<String,String>> result = new ArrayList<>();
            result.add(Pair.create("IOException", ""));
            return result;
        }
        catch (JSONException error) {
            //Handles input and output errors
            ArrayList<Pair<String,String>> result = new ArrayList<>();
            result.add(Pair.create("JSONException", ""));
            return result;
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

    public String deleteFriendshipRequest(String fromUsername, String toUsername) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(URL + "/friendship/request/" + fromUsername + "/" + toUsername);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("DELETE");

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

            return toUsername;

        } catch(MalformedURLException error) {
            //Handles an incorrectly entered URL
            return "";
        }
        catch(SocketTimeoutException error) {
            //Handles URL access timeout.
            return "";
        }
        catch (IOException error) {
            //Handles input and output errors
            return "";
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

    public String acceptFriendshipRequest(String fromUsername, String toUsername) {
        HttpURLConnection client = null;

        try {
            URL url = new URL(URL + "/friendship");
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

            return fromUsername;

        } catch(MalformedURLException error) {
            //Handles an incorrectly entered URL
            return "";
        }
        catch(SocketTimeoutException error) {
            //Handles URL access timeout.
            return "";
        }
        catch (IOException error) {
            //Handles input and output errors
            return "";
        }
        catch (JSONException error) {
            //Handles input and output errors
            return "";
        }
        finally {
            if(client != null) {
                client.disconnect();
            }
        }
    }

}

