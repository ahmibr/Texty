package com.example.texty.SignIn;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.texty.Utilities.Authenticator;
import com.example.texty.Utilities.Constants;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class SignInPresenter {

    View mView;

    SignInPresenter(View view){
        mView = view;
    }

    void signIn(final String userName, String password){

        final String TAG = "SignInPresenter";

        AsyncHttpClient server = new AsyncHttpClient();

        RequestParams params = new RequestParams();


        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {

                super.onSuccess(statusCode, headers, response);

                Log.i(TAG, "Request was sent successfully!");

                try {
                    if (response.isNull("error")) {
                        String token = response.getString("token");

                        Authenticator.setUserName(mView.getContext(),userName);
                        Authenticator.setToken(mView.getContext(),token);

                        //@TODO add callback
                        //mView.onSuccess();
                    }
                    else {
                        String error = response.getString("error");

                        //@TODO add callback
                        //mView.onFail();
                    }
                }
                catch (Exception e) {
                    Log.e(TAG, "Can't retrieve message from json response!");
                }
            }


        };

        server.post(Constants.SIGN_IN_API, params, responseHandler);



    }

}
