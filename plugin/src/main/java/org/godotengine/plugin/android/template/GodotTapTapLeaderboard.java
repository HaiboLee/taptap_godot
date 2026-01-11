package org.godotengine.plugin.android.template;

import android.util.Log;

import com.taptap.sdk.leaderboard.androidx.TapTapLeaderboard;
import com.taptap.sdk.leaderboard.callback.TapTapLeaderboardResponseCallback;
import com.taptap.sdk.leaderboard.data.request.LeaderboardCollection;
import com.taptap.sdk.leaderboard.data.request.SubmitScoresRequest;
import com.taptap.sdk.leaderboard.data.response.LeaderboardScoresResponse;
import com.taptap.sdk.leaderboard.data.response.SubmitScoresResponse;
import com.taptap.sdk.leaderboard.data.response.common.Score;
import com.taptap.sdk.login.TapTapAccount;
import com.taptap.sdk.login.TapTapLogin;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

//TapTap 排行榜
public class GodotTapTapLeaderboard {

    TaptapPlugin taptap;

    public GodotTapTapLeaderboard(TaptapPlugin _taptap) {
        taptap = _taptap;
    }

    //打开排行榜页面
    protected void OpenLeaderboardPage(String id, String lbType) {
        TapTapAccount currentTapAccount = TapTapLogin.getCurrentTapAccount();
        if (currentTapAccount == null) {
            taptap.tapTapLogin();
        }
        TapTapLeaderboard.openLeaderboard(
                Objects.requireNonNull(taptap.GetGodot().getActivity()),
                id,
                lbType
        );
    }

    protected void SubmitScore(String id, long score) {
        TapTapAccount currentTapAccount = TapTapLogin.getCurrentTapAccount();
        if (currentTapAccount == null) {
            taptap.tapTapLogin();
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

    //获得排行榜首页分数
    protected void GetLeaderboardScores(String id) {
        TapTapAccount currentTapAccount = TapTapLogin.getCurrentTapAccount();
        if (currentTapAccount == null) {
            taptap.tapTapLogin();
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
                                    obj.put("userId", s.getUser().getOpenid());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            arr.put(obj);
                        }
                        taptap.TapTapEmitSignal("leaderboard_scores", arr.toString());
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        // 获取失败
                        Log.e("Leaderboard", "获取排行榜数据失败: code=" + code + ", message=" + message);
                    }
                }
        );
    }
}
