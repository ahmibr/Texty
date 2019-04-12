package com.example.texty.PrivateMessage;
import com.example.texty.HomePage.HomePagePresenter;
import com.example.texty.HomePage.Message;
import com.example.texty.HomePage.MessagesListAdapter;
import com.example.texty.R;
import com.example.texty.Utilities.Authenticator;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PrivateMessageActivity extends AppCompatActivity implements PrivateMessageView, PopupMenu.OnMenuItemClickListener {

    private MessagesListAdapter messageadapter;
    private List<Message> arrayList;
    private PrivateMessagePresenter mPresenter;
    private MediaPlayer notificationSound;
    String myname="lazem yt3ml ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        String to = Authenticator.getUsername(getApplicationContext());

        arrayList = new ArrayList<Message>();
        messageadapter = new MessagesListAdapter(this, arrayList);
        ListView list = (ListView) findViewById(R.id.messages_view);
        list.setAdapter(messageadapter);
        mPresenter = new PrivateMessagePresenter(this, to);
        TextView header = (TextView) findViewById(R.id.header);
        header.setText(to);
       // Toast.makeText(getApplicationContext(), "Hello " + Authenticator.getUsername(getApplicationContext()), Toast.LENGTH_LONG).show();

    }

    @Override
    public void notifyPrivateMessage(String message, String username) {

    }

    @Override
    public void runThread(Runnable thread) {

    }

    @Override
    public void addMyMessage(String message) {
        Message m = new Message("reem", message, 1);
        arrayList.add(m);
        messageadapter.notifyDataSetChanged();
        ListView list = (ListView) findViewById(R.id.messages_view);
        list.setSelection(list.getCount() - 1);
    }

    @Override
    public void addOtherMessage(String message, String username) {
        Message m = new Message(username, message, 2);
        arrayList.add(m);
        messageadapter.notifyDataSetChanged();
        ListView list = (ListView) findViewById(R.id.messages_view);
        list.setSelection(list.getCount() - 1);
    }

    public void onSendClick(View v) {
        String message = ((EditText) findViewById(R.id.Message)).getText().toString();
        ((EditText) findViewById(R.id.Message)).getText().clear();
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
       // mPresenter.sendMessage(message);
    }

    public void onMoreClick(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.getMenu().add(1, 1, 1, "Back");
        popup.show();

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.Log_Out:
                Toast.makeText(getApplicationContext(), "LOG_OUT", Toast.LENGTH_LONG).show();
                return true;
            case 1:
                Toast.makeText(getApplicationContext(), "BACK", Toast.LENGTH_LONG).show();
                return false;
            default:
                return false;
        }
    }
}
