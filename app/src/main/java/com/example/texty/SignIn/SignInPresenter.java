package com.example.texty.SignIn;

import android.nfc.Tag;
import android.util.Log;
import android.view.View;

import com.example.texty.Utilities.Authenticator;
import com.example.texty.Utilities.Constants;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class SignInPresenter {

    SignInView mView;
    final String TAG = "SignInPresenter";
    SignInPresenter(SignInView view){
        mView = view;
    }

    void signIn(final String username, String password){
        if(username.isEmpty()){
            mView.onFail("Please enter username");
            return;
        }
        if(password.isEmpty()){
            mView.onFail("Please enter password");
            return;
        }

        AsyncHttpClient server = new AsyncHttpClient();

        RequestParams params = new RequestParams();


        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {

                super.onSuccess(statusCode, headers, response);

                Log.i(TAG, "Request was sent successfully!");

                try {
                    if (response.isNull("errors")) {
                        String token = response.getString("token");
                        Log.d(TAG,"Token = " + token);
                        Log.d(TAG,"Username = " + username);
                        Authenticator.setUsername(mView.getContext(),username);
                        Authenticator.setToken(mView.getContext(),token);

                        mView.onSuccess();
                    }
                    else {

                        JSONArray jsonArray = response.getJSONArray("errors");

                        String error = jsonArray.getString(0);
                        Log.e(TAG,error);
                        mView.onFail(error);
                    }
                }
                catch (Exception e) {
                    Log.e(TAG, "Can't retrieve message from json response!");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                mView.onFail("Connection error, please check your connection and retry!");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                mView.onFail("Connection error, please check your connection and retry!");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                mView.onFail("Connection error, please check your connection and retry!");
            }



        };

        params.put("username",username);
        params.put("password",password);
        server.post(Constants.SIGN_IN_API, params, responseHandler);



    }

}
