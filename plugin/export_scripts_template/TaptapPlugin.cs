using Godot;

public partial class TapTapPlugin : Node
{
    public override void _Ready()
    {
        if (Engine.HasSingleton("TapTap"))
        {
            var plugin = Engine.GetSingleton("TapTap");
            plugin.Call("helloWorld");  // 调用 Java 插件方法
        }
    }

    public string TapTapLogin(){
        GD.Print("taptap 登陆");
        return "测试登陆taptap";
    }
}
