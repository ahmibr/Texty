package com.example.texty.SignIn;

import android.content.Context;

public interface SignInView {
    void onSuccess();
    void onFail(String error);
    Context getContext();
}
