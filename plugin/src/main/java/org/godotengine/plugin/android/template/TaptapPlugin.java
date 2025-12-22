package org.godotengine.plugin.android.template;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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
        signals.add(new SignalInfo("login_success", Boolean.class, String.class, String.class));
        signals.add(new SignalInfo("taptap_compliance", Boolean.class));
        signals.add(new SignalInfo("leaderboard_scores", String.class)); //排行榜分数
        return signals;
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

        //complianceListen();

        var context = activity.getApplicationContext();
        if (context == null) {
            Log.e("taptap", "context is null");
            return;
        }
        TapTapSdk.init(context, tapSdkOptions, tapComplianceOptions);

    }

    @UsedByGodot
    public void tapTapLogin() {

        String[] scopes = new String[]{Scopes.SCOPE_PUBLIC_PROFILE};
        var activity = getGodot().getActivity();
        if (activity == null) {
            Log.e("taptap", "login taptap activity is null");
            return;
        }

        TapTapLogin.loginWithScopes(activity, scopes, new TapTapCallback<TapTapAccount>() {
            @Override
            public void onSuccess(TapTapAccount tapTapAccount) {
                // 登录成功
                emitSignal("login_success", true, tapTapAccount.getName(), tapTapAccount.getOpenId());
            }

            @Override
            public void onFail(@NonNull TapTapException exception) {
                // 登录失败
                emitSignal("login_success", false, exception.toString(), "");
            }

            @Override
            public void onCancel() {
                // 登录取消
                emitSignal("login_success", false, "取消登录", "");
            }
        });
    }


    //合规认证
    @UsedByGodot
    public void tapTapCompliance() {


        TapTapAccount currentTapAccount = TapTapLogin.getCurrentTapAccount();
        if (currentTapAccount != null) {
            complianceListen();
            String unionId = currentTapAccount.getUnionId();
            TapTapCompliance.startup(Objects.requireNonNull(getGodot().getActivity()), unionId);
        } else {
            Log.i("taptap", "合规认证账户为空");
            tapTapLogin();
        }

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
        TapTapAccount currentTapAccount = TapTapLogin.getCurrentTapAccount();
        if (currentTapAccount == null) {
            tapTapLogin();
        }
        TapTapLeaderboard.openLeaderboard(
                Objects.requireNonNull(getGodot().getActivity()),
                id,
                lbType
        );
    }

    //提交排行榜分数
    @UsedByGodot
    public void submitScore(String id, long score) {

        TapTapAccount currentTapAccount = TapTapLogin.getCurrentTapAccount();
        if (currentTapAccount == null) {
            tapTapLogin();
        }

        List<SubmitScoresRequest.ScoreItem> scores = Arrays.asList(
                new SubmitScoresRequest.ScoreItem(id, score)
        );

        TapTapLeaderboard.submitScores(
                scores,
                new TapTapLeaderboardResponseCallback<SubmitScoresResponse>() {
                    @Override
                    public void onSuccess(SubmitScoresResponse data) {
                        // 提交成功
                        Log.i("Leaderboard", "提交成功: " + data);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        // 提交失败
                        Log.e("Leaderboard", "提交失败: code=" + code + ", message=" + message);
                    }
                }
        );
    }

    //获取排行榜分数
    @UsedByGodot
    public void GetLeaderboardScores(String id) {

        TapTapAccount currentTapAccount = TapTapLogin.getCurrentTapAccount();
        if (currentTapAccount == null) {
            tapTapLogin();
        }


        TapTapLeaderboard.loadLeaderboardScores(
                id, // 排行榜ID
                LeaderboardCollection.PUBLIC, // 总榜
                null, // nextPage - 首次请求传null
                null, // periodToken - 时间周期标识
                new TapTapLeaderboardResponseCallback<LeaderboardScoresResponse>() {
                    @Override
                    public void onSuccess(LeaderboardScoresResponse data) {
                        // 获取成功
                        List<Score> scores = data.getScores();

                        if (scores.isEmpty()) {
                            return;
                        }
                        JSONArray arr = new JSONArray();
                        for (Score s : scores) {
                            JSONObject obj = new JSONObject();
                            try {
                                obj.put("rank", s.getRank());
                                obj.put("score", s.getScore());
                                if (s.getUser() != null) {
                                    obj.put("userName", s.getUser().getName());
                                    assert s.getUser().getAvatar() != null;
                                    obj.put("userId", s.getUser().getAvatar().getUrl());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            arr.put(obj);
                        }
                        emitSignal("leaderboard_scores", arr.toString());
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        // 获取失败
                        Log.e("Leaderboard", "获取排行榜数据失败: code=" + code + ", message=" + message);
                    }
                }
        );
    }


    //注册排行榜回调函数
    private void registerLeaderboard() {
        // 注册排行榜事件回调
        TapTapLeaderboardCallback callback = new TapTapLeaderboardCallback() {
            @Override
            public void onLeaderboardResult(int code, String message) {
                // 处理排行榜事件
                switch (code) {
                    case 500102:
                        // 用户未登录，需要引导用户登录
                        // showLoginDialog();
                        break;
                    // 处理其他事件
                    default:
                        Log.d("Leaderboard", "code: " + code + ", message: " + message);
                        break;
                }
            }
        };
        TapTapLeaderboard.registerLeaderboardCallback(callback);
        TapTapLeaderboard.unregisterLeaderboardCallback(callback);
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
                    emitSignal("taptap_compliance", true);
                    break;
//                        case ComplianceMessage.EXITED:
//                        case ComplianceMessage.SWITCH_ACCOUNT:
//                        case ComplianceMessage.PERIOD_RESTRICT:
//                        case ComplianceMessage.DURATION_LIMIT:
//                        case ComplianceMessage.INVALID_CLIENT_OR_NETWORK_ERROR:
                default:
                    Log.i("taptap", "合规认证失败:" + code);
                    emitSignal("taptap_compliance", false);
                    break;
            }
        });


    }

}
