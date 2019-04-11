package com.example.texty.SignUp;

import android.util.Log;

import com.example.texty.SignIn.SignInView;
import com.example.texty.Utilities.Authenticator;
import com.example.texty.Utilities.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SignUpPresenter {

    final String TAG = "SignUpPresenter";

    SignInView mView;

    SignUpPresenter(SignInView view){
        mView = view;
    }

    void signUp(final String username, String password){
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
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                super.onSuccess(statusCode, headers, response);

                Log.i(TAG, "Request was sent successfully!");

                try {
                    if (response.isNull("error")) {
                        String token = response.getString("token");

                        Authenticator.setUsername(mView.getContext(),username);
                        Authenticator.setToken(mView.getContext(),token);

                        mView.onSuccess();
                    }
                    else {
                        String error = response.getString("error");

                        mView.onFail(error);
                    }
                }
                catch (Exception e) {
                    Log.e(TAG, "Can't retrieve message from json response!");
                }
            }


        };

        server.post(Constants.SIGN_UP_API, params, responseHandler);

    }
}
