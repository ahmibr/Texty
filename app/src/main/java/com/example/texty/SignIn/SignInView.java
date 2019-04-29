package com.example.texty.SignIn;

import android.content.Context;

public interface SignInView {
    void onSuccess(String userName);
    void onFail(String error);
    Context getContext();
}
