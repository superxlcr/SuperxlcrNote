package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.superxlcrnote.app.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import model.PersonAttr;
import model.SuperxlcrNoteDB;
import util.DrawView;
import util.SDCardCheckTools;

/**
 * Created by Superxlcr
 * 个人信息界面的类
 */
public class PersonInfoActivity {
    private Activity activity = null;

    // 个人数据页面view
    private ImageView person_face, person_sex_image;
    private TextView person_name, person_sex_text, person_level, person_gold;
    private TextView exp_now_progress, exp_total_progress;
    private ProgressBar person_exp_progressBar;
    public LinearLayout person_reward;

    // 进行任务和完成任务个人数据view
    private ImageView person_face_tab_working_task, person_face_tab_finish_task,
            person_face_tab_daily_task;
    private TextView person_name_tab_working_task, person_name_tab_finish_task,
            person_name_tab_daily_task;
    private TextView person_level_tab_working_task, person_level_tab_finish_task,
            person_level_tab_daily_task;

    private String[] items = new String[]{"选择本地图片", "拍照"};
    /* 头像名称 */
    private static final String IMAGE_FILE_NAME = "personHead.jpg";

    /* 请求码 */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;

    private List<PersonAttr> list = null;

    // 数据存储类
    private SuperxlcrNoteDB db;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public PersonInfoActivity(Activity activity) {
        this.activity = activity;
    }

    public void onCreate() {

        // 初始化数据库
        db = SuperxlcrNoteDB.getInstance(activity);

        // 取得sharedPreferences
        sp = activity.getSharedPreferences("Person_info", 0);
        editor = sp.edit();

        // 初始化个人信息页view
        person_face = (ImageView) activity.findViewById(R.id.person_face);
        person_name = (TextView) activity.findViewById(R.id.person_name);
        person_sex_image = (ImageView) activity.findViewById(R.id.person_sex_image);
        person_sex_text = (TextView) activity.findViewById(R.id.person_sex_text);
        person_level = (TextView) activity.findViewById(R.id.person_level);
        person_gold = (TextView) activity.findViewById(R.id.person_gold);
        person_exp_progressBar = (ProgressBar) activity
                .findViewById(R.id.person_exp_progressBar);
        exp_now_progress = (TextView) activity.findViewById(R.id.exp_now_progress);
        exp_total_progress = (TextView) activity
                .findViewById(R.id.exp_total_progress);
        person_reward = (LinearLayout) activity.findViewById(R.id.person_reward);

        // 初始化顶部view
        person_face_tab_working_task = (ImageView) activity
                .findViewById(R.id.person_face_tab_working_task);
        person_face_tab_finish_task = (ImageView) activity
                .findViewById(R.id.person_face_tab_finish_task);
        person_face_tab_daily_task = (ImageView) activity
                .findViewById(R.id.person_face_tab_daily_task);
        person_name_tab_working_task = (TextView) activity
                .findViewById(R.id.person_name_tab_working_task);
        person_name_tab_finish_task = (TextView) activity
                .findViewById(R.id.person_name_tab_finish_task);
        person_name_tab_daily_task = (TextView) activity
                .findViewById(R.id.person_name_tab_daily_task);
        person_level_tab_working_task = (TextView) activity
                .findViewById(R.id.person_level_tab_working_task);
        person_level_tab_finish_task = (TextView) activity
                .findViewById(R.id.person_level_tab_finish_task);
        person_level_tab_daily_task = (TextView) activity
                .findViewById(R.id.person_level_tab_daily_task);

        // 刷新view数据
        refresh_person_info();

        // 头像点击
        person_face.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showGetPhotoDialog();
            }
        });

        // 名称点击
        person_name.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final EditText et = new EditText(activity);
                new AlertDialog.Builder(activity).setTitle("更改姓名")
                        .setIcon(android.R.drawable.ic_dialog_info).setView(et)
                        .setPositiveButton("確定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        String name = et.getText().toString();
                                        if (name.isEmpty()) {
                                            Toast.makeText(activity, "姓名為空，更改失敗！",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            editor.putString("name", name);
                                            editor.commit();
                                            person_name.setText(name);
                                            person_name_tab_working_task
                                                    .setText(name);
                                            person_name_tab_finish_task
                                                    .setText(name);
                                            person_name_tab_daily_task.setText(name);
                                        }
                                    }
                                }).setNegativeButton("取消", null).show();
            }
        });

        // 性别点击
        person_sex_text.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity).setTitle("更改性別")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setItems(new String[]{"未知", "男", "女"},
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        editor.putInt("sex", which);
                                        editor.commit();
                                        switch (which) {
                                        case 0:
                                            person_sex_image.setImageResource(
                                                    R.drawable.sex_00);
                                            person_sex_text.setText("未知");
                                            break;
                                        case 1:
                                            person_sex_image.setImageResource(
                                                    R.drawable.sex_01);
                                            person_sex_text.setText("男");
                                            break;
                                        case 2:
                                            person_sex_image.setImageResource(
                                                    R.drawable.sex_02);
                                            person_sex_text.setText("女");
                                            break;
                                        default:
                                            break;
                                        }
                                    }
                                }).setNegativeButton("取消", null).show();
            }
        });
    }

    public void onResume() {
        refresh_person_info();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 结果码不等于取消时候
        if (resultCode != Activity.RESULT_CANCELED) {

            switch (requestCode) {
            case IMAGE_REQUEST_CODE:
                startPhotoZoom(data.getData());
                break;
            case CAMERA_REQUEST_CODE:
                if (SDCardCheckTools.hasSdcard()) {
                    File tempFile = new File(
                            Environment.getExternalStorageDirectory() +
                                    "/SuperxlcrNote/headPhoto/" + IMAGE_FILE_NAME);
                    startPhotoZoom(Uri.fromFile(tempFile));
                } else {
                    Toast.makeText(activity, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG)
                            .show();
                }

                break;
            case RESULT_REQUEST_CODE:
                if (data != null) {
                    getImageToView(data);
                }
                break;
            }
        }
    }

    // 刷新view数据
    private void refresh_person_info() {
        String name = sp.getString("name", "");
        int sex = sp.getInt("sex", 0); // 0 未知 ， 1 男 ， 2女
        int level = sp.getInt("level", 0);
        int exp_now = sp.getInt("now_exp", 0);
        int exp_total = level * 10 + 50;
        int gold = sp.getInt("gold", 0);
        File headFile = new File(Environment.getExternalStorageDirectory() +
                "/SuperxlcrNote/headPhoto/" + IMAGE_FILE_NAME);
        if (headFile.exists()) {
            Drawable drawable = new BitmapDrawable(
                    BitmapFactory.decodeFile(headFile.toString()));
            person_face.setImageDrawable(drawable);
            person_face_tab_working_task.setImageDrawable(drawable);
            person_face_tab_finish_task.setImageDrawable(drawable);
            person_face_tab_daily_task.setImageDrawable(drawable);
        }
        if (!name.equals("")) {
            person_name.setText(name);
            person_name_tab_working_task.setText(name);
            person_name_tab_finish_task.setText(name);
            person_name_tab_daily_task.setText(name);
        }
        switch (sex) {
        case 0:
            person_sex_image.setImageResource(R.drawable.sex_00);
            person_sex_text.setText("未知");
            break;
        case 1:
            person_sex_image.setImageResource(R.drawable.sex_01);
            person_sex_text.setText("男");
            break;
        case 2:
            person_sex_image.setImageResource(R.drawable.sex_02);
            person_sex_text.setText("女");
            break;
        default:
            break;
        }
        person_level.setText(Integer.toString(level));
        person_level_tab_working_task.setText(Integer.toString(level));
        person_level_tab_finish_task.setText(Integer.toString(level));
        person_level_tab_daily_task.setText(Integer.toString(level));
        // exp
        person_exp_progressBar.setProgress(exp_now);
        person_exp_progressBar.setMax(exp_total);
        exp_now_progress.setText(Integer.toString(exp_now));
        exp_total_progress.setText(Integer.toString(exp_total));

        // gold
        person_gold.setText(Integer.toString(gold));

        // 生成奖励界面
        list = db.getPersonAttr();
        // 计算界面宽度,左右间隔40dp
        final int deviceWidth = activity.getResources()
                .getDisplayMetrics().widthPixels;
        final float scale = activity.getResources().getDisplayMetrics().density;
        DrawView.drawShowRewardLayout(activity, person_reward,
                (deviceWidth - (int) (80 * scale + 0.5f)), 90, 2, 10, list, 1);
    }

    /**
     * 显示头像选择对话框
     */
    private void showGetPhotoDialog() {

        new AlertDialog.Builder(activity).setTitle("设置头像")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                        case 0:
                            Intent intentFromGallery = new Intent();
                            intentFromGallery.setType("image/*"); // 设置文件类型
                            intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                            activity.startActivityForResult(intentFromGallery,
                                    IMAGE_REQUEST_CODE);
                            break;
                        case 1:

                            Intent intentFromCapture = new Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE);
                            // 判断存储卡是否可以用，可用进行存储
                            if (SDCardCheckTools.hasSdcard()) {
                                File destDir = new File(
                                        Environment.getExternalStorageDirectory() +
                                                "/SuperxlcrNote/headPhoto");
                                if (!destDir.exists()) {
                                    destDir.mkdirs();
                                }
                                intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(new File(Environment
                                                .getExternalStorageDirectory() +
                                                "/SuperxlcrNote/headPhoto/" +
                                                IMAGE_FILE_NAME)));
                            }

                            activity.startActivityForResult(intentFromCapture,
                                    CAMERA_REQUEST_CODE);
                            break;
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, 2);
    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            try {
                FileOutputStream fos = null;
                String filename = Environment.getExternalStorageDirectory() +
                        "/SuperxlcrNote/headPhoto/" + IMAGE_FILE_NAME;
                fos = new FileOutputStream(filename);
                photo.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (java.io.IOException ex) {
                ex.printStackTrace();
            }
            Drawable drawable = new BitmapDrawable(photo);
            person_face.setImageDrawable(drawable);
            person_face_tab_working_task.setImageDrawable(drawable);
            person_face_tab_finish_task.setImageDrawable(drawable);
            person_face_tab_daily_task.setImageDrawable(drawable);
        }
    }

}
