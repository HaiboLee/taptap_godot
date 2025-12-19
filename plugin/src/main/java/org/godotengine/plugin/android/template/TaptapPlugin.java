package org.godotengine.plugin.android.template;

import android.util.Log;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.UsedByGodot;

public class TaptapPlugin extends GodotPlugin {
    /**
     * Base constructor passing a {@link Godot} instance through which the plugin can access Godot's
     * APIs and lifecycle events.
     *
     * @param godot
     */
    public TaptapPlugin(Godot godot) {
        super(godot);
    }

    @Override
    public String getPluginName() {
        return "TapTap";
    }

    @UsedByGodot
    public String helloWorld(){
        Log.d("taptap","hello from taptap plugin");
        return "Hello Taptap Godot";
    }
}
