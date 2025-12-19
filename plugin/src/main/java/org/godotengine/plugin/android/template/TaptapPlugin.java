package org.godotengine.plugin.android.template;

import static com.taptap.sdk.kit.internal.TapTapKit.context;

import android.support.annotation.NonNull;
import android.util.Log;

import com.taptap.sdk.BuildConfig;
import com.taptap.sdk.core.TapTapRegion;
import com.taptap.sdk.core.TapTapSdk;
import com.taptap.sdk.core.TapTapSdkOptions;
import com.taptap.sdk.kit.internal.callback.TapTapCallback;
import com.taptap.sdk.kit.internal.exception.TapTapException;
import com.taptap.sdk.login.Scopes;
import com.taptap.sdk.login.TapTapAccount;
import com.taptap.sdk.login.TapTapLogin;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import java.util.HashSet;
import java.util.Set;

public class TaptapPlugin extends GodotPlugin {

    public TaptapPlugin(Godot godot) {
        super(godot);
    }

    @Override
    public String getPluginName() {
        return "TapTap";
    }

    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new HashSet<>();
        signals.add(new SignalInfo("login_success"));
        signals.add(new SignalInfo("login_fail"));
        return signals;
    }

    @UsedByGodot
    public void TapTapInit(String clientId, String clientToken) {
        TapTapSdkOptions tapSdkOptions = new TapTapSdkOptions(
                clientId, // 游戏 Client ID
                clientToken, // 游戏 Client Token
                TapTapRegion.CN // 游戏可玩区域: [TapTapRegion.CN]=国内 [TapTapRegion.GLOBAL]=海外
        );
        tapSdkOptions.setEnableLog(BuildConfig.DEBUG);
        // 初始化 TapSDK
        TapTapSdk.init(context, tapSdkOptions);
    }

    @UsedByGodot
    public void TapTapLogin() {
        String[] scopes = new String[]{Scopes.SCOPE_PUBLIC_PROFILE};

        var activity = getGodot().getActivity();
        if (activity == null) {
            return;
        }


        TapTapLogin.loginWithScopes(activity, scopes, new TapTapCallback<TapTapAccount>() {
            @Override
            public void onSuccess(TapTapAccount tapTapAccount) {
                // 登录成功
                emitSignal("login_success", tapTapAccount.getName());
            }

            @Override
            public void onFail(@NonNull TapTapException exception) {
                // 登录失败
                emitSignal("login_fail", exception.getMessage());
            }

            @Override
            public void onCancel() {
                // 登录取消
                emitSignal("login_fail", "取消登陆");
            }
        });

    }
}
