package org.godotengine.plugin.android.template;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.taptap.sdk.cloudsave.TapTapCloudSave;
import com.taptap.sdk.cloudsave.internal.TapCloudSaveCallback;
import com.taptap.sdk.compliance.TapTapCompliance;
import com.taptap.sdk.compliance.TapTapComplianceCallback;
import com.taptap.sdk.compliance.constants.ComplianceMessage;
import com.taptap.sdk.compliance.option.TapTapComplianceOptions;
import com.taptap.sdk.core.TapTapRegion;
import com.taptap.sdk.core.TapTapSdk;
import com.taptap.sdk.core.TapTapSdkOptions;
import com.taptap.sdk.kit.internal.callback.TapTapCallback;
import com.taptap.sdk.kit.internal.exception.TapTapException;
import com.taptap.sdk.leaderboard.androidx.TapTapLeaderboard;
import com.taptap.sdk.leaderboard.callback.TapTapLeaderboardCallback;
import com.taptap.sdk.leaderboard.callback.TapTapLeaderboardResponseCallback;
import com.taptap.sdk.leaderboard.data.request.LeaderboardCollection;
import com.taptap.sdk.leaderboard.data.request.SubmitScoresRequest;
import com.taptap.sdk.leaderboard.data.response.LeaderboardScoresResponse;
import com.taptap.sdk.leaderboard.data.response.SubmitScoresResponse;
import com.taptap.sdk.leaderboard.data.response.common.Score;
import com.taptap.sdk.login.Scopes;
import com.taptap.sdk.login.TapTapAccount;
import com.taptap.sdk.login.TapTapLogin;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TaptapPlugin extends GodotPlugin {

    GodotTapTapCloudSave godotTapTapCloudSave;
    GodotTapTapLogin godotTapTapLogin;
    GodotTapTapCompliance godotTapTapCompliance;

    GodotTapTapLeaderboard godotTapTapLeaderboard;

    public TaptapPlugin(Godot godot) {
        super(godot);
        godotTapTapCloudSave = new GodotTapTapCloudSave(this);
        godotTapTapLogin = new GodotTapTapLogin(this);
        godotTapTapCompliance = new GodotTapTapCompliance(this);
        godotTapTapLeaderboard = new GodotTapTapLeaderboard(this);
    }

    @Override
    public String getPluginName() {
        return "TapTap";
    }

    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new HashSet<>();
        signals.add(new SignalInfo("login", Integer.class, Boolean.class, String.class));
        signals.add(new SignalInfo("taptap_compliance", Boolean.class));
        signals.add(new SignalInfo("leaderboard_scores", Integer.class, String.class)); //排行榜分数
        signals.add(new SignalInfo("cloud_save", Integer.class, Boolean.class, String.class));
        return signals;
    }


    @UsedByGodot
    public void tapTapCreateCloudSave(String filePath, String name, String des) { //创建云存档
        godotTapTapCloudSave.CreateCloudSave(filePath, name, des);
    }

    @UsedByGodot
    public void taptapGetCloudList() {
        godotTapTapCloudSave.GetCloudSaveList();
    }

    @UsedByGodot
    public void downloadCloudSave(String uuid, String fileId) {
        godotTapTapCloudSave.DownloadCloudSave(uuid, fileId);
    }

    @UsedByGodot
    public void updateCloudSave(String uuid, String path, String name) {
        godotTapTapCloudSave.UpdateCloudSave(uuid, path, name);
    }

    @UsedByGodot
    public void deleteCloudSave(String uuid) {
        godotTapTapCloudSave.DeleteCloudSave(uuid);
    }


    @UsedByGodot
    public void tapTapLogin() {
        godotTapTapLogin.TapTapLogin();
    }

    @UsedByGodot
    public void tapTapGetAccount() {
        godotTapTapLogin.GetCurrentTapAccount();
    }


    //合规认证
    @UsedByGodot
    public void tapTapCompliance() {
        godotTapTapCompliance.tapTapCompliance();
    }

    //退出认证
    @UsedByGodot
    public void exitCompliance() {
        TapTapCompliance.exit();
    }

    @UsedByGodot
    public void logout() {
        TapTapLogin.logout();
    }


    //打开排行榜页面
    @UsedByGodot
    public void openLeaderboardPage(String id, String lbType) {
        godotTapTapLeaderboard.OpenLeaderboardPage(id, lbType);
    }

    //提交排行榜分数
    @UsedByGodot
    public void submitScore(String id, long score) {
        godotTapTapLeaderboard.SubmitScore(id, score);
    }

    //获取排行榜分数
    @UsedByGodot
    public void GetLeaderboardScores(String id) {

        godotTapTapLeaderboard.GetLeaderboardScores(id);
    }

    @UsedByGodot
    public void GetNearbyScores(String id) {
        godotTapTapLeaderboard.PlayerCenteredScores(id);
    }


    @UsedByGodot
    public void tapTapInit(String clientId, String clientToken) {

        Log.i("taptap", "taptap init begin");
        var activity = getGodot().getActivity();

        if (activity == null) {
            Log.e("taptap", "activity is null");
            return;
        }

        TapTapSdkOptions tapSdkOptions = new TapTapSdkOptions(
                clientId, // 游戏 Client ID
                clientToken, // 游戏 Client Token
                TapTapRegion.CN // 游戏可玩区域: [TapTapRegion.CN]=国内 [TapTapRegion.GLOBAL]=海外
        );
        tapSdkOptions.setEnableLog(true);
        // 初始化 TapSDK

        // 可选配置 合规模块
        TapTapComplianceOptions tapComplianceOptions = new TapTapComplianceOptions(
                true, // 是否显示切换账号按钮
                true // 游戏是否需要获取真实年龄段信息
        );

        var context = activity.getApplicationContext();
        if (context == null) {
            Log.e("taptap", "context is null");
            return;
        }
        TapTapSdk.init(context, tapSdkOptions, tapComplianceOptions);

    }


    //获取 godot 对象
    protected Godot GetGodot() {
        return getGodot();
    }

    protected void ComplianceListen() {
        complianceListen();
    }


    //合规认证监听
    private void complianceListen() {

        TapTapCompliance.registerComplianceCallback((code, extra) -> {

            Log.i("taptap", "合规认证结果:" + code);

            // 合规结果
            switch (code) {
                case ComplianceMessage.LOGIN_SUCCESS:
                    // do something
                    Log.i("taptap", "合规认证成功");
                    TapTapEmitSignal("taptap_compliance", true);
                    break;
                default:
                    Log.i("taptap", "合规认证失败:" + code);
                    TapTapEmitSignal("taptap_compliance", false);
                    break;
            }
        });

    }

    public void TapTapEmitSignal(String signalName, final Object... signalArgs) {
        emitSignal(signalName, signalArgs);
    }

}
