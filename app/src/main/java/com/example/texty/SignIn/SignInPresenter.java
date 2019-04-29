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

    /**
     * Sends sign in request to server
     *
     * @author Ahmed Ibrahim
     * @version 1.0
     */
    void signIn(final String username, String password){

        //********************Validate input************************************//
        if(username.isEmpty()){
            mView.onFail("Please enter username");
            return;
        }
        if(password.isEmpty()){
            mView.onFail("Please enter password");
            return;
        }
        /************************************************************************/


        //Initialize connection to server
        AsyncHttpClient server = new AsyncHttpClient();

        RequestParams params = new RequestParams();


        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler(){

            /*****************************On Success******************************************************/

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {

                super.onSuccess(statusCode, headers, response);

                Log.i(TAG, "Request was sent successfully!");

                try {
                    //If sign in is done successfully
                    if (response.isNull("errors")) {
                        //retrieve token
                        String token = response.getString("token");
                        Log.d(TAG,"Token = " + token);
                        Log.d(TAG,"Username = " + username);

                        //set user info
                        Authenticator.setUsername(mView.getContext(),username);
                        Authenticator.setToken(mView.getContext(),token);

                        mView.onSuccess();
                    }
                    else {
                        //Print errors
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

            /*****************************On fail******************************************************/
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

        // Put user info into request
        params.put("username",username);
        params.put("password",password);

        //send request to server
        server.post(Constants.SIGN_IN_API, params, responseHandler);



    }

}
