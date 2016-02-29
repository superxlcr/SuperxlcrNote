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
 * ������Ϣ�������
 */
public class PersonInfoActivity {
    private Activity activity = null;

    // ��������ҳ��view
    private ImageView person_face, person_sex_image;
    private TextView person_name, person_sex_text, person_level, person_gold;
    private TextView exp_now_progress, exp_total_progress;
    private ProgressBar person_exp_progressBar;
    public LinearLayout person_reward;

    // �����������������������view
    private ImageView person_face_tab_working_task, person_face_tab_finish_task,
            person_face_tab_daily_task;
    private TextView person_name_tab_working_task, person_name_tab_finish_task,
            person_name_tab_daily_task;
    private TextView person_level_tab_working_task, person_level_tab_finish_task,
            person_level_tab_daily_task;

    private String[] items = new String[]{"ѡ�񱾵�ͼƬ", "����"};
    /* ͷ������ */
    private static final String IMAGE_FILE_NAME = "personHead.jpg";

    /* ������ */
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;

    private List<PersonAttr> list = null;

    // ���ݴ洢��
    private SuperxlcrNoteDB db;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public PersonInfoActivity(Activity activity) {
        this.activity = activity;
    }

    public void onCreate() {

        // ��ʼ�����ݿ�
        db = SuperxlcrNoteDB.getInstance(activity);

        // ȡ��sharedPreferences
        sp = activity.getSharedPreferences("Person_info", 0);
        editor = sp.edit();

        // ��ʼ��������Ϣҳview
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

        // ��ʼ������view
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

        // ˢ��view����
        refresh_person_info();

        // ͷ����
        person_face.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showGetPhotoDialog();
            }
        });

        // ���Ƶ��
        person_name.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final EditText et = new EditText(activity);
                new AlertDialog.Builder(activity).setTitle("��������")
                        .setIcon(android.R.drawable.ic_dialog_info).setView(et)
                        .setPositiveButton("�_��",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        String name = et.getText().toString();
                                        if (name.isEmpty()) {
                                            Toast.makeText(activity, "������գ�����ʧ����",
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
                                }).setNegativeButton("ȡ��", null).show();
            }
        });

        // �Ա���
        person_sex_text.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity).setTitle("�����Ԅe")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setItems(new String[]{"δ֪", "��", "Ů"},
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
                                            person_sex_text.setText("δ֪");
                                            break;
                                        case 1:
                                            person_sex_image.setImageResource(
                                                    R.drawable.sex_01);
                                            person_sex_text.setText("��");
                                            break;
                                        case 2:
                                            person_sex_image.setImageResource(
                                                    R.drawable.sex_02);
                                            person_sex_text.setText("Ů");
                                            break;
                                        default:
                                            break;
                                        }
                                    }
                                }).setNegativeButton("ȡ��", null).show();
            }
        });
    }

    public void onResume() {
        refresh_person_info();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // ����벻����ȡ��ʱ��
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
                    Toast.makeText(activity, "δ�ҵ��洢�����޷��洢��Ƭ��", Toast.LENGTH_LONG)
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

    // ˢ��view����
    private void refresh_person_info() {
        String name = sp.getString("name", "");
        int sex = sp.getInt("sex", 0); // 0 δ֪ �� 1 �� �� 2Ů
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
            person_sex_text.setText("δ֪");
            break;
        case 1:
            person_sex_image.setImageResource(R.drawable.sex_01);
            person_sex_text.setText("��");
            break;
        case 2:
            person_sex_image.setImageResource(R.drawable.sex_02);
            person_sex_text.setText("Ů");
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

        // ���ɽ�������
        list = db.getPersonAttr();
        // ���������,���Ҽ��40dp
        final int deviceWidth = activity.getResources()
                .getDisplayMetrics().widthPixels;
        final float scale = activity.getResources().getDisplayMetrics().density;
        DrawView.drawShowRewardLayout(activity, person_reward,
                (deviceWidth - (int) (80 * scale + 0.5f)), 90, 2, 10, list, 1);
    }

    /**
     * ��ʾͷ��ѡ��Ի���
     */
    private void showGetPhotoDialog() {

        new AlertDialog.Builder(activity).setTitle("����ͷ��")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                        case 0:
                            Intent intentFromGallery = new Intent();
                            intentFromGallery.setType("image/*"); // �����ļ�����
                            intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                            activity.startActivityForResult(intentFromGallery,
                                    IMAGE_REQUEST_CODE);
                            break;
                        case 1:

                            Intent intentFromCapture = new Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE);
                            // �жϴ洢���Ƿ�����ã����ý��д洢
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
                }).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

    /**
     * �ü�ͼƬ����ʵ��
     *
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // ���òü�
        intent.putExtra("crop", "true");
        // aspectX aspectY �ǿ�ߵı���
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY �ǲü�ͼƬ���
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, 2);
    }

    /**
     * ����ü�֮���ͼƬ����
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
