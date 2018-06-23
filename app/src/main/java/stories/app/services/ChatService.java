package stories.app.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import stories.app.R;
import stories.app.activities.DirectMessagesActivity;
import stories.app.activities.FriendsListActivity;
import stories.app.activities.FriendshipRequestsReceivedActivity;
import stories.app.activities.HomeActivity;

public class ChatService extends FirebaseMessagingService {

    private enum NotificationType {
        MESSAGE("message"), FRIENDSHIP_REQUEST("friendship_request"), FRIENDSHIP_REQUEST_ACCEPTED("friendship_request_accepted"), STORY("story");

        private String name;

        NotificationType(String name) {
            this.name = name;
        }

        public static NotificationType getTypeByName(String name) {
            for (NotificationType notificationType : NotificationType.values()) {
                if (notificationType.name.equals(name)) {
                    return notificationType;
                }
            }
            return NotificationType.MESSAGE;
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {

            String titulo = remoteMessage.getNotification().getTitle();
            String texto = remoteMessage.getNotification().getBody();
            Map<String, String> data = remoteMessage.getData();
            NotificationType notificationType;
            if (data.containsKey("notification_type")) {
                notificationType = NotificationType.getTypeByName(data.get("notification_type"));
            } else {
                notificationType = NotificationType.MESSAGE;
            }

            //Opcional: mostrar la notificación en la barra de estado
            showNotification(titulo, texto, notificationType);
        }
    }

    private void showNotification(String title, String text, NotificationType notificationType) {
        Intent intent;
        switch (notificationType) {
            case STORY:
                intent = new Intent(this, HomeActivity.class);
                break;
            case FRIENDSHIP_REQUEST:
                intent = new Intent(this, FriendshipRequestsReceivedActivity.class);
                break;
            case FRIENDSHIP_REQUEST_ACCEPTED:
                intent = new Intent(this, FriendsListActivity.class);
                break;
            case MESSAGE:
            default:
                intent = new Intent(this, DirectMessagesActivity.class);
                break;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }


    public DatabaseReference messagesDB;
    //public Query userMessagesDB;

    /*
    public ChatService() {

        this.messagesDB = FirebaseDatabase.getInstance().getReference();
        messagesDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved: {" + dataSnapshot.getKey() + ": " + dataSnapshot.getValue() + "}");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved: " + dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error!", databaseError.toException());
            }
        });

        userMessagesDB = FirebaseDatabase.getInstance().getReference().orderByChild("");

    }
    */

}
