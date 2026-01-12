package org.godotengine.plugin.android.template;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.taptap.sdk.kit.internal.callback.TapTapCallback;
import com.taptap.sdk.kit.internal.exception.TapTapException;
import com.taptap.sdk.login.Scopes;
import com.taptap.sdk.login.TapTapAccount;
import com.taptap.sdk.login.TapTapLogin;

import org.godotengine.godot.plugin.UsedByGodot;

import kotlinx.serialization.json.Json;

public class GodotTapTapLogin {

    static final int Login = 1; //登录
    static final int GetInfo = 2; //获取信息

    private TaptapPlugin taptap;

    GodotTapTapLogin(TaptapPlugin _taptap) {
        taptap = _taptap;
    }

    public void TapTapLogin() {
        String[] scopes = new String[]{Scopes.SCOPE_PUBLIC_PROFILE};
        var activity = taptap.GetGodot().getActivity();
        if (activity == null) {
            Log.e("taptap", "login taptap activity is null");
            return;
        }

        TapTapLogin.loginWithScopes(activity, scopes, new TapTapCallback<TapTapAccount>() {
            @Override
            public void onSuccess(TapTapAccount tapTapAccount) {
                // 登录成功
                var info = new TapTapUserInfo();
                info.name = tapTapAccount.getName();
                info.accessToken = tapTapAccount.getAccessToken().toString();
                info.openId = tapTapAccount.getOpenId();
                taptap.TapTapEmitSignal("login", Login, true, info.toJson());
            }

            @Override
            public void onFail(@NonNull TapTapException exception) {
                // 登录失败
                taptap.TapTapEmitSignal("login", Login, false, exception.toString(), "");
            }

            @Override
            public void onCancel() {
                // 登录取消
                taptap.TapTapEmitSignal("login", Login, false, "取消登录", "");
            }
        });
    }

    //获取用户信息
    public void GetCurrentTapAccount() {
        var account = TapTapLogin.getCurrentTapAccount();
        var info = new TapTapUserInfo();
        info.name = account.getName();
        info.accessToken = account.getAccessToken().toString();
        info.openId = account.getOpenId();
        taptap.TapTapEmitSignal("login", GetInfo, true, info.toJson());

    }

    public class TapTapUserInfo {

        public String openId;
        public String accessToken;
        public String name;


        public String toJson() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }

}

