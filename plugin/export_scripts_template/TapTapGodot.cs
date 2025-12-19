using Godot;
using System;

public partial class TapTapGodot : Node
{
    public override void _Ready()
    {
        base._Ready();
        GD.Print("TapTap 插件初始化");
        if (Engine.HasSingleton("TapTap"))
        {
            GD.Print("---准备打印---");
            var plugin = Engine.GetSingleton("TapTap");
            var result = plugin.Call("helloWorld");
            GD.Print("登录返回:",result);
        }
        else
        {
            GD.Print("TapTap 插件未加载");
            DebugAvailableSingletons();

        }
    }

    private void DebugAvailableSingletons()
    {
        GD.Print("📋 可用的引擎单例:");
        try
        {
            var singletons = Engine.GetSingletonList();
            foreach (var singleton in singletons)
            {
                GD.Print($"  - {singleton}");
            }
        }
        catch (Exception ex)
        {
            GD.Print($"获取单例列表失败: {ex.Message}");
        }
    }

}