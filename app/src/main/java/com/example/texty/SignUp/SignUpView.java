package com.example.texty.SignUp;

import android.content.Context;

public interface SignUpView {
    void onSuccess();
    void onFail(String error);
    Context getContext();
}
