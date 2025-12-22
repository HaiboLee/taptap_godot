using Godot;
using System;

public partial class TapTapGodot : Node
{
    private GodotObject _tapTap;

    private string _clientId = "b0hnecor7odhsozvfe";
    private string _clientToken = "YVnPjPYe5v6awpnkeSXtNdiOh5UJdzuLmjt1NKJY";

    [Signal]
    private delegate void LoginResultEventHandler(bool success, string name = "", string openId = "");

    public override void _Ready()
    {
        base._Ready();
        if (Engine.HasSingleton("TapTap"))
        {
            GD.Print("TapTap 插件初始化");
            _tapTap = Engine.GetSingleton("TapTap");
            _tapTap.Call("tapTapInit", _clientId, _clientToken);
            _tapTap.Connect("login_success",
                Callable.From((bool success, string name, string openId) =>
                {
                    GD.Print("login_result ", success, name, openId);
                    EmitSignal(SignalName.LoginResult, success, name, openId);
                }));

            _tapTap.Connect("taptap_compliance",
                Callable.From((bool success) => { GD.Print("taptap_compliance 合规认证 ", success); }));

            _tapTap.Connect("leaderboard_scores", Callable.From((string result) => { GD.Print("排行榜查询结果:", result); }));
        }
        else
        {
            GD.Print("TapTap 插件未加载");
        }
    }

    public void TapTapLogin()
    {
        _tapTap.Call("tapTapLogin");
    }

    //合规认证
    public void TapTapCompliance()
    {
        _tapTap.Call("tapTapCompliance");
    }

    //退出登录
    public void TapTapLogout()
    {
        _tapTap.Call("logout");
    }

    //退出合规认证
    public void ComplianceExit()
    {
        _tapTap.Call("exitCompliance");
    }


    //打开排行榜页面
    public void OpenLeaderboardPage(string id, string type = "public")
    {
        _tapTap.Call("openLeaderboardPage", id, type);
    }

    //更新排行榜分数
    public void UpdateLeaderboardScore(string id, long score)
    {
        _tapTap.Call("submitScore", id, score);
    }

    //获取排行榜分数
    public void GetLeaderboardScore(string id)
    {
        _tapTap.Call("GetLeaderboardScores", id);
    }
}