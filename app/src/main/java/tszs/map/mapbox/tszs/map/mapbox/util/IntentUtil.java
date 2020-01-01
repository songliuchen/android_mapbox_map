package tszs.map.mapbox.tszs.map.mapbox.util;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import static android.R.attr.value;

/**
 * Created by songliuchen on 2018/4/21.
 * 用来打开系统视图面板
 * 例如选择文件对话框、摄像机、打开短信等
 */
public class IntentUtil
{
    //打开相机
    public static final int TAKE_PICTURE = 1;
    //选择文件
    public static final int FILE = 2;

    /**
     * 打开选择文件对话框
     * @param focused 当前activity对象
     * @param type 文件类型
     * @param title 文件标题
     */
    public static void openSelectFileIntent(Activity focused, String type, String title)
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/"+value);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            if(title == null || title.trim().length() == 0)
                title = "选择上传附件";
            focused.startActivityForResult(Intent.createChooser(intent, title),FILE);
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            Toast.makeText(focused,"未安装文件管理器",Toast.LENGTH_LONG).show();
        }
    }
}
