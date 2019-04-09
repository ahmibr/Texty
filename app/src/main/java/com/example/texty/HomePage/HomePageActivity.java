package com.example.texty.HomePage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.texty.R;
import com.example.texty.SignIn.SignInActivity;
import com.example.texty.Utilities.Authenticator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToLongBiFunction;


public class HomePageActivity extends AppCompatActivity implements HomePageView, PopupMenu.OnMenuItemClickListener {

    private MessagesListAdapter messageadapter;
    private List<Message> arrayList;

    //private List<String>menu;
    //private ArrayAdapter<String> menuadapter;
    private String my_name ="reem ";
    private final String TAG = "HomePageActivity";
    private HomePagePresenter mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
//        menu = new ArrayList<String>();
//        menuadapter = new ArrayAdapter<String>(this,R.layout.buttons,R.id.button,menu);
//        menu.add("signout");
//        menu.add("Friends");
//        menuadapter.notifyDataSetChanged();
        arrayList = new ArrayList<Message>();
        messageadapter =new MessagesListAdapter(this, arrayList);
        ListView list = (ListView)findViewById(R.id.messages_view);
        list.setAdapter(messageadapter);
//        ListView l = (ListView)findViewById(R.id.menu);
//        l.setAdapter(menuadapter);
        mPresenter = new HomePagePresenter(this);

        if(!mPresenter.IsLoggedIn())
        {
            reSignIn();
        }
        else {
            mPresenter.initializeSocket();
            mPresenter.initializeChat();
            my_name = Authenticator.getUsername(getApplicationContext());
            Toast.makeText(getApplicationContext(), "Hello " + Authenticator.getUsername(getApplicationContext()), Toast.LENGTH_LONG).show();

        }
    }

    public void send(View v){
        String message = ((EditText)findViewById(R.id.Message)).getText().toString();
        ((EditText)findViewById(R.id.Message)).getText().clear();
        mPresenter.sendMessage(message);
        mPresenter.sendMessage(message);

    }
    public void viewspinner(View v)
    {
        /*ListView l = (ListView)findViewById(R.id.menu);
        if (l.getVisibility()==View.VISIBLE)
            l.setVisibility(View.GONE);
        else
            l.setVisibility(View.VISIBLE);
*/
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }


    void reSignIn(){
        Intent signInIntent = new Intent(this, SignInActivity.class);
        startActivity(signInIntent);
        finish();
    }


    void printToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"Socket closed");
        super.onDestroy();
        mPresenter.closeSocket();
    }

    @Override
    public void runThread(Runnable thread) {
        runOnUiThread(thread);
    }

    @Override
    public void addMyMessage(String message) {
        Message m = new Message(my_name,message,true);
        arrayList.add(m);
        messageadapter.notifyDataSetChanged();
        ListView list = (ListView)findViewById(R.id.messages_view);
        list.setSelection(list.getCount() - 1);

    }

    @Override
    public void addOtherMessage(String message, String username) {
        Message m = new Message(username,message,false);
        arrayList.add(m);
        messageadapter.notifyDataSetChanged();
        ListView list = (ListView)findViewById(R.id.messages_view);
        list.setSelection(list.getCount() - 1);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.Log_Out:
                printToast("item 2 is clicked");
                return true;
            case R.id.Show_Friends:
                printToast("item 1 is clicked");
                return true;
            default:
                return false;

        }

    }
}
