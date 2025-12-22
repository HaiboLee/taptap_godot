@tool
extends EditorPlugin

var export_plugin : AndroidExportPlugin

func _enter_tree():
	export_plugin = AndroidExportPlugin.new()
	add_export_plugin(export_plugin)
	add_autoload_singleton("TapTapPlugin","res://addons/taptap/TapTapGodot.cs")
	print("添加包")
	
func _exit_tree():
	# Clean-up of the plugin goes here.
	remove_export_plugin(export_plugin)
	export_plugin = null	
	remove_autoload_singleton("TapTapPlugin")
	print("移除包")

	
class AndroidExportPlugin extends EditorExportPlugin:
	# TODO: Update to your plugin's name.
	var _plugin_name = "TapTap"

	func _supports_platform(platform):
		if platform is EditorExportPlatformAndroid:
			return true
		return false

	func _get_android_libraries(platform, debug):
		var libs = []
		if debug:
			libs.append("taptap/lib/TapTap-debug.aar")
		else:
			libs.append("taptap/lib/TapTap-release.aar")
		return PackedStringArray(libs)
			
			

	func _get_android_dependencies(platform, debug):
		# TODO: Add remote dependices here.
		var array = ["com.taptap.sdk:tap-core:4.9.2","com.taptap.sdk:tap-login:4.9.2"]
		return PackedStringArray(array)

	func _get_name():
		return _plugin_name	
