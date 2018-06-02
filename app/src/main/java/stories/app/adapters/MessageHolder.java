package stories.app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import stories.app.R;
import stories.app.models.Message;
import stories.app.utils.LocalStorage;

public class MessageHolder extends RecyclerView.ViewHolder {
    private View mView;
    private static String DATE_FORMAT = "dd MMM yyyy HH:mm";

    public MessageHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setMessageUsername(Message message) {
        TextView messageUsername = (TextView) mView.findViewById(R.id.message_item_title);
        StringBuilder sb = new StringBuilder();
        if (LocalStorage.getUsername().equals(message.from_username)) {
            sb.append("To: ");
            sb.append(message.to_username);
        } else {
            sb.append("From: ");
            sb.append(message.from_username);
        }
        messageUsername.setText(sb.toString());
    }

    public void setTimestamp(Message message) {
        TextView timestamp = (TextView) mView.findViewById(R.id.message_item_timestamp);
        timestamp.setText(new SimpleDateFormat(DATE_FORMAT).format(new Date(message.timestamp)));
    }

    public void setMessage(Message message) {
        TextView messageText = (TextView) mView.findViewById(R.id.message_item_text);
        messageText.setText(message.message);
    }
}
