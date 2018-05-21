package stories.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import stories.app.R;
import stories.app.models.Message;

public class DirectMessagesAdapter extends ArrayAdapter<Message> {

    private static String DATE_FORMAT = "dd MMM yyyy HH:mm";

    public DirectMessagesAdapter(Context context, ArrayList<Message> messages) {
        super(context, R.layout.message_item, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);

            Message message = this.getItem(position);

            TextView messageUsername = (TextView) convertView.findViewById(R.id.message_item_title);
            StringBuilder sb = new StringBuilder();
            if(message._id.equals(message.from_username)) {
                sb.append("From: ");
            } else {
                sb.append("To: ");
            }
            sb.append(message._id);
            messageUsername.setText(sb.toString());

            TextView timestamp = (TextView) convertView.findViewById(R.id.message_item_timestamp);
            timestamp.setText(new SimpleDateFormat(DATE_FORMAT).format(new Date(message.timestamp)));

            TextView messageText = (TextView) convertView.findViewById(R.id.message_item_text);
            messageText.setText(message.message);
        }

        return convertView;
    }
}
