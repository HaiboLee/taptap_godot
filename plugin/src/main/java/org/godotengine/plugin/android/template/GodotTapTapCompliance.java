package org.godotengine.plugin.android.template;

import android.util.Log;

import com.taptap.sdk.compliance.TapTapCompliance;
import com.taptap.sdk.login.TapTapAccount;
import com.taptap.sdk.login.TapTapLogin;

import java.util.Objects;

public class GodotTapTapCompliance {

    TaptapPlugin taptap;

    public GodotTapTapCompliance(TaptapPlugin _taptap) {
        taptap = _taptap;
    }

    public void tapTapCompliance() {
        TapTapAccount currentTapAccount = TapTapLogin.getCurrentTapAccount();
        if (currentTapAccount != null) {
            taptap.ComplianceListen();
            String unionId = currentTapAccount.getUnionId();
            TapTapCompliance.startup(Objects.requireNonNull(taptap.GetGodot().getActivity()), unionId);
        } else {
            Log.i("taptap", "合规认证账户为空");
            taptap.tapTapLogin();
        }
    }
}
