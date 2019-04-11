package com.example.texty.SignIn;

import android.util.Log;
import android.view.View;

import com.example.texty.Utilities.Authenticator;
import com.example.texty.Utilities.Constants;
import com.loopj.android.http.*;

import org.json.JSONObject;


public class SignInPresenter {

    SignInView mView;
    final String TAG = "SignInPresenter";
    SignInPresenter(SignInView view){
        mView = view;
    }

    void signIn(final String userName, String password){
        if(userName.isEmpty()){
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
                    if (response.isNull("error")) {
                        String token = response.getString("token");

                        Authenticator.setUsername(mView.getContext(),userName);
                        Authenticator.setToken(mView.getContext(),token);

                        //@TODO add callback
                        mView.onSuccess();
                    }
                    else {
                        String error = response.getString("error");

                        //@TODO add callback
                        mView.onFail(error);
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
