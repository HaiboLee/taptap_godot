package org.godotengine.plugin.android.template;

import android.support.annotation.NonNull;
import android.util.Log;

import com.taptap.sdk.kit.internal.callback.TapTapCallback;
import com.taptap.sdk.kit.internal.exception.TapTapException;
import com.taptap.sdk.login.Scopes;
import com.taptap.sdk.login.TapTapAccount;
import com.taptap.sdk.login.TapTapLogin;

import org.godotengine.godot.plugin.UsedByGodot;

public class GodotTapTapLogin {

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
                //emitSignal("login_success", true, tapTapAccount.getName(), tapTapAccount.getOpenId());

                taptap.TapTapEmitSignal("login_success", true, tapTapAccount.getName(), tapTapAccount.getOpenId());
            }

            @Override
            public void onFail(@NonNull TapTapException exception) {
                // 登录失败
                taptap.TapTapEmitSignal("login_success", false, exception.toString(), "");
            }

            @Override
            public void onCancel() {
                // 登录取消
                taptap.TapTapEmitSignal("login_success", false, "取消登录", "");
            }
        });
    }

}
