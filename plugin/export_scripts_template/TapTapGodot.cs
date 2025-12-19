using Godot;
using System;

public partial class TapTapGodot : Node
{
    private GodotObject _tapTap;
    
    public override void _Ready()
    {
        base._Ready();
        if (Engine.HasSingleton("TapTap"))
        {
            GD.Print("TapTap 插件初始化");
            _tapTap = Engine.GetSingleton("TapTap");
            _tapTap.Call("TapTapInit", "b0hnecor7odhsozvfe", "YVnPjPYe5v6awpnkeSXtNdiOh5UJdzuLmjt1NKJY");
            _tapTap.Connect("login_success", Callable.From((string name) =>
            {
                GD.Print("login_success ", name);
            }));

            _tapTap.Connect("login_fail", Callable.From((string name) =>
            {
                GD.Print("login_fail ", name);
            }));
        }
        else
        {
            GD.Print("TapTap 插件未加载");
        }
    }

    public void TapTapLogin()
    {
        _tapTap.Call("TapTapLogin");
    }

}