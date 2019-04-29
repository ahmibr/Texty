package com.example.texty.HomePage;

import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.texty.HomePage.Message;
import com.example.texty.R;
public class MessagesListAdapter extends BaseAdapter {

    private Context context;
    private List<Message> messagesItems;

    public MessagesListAdapter(Context context, List<Message> navDrawerItems) {
        this.context = context;
        this.messagesItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return messagesItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messagesItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /**
         * The following list not implemented reusable list items as list items
         * are showing incorrect data Add the solution if you have one
         * */

        Message m = messagesItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // Identifying the message owner
        if (messagesItems.get(position).isSelf()==1) {
            // message belongs to you, so load the right aligned layout
            convertView = mInflater.inflate(R.layout.my_message,
                    null);
            TextView txtMsg = (TextView) convertView.findViewById(R.id.message_body);
            txtMsg.setText(m.getMessage());
        } else if(messagesItems.get(position).isSelf()==2) {
            // message belongs to other person, load the left aligned layout
            convertView = mInflater.inflate(R.layout.their_message,
                    null);
            TextView txtMsg = (TextView) convertView.findViewById(R.id.message_body);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            txtMsg.setText(m.getMessage());
            name.setText(m.getFromName());
        }
        else{
            convertView = mInflater.inflate(R.layout.user_join,
                    null);
            TextView joinMsg = (TextView) convertView.findViewById(R.id.join_message);
            joinMsg.setText(m.getMessage());
         }
        return convertView;
    }
}