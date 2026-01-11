package org.godotengine.plugin.android.template;

import android.support.annotation.NonNull;
import android.util.Log;

import com.taptap.sdk.cloudsave.ArchiveData;
import com.taptap.sdk.cloudsave.ArchiveMetadata;
import com.taptap.sdk.cloudsave.TapTapCloudSave;
import com.taptap.sdk.cloudsave.internal.TapCloudSaveRequestCallback;

import org.godotengine.godot.Godot;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;


//玩家云存档
public class GodotTapTapCloudSave {

    private static final int createCloud = 1; //创建云存档
    private static final int CloudList = 2;//云存档列表
    private static final int DownloadCloudSave = 3;//下载云存档

    private static final int UpdateCloudSave = 4; //更新云存档

    private static final int DeleteCloudSave = 5;//删除云存档

    private TaptapPlugin taptap;

    GodotTapTapCloudSave(TaptapPlugin _taptap) {
        this.taptap = _taptap;
    }


    //创建云存档
    protected void CreateCloudSave(String filePath, String name, String des) {
        // 存档元信息
        ArchiveMetadata metadata = new ArchiveMetadata.Builder()
                .setName(name)
                .setSummary(des)
                .setExtra("像素拉力赛玩家存档")
                .setPlaytime(0)
                .build();

        // 存档文件路径（单个存档文件大小不超过10MB）
        String archiveFilePath = filePath;
        // 存档封面路径（可选，封面大小不超过512KB）
        String archiveCoverPath = "";

        TapTapCloudSave.createArchive(metadata, archiveFilePath, archiveCoverPath, callback);
    }

    //获取云存档列表
    protected void GetCloudSaveList() {
        TapTapCloudSave.getArchiveList(callback);
    }

    //下载云存档
    protected void DownloadCloudSave(String uuid, String fileId) {
        TapTapCloudSave.getArchiveData(uuid, fileId, callback);
    }

    //更新云文档
    protected void UpdateCloudSave(String uuid, String path, String name) {
        ArchiveMetadata metadata = new ArchiveMetadata.Builder()
                .setName(name)
                .setSummary("存档描述")
                .setExtra("像素拉力赛玩家存档更新")
                .setPlaytime(0)
                .build();
        TapTapCloudSave.updateArchive(uuid, metadata, path, "", callback);
    }

    protected void DeleteCloudSave(String uuid) {
        TapTapCloudSave.deleteArchive(uuid, callback);
    }


    // 请求回调
    TapCloudSaveRequestCallback callback = new TapCloudSaveRequestCallback() {
        @Override
        public void onArchiveCreated(@NonNull ArchiveData archive) {
            // 处理存档创建成功
            Log.i("taptap", "玩家存档创建成功");
            taptap.TapTapEmitSignal("cloud_save", createCloud, true, "创建成功");
        }

        @Override
        public void onArchiveDataResult(@androidx.annotation.NonNull byte[] bytes) {
            taptap.TapTapEmitSignal("cloud_save", DownloadCloudSave, true, new String(bytes, StandardCharsets.UTF_8));
        }

        @Override
        public void onArchiveListResult(@androidx.annotation.NonNull List<ArchiveData> list) {

            JSONArray arr = new JSONArray();
            for (ArchiveData s : list) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("name", s.getName());
                    obj.put("fileId", s.getFileId());
                    obj.put("uuid", s.getUuid());
                    obj.put("update_time", s.getModifiedTime());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                arr.put(obj);
            }
            taptap.TapTapEmitSignal("cloud_save", CloudList, true, arr.toString());
        }

        @Override
        public void onArchiveDeleted(@androidx.annotation.NonNull ArchiveData archiveData) {
            taptap.TapTapEmitSignal("cloud_save", DeleteCloudSave, true, "删除成功");
        }

        @Override
        public void onArchiveUpdated(@androidx.annotation.NonNull ArchiveData archiveData) {
            taptap.TapTapEmitSignal("cloud_save", UpdateCloudSave, true, "更新成功");
        }

        @Override
        public void onArchiveCoverResult(@androidx.annotation.NonNull byte[] bytes) {

        }

        @Override
        public void onRequestError(int errorCode, @NonNull String errorMessage) {
            // 处理请求错误
            Log.e("taptap", "玩家存档创建错误" + errorMessage);
            taptap.TapTapEmitSignal("cloud_save", createCloud, false, "创建失败" + errorMessage);
        }

    };

}
