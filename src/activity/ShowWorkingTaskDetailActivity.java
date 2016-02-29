package activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.superxlcrnote.app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import broadcast.WidgetProvider;
import model.PersonAttr;
import model.SuperxlcrNoteDB;
import model.TestSetting;
import model.WorkingTask;
import util.DrawView;

/**
 * Created by Superxlcr
 * ��ʾδ�������ϸ�ڽ���
 */
public class ShowWorkingTaskDetailActivity extends Activity
        implements OnClickListener {

    // ��ͨ����view
    private TextView task_description;
    private TextView task_note;
    private TextView task_start_time;
    private TextView task_now_progress;
    private TextView task_total_progress;
    private LinearLayout task_reward;
    private ProgressBar task_progressbar;
    private Button confirm;

    // ���ؽ���view
    private LinearLayout finishRewardLayout;
    private LinearLayout finishRewardAttrLayout;
    private TextView person_level, person_gold;
    private TextView exp_now_progress, exp_total_progress;
    private ProgressBar person_exp_progressBar;

    // ����������
    private int now_progress, new_progress, total_progress;
    // �������Ŵ�ϵ��
    final private int progress_coefficient = 1000;
    // ��������λ
    private String unit;

    // ��Ч
    private SoundPool soundPool;
    private int soundId_start, soundId_end, soundId_finish, soundId_finish_reward;

    // ����ʵ��
    private int taskId;
    private WorkingTask task;

    // �洢���ݿ���SP
    private SuperxlcrNoteDB db = null;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    // �����������б�
    private List<String> rewardNameList = new ArrayList<String>();
    private List<Integer> rewardNumberList = new ArrayList<Integer>();
    private Map<String, List<TextView>> viewMap = new HashMap<String,
            List<TextView>>();
    private Iterator<String> iterator;
    private Iterator<Integer> iterator2;
    private String finishRewardName = "";
    private int finishRewardNumber = 0;

    // ������������Ŵ��С
    final int textBigSize = 20;

    // �Ƿ��޸ı�ע
    String intentNote = null;

    //��һ�μ�����ɽ���
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ����ȫ��ģʽ
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_working_task_detail);

        // ��ʼ��
        initial();
        // ��ȡ����ʵ��
        taskId = getIntent().getIntExtra("taskId", -1);
        if (taskId != -1) {
            // ��ʼ����ͨview����
            initialTaskView();
        } else { // ��ȡ����ʵ��ʧ��
            confirm.setVisibility(View.VISIBLE);
        }
    }

    // ����¼�
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.confirm:
            finish();
            break;
        }
    }

    // ���������Ϻ󣬼��ض�������Ч
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isFirst) {
            if (new_progress != now_progress) {
                // ��������Ч
                soundPool.play(soundId_start, 1, 1, 0, -1, 1.6f);
                // ����������
                ObjectAnimator oaProgressBar = ObjectAnimator
                        .ofInt(task_progressbar, "progress",
                                now_progress * progress_coefficient,
                                new_progress * progress_coefficient);
                // ���������ֶ���
                ValueAnimator vaTextView = ValueAnimator
                        .ofInt(now_progress, new_progress);
                vaTextView.addUpdateListener(
                        new ValueAnimator.AnimatorUpdateListener() {

                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                task_now_progress.setText(Integer.toString(
                                        (Integer) animation.getAnimatedValue()) +
                                        unit);
                            }
                        });
                // ���ö�������
                AnimatorSet as = new AnimatorSet();
                as.play(oaProgressBar).with(vaTextView);
                as.setDuration(2000);
                as.setInterpolator(new LinearInterpolator());
                as.start();
                // ������������
                as.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        progressAnimationFinish();
                    }
                });
            }
            // ���ñ�־λ
            isFirst = false;
        }
    }

    // �����ʼ��
    private void initial() {
        // �洢������ʼ��
        db = SuperxlcrNoteDB.getInstance(ShowWorkingTaskDetailActivity.this);
        // ȡ��sharedPreferences
        sp = getSharedPreferences("Person_info", 0);
        editor = sp.edit();

        // ��ʼ����ͨview����
        task_description = (TextView) findViewById(R.id.task_description);
        task_note = (TextView) findViewById(R.id.task_note);
        task_start_time = (TextView) findViewById(R.id.task_start_time);
        task_now_progress = (TextView) findViewById(R.id.task_now_progress);
        task_total_progress = (TextView) findViewById(R.id.task_total_progress);
        task_reward = (LinearLayout) findViewById(R.id.task_reward);
        task_progressbar = (ProgressBar) findViewById(R.id.task_progressBar);
        confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
        confirm.setVisibility(View.INVISIBLE);

        // ��ʼ������view����
        person_level = (TextView) findViewById(R.id.person_level);
        person_gold = (TextView) findViewById(R.id.person_gold);
        person_exp_progressBar = (ProgressBar) findViewById(
                R.id.person_exp_progressBar);
        exp_now_progress = (TextView) findViewById(R.id.exp_now_progress);
        exp_total_progress = (TextView) findViewById(R.id.exp_total_progress);
        finishRewardLayout = (LinearLayout) findViewById(R.id.finish_reward_layout);
        finishRewardAttrLayout = (LinearLayout) findViewById(
                R.id.finish_reward_attr_layout);
    }

    // ��ʼ����ͨView
    private void initialTaskView() {
        // ��ʼ������view����
        intentNote = getIntent().getStringExtra("note");
        if (intentNote != null) {
            db.changeWorking_taskNoteById(taskId, intentNote);
        }
        task = db.getWorking_taskById(taskId);
        task_description.setText(task.getDescription());
        task_note.setText(task.getNote());
        task_start_time.setText(task.getStartTime());
        unit = task.getUnit();

        // ��ʼ������������
        now_progress = task.getNowProgress();
        total_progress = task.getTotalProgress();
        task_now_progress.setText(Integer.toString(now_progress) + unit);
        task_total_progress.setText(Integer.toString(total_progress) + unit);
        // ������
        task_progressbar.setMax(total_progress * progress_coefficient);
        task_progressbar.setProgress(now_progress * progress_coefficient);
        new_progress = getIntent().getIntExtra("new_progress", now_progress);
        if (new_progress > total_progress) {
            new_progress = total_progress;
        } else if (new_progress < 0) {
            new_progress = 0;
        }

        // �����б����ɽ�������
        List<PersonAttr> list = new ArrayList<PersonAttr>();
        String[] reward = task.getReward().split("\\|");
        for (int i = 0; i < reward.length; i++) {
            String[] singleReward = reward[i].split("_");
            rewardNameList.add(singleReward[0]);
            rewardNumberList.add(Integer.parseInt(singleReward[1]));
            PersonAttr personAttr;
            if (singleReward[0].equals("���ֵ")) { // ���ֵ
                personAttr = new PersonAttr("���ֵ", Integer.parseInt(singleReward[1]),
                        "#fcfe66", R.drawable.exp, true, "");
            } else if (singleReward[0].equals("����")) { // ����
                personAttr = new PersonAttr("����", Integer.parseInt(singleReward[1]),
                        "#dde000", R.drawable.gold, true, "");
            } else {
                personAttr = db.getPersonAttrByName(singleReward[0]);
                personAttr.setNumber(Integer.parseInt(singleReward[1]));
            }
            list.add(personAttr);
        }
        // layout������ɺ���뽱����������
        final List<PersonAttr> finalList = list;
        task_reward.post(new Runnable() {
            @Override
            public void run() {
                DrawView.drawShowRewardLayout(ShowWorkingTaskDetailActivity.this,
                        task_reward, task_reward.getMeasuredWidth(), 90, 2, 10,
                        finalList, 0);
            }
        });

        // �������ؽ���
        initialTaskFinishView();

        // ����Ƿ��н��ȸı�
        if (new_progress == now_progress) { // ���Ȳ���
            confirm.setVisibility(View.VISIBLE);
        } else {
            // ��ʼ����Ч
            initialSoundPool();
            // ���������뷢�͹㲥����widget
            db.changeWorking_taskNowProgressById(taskId, new_progress,
                    new_progress - now_progress);
            // ˢ��widget
            Intent intent = new Intent(ShowWorkingTaskDetailActivity
                    .this, WidgetProvider.class);
            intent.putExtra("workingTask", db.getWorkingTaskNumber());
            intent.putExtra("workingTaskTodayWork",
                    db.getWorkingTaskTodayWorkNumber());
            sendBroadcast(intent);
        }
    }

    // ��ʼ����Ч
    private void initialSoundPool() {
        // ��Ч���ã���ͼ��Ϻ��ٴ�����Ч��ֹ���ٳ��֣�
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        soundId_start = soundPool
                .load(getApplicationContext(), R.raw.count_start, 1);
        soundId_end = soundPool.load(getApplicationContext(), R.raw.count_end, 1);
        soundId_finish = soundPool
                .load(getApplicationContext(), R.raw.task_finish, 1);
        soundId_finish_reward = soundPool
                .load(getApplicationContext(), R.raw.sound_finish_reward, 1);
    }

    // �����������󶯻�
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 0) { // ��������
                if (finishRewardName.equals("���ֵ")) {
                    int level = Integer.parseInt(person_level.getText().toString());
                    int now_exp = Integer
                            .parseInt(exp_now_progress.getText().toString());
                    int total_exp = level * 10 + 50;
                    now_exp += 1;
                    if (now_exp >= total_exp) {
                        level += 1;
                        now_exp -= total_exp;
                        person_level.setText(Integer.toString(level));
                        total_exp = level * 10 + 50;
                        exp_total_progress.setText(Integer.toString(total_exp));
                        person_exp_progressBar.setMax(total_exp);
                    }
                    person_exp_progressBar.setProgress(now_exp);
                    exp_now_progress.setText(Integer.toString(now_exp));
                } else if (finishRewardName.equals("����")) {
                    int gold = Integer.parseInt(person_gold.getText().toString());
                    gold++;
                    person_gold.setText(Integer.toString(gold));
                    if (finishRewardNumber > 1) {
                        // ���ַŴ�
                        person_gold.setTextSize(textBigSize);
                    } else {
                        // ��С��ԭ
                        person_gold.setTextSize(14);
                    }

                } else if (viewMap.containsKey(finishRewardName)) {
                    List<TextView> list = viewMap.get(finishRewardName);
                    TextView name = list.get(0);
                    TextView numberTv = list.get(2);

                    int number = Integer.parseInt(numberTv.getText().toString());
                    number++;
                    numberTv.setText(Integer.toString(number));
                    if (finishRewardNumber > 1) {
                        // ���ַŴ�
                        numberTv.setTextSize(textBigSize);
                    } else {
                        // ��С��ԭ
                        numberTv.setTextSize(14);
                    }
                } else {
                    finishRewardNumber = 1;
                }

                finishRewardNumber -= 1;
                post(runnable);
            } else { // ��������
                handler.removeCallbacks(runnable);
                confirm.setVisibility(View.VISIBLE);
            }
        }
    };

    // ������ɺ󶯻�
    private final Runnable runnable = new Runnable() {

        @Override
        public void run() {
            soundPool.play(soundId_finish_reward, 1, 1, 1, 0, 1);
            Message message = handler.obtainMessage();
            if (finishRewardNumber == 0) {
                if (iterator.hasNext()) {
                    finishRewardName = iterator.next();
                    finishRewardNumber = iterator2.next();
                    message.arg1 = 0;
                } else {
                    message.arg1 = 1;
                }
            } else {
                message.arg1 = 0;
            }
            try {
                Thread.sleep(250);
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendMessage(message);
        }
    };

    // �������������������
    private void progressAnimationFinish() {
        // ��ͣ��������Ч�����������Ч
        soundPool.stop(soundId_start);
        soundPool.play(soundId_end, 1, 1, 0, 0, 1);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // ��������Ƿ����
        if (new_progress == total_progress) { // ���
            // չʾ���ؽ���
            finishRewardLayout.setVisibility(View.VISIBLE);

            // �������������Ч
            soundPool.play(soundId_finish, 1, 1, 0, 0, 1);
            db.deleteWorking_task(task);
            String finish_time = Long.toString(System.currentTimeMillis());
            db.saveFinish_task(task.getDescription(), task.getTotalProgress(),
                    task.getUnit(), task.getReward(), task.getOriginalStartTime(),
                    finish_time);

            // ˢ��widget
            Intent intent = new Intent(ShowWorkingTaskDetailActivity.this,
                    WidgetProvider.class);
            intent.putExtra("workingTask", db.getWorkingTaskNumber());
            intent.putExtra("workingTaskTodayWork",
                    db.getWorkingTaskTodayWorkNumber());
            sendBroadcast(intent);

            // Ӌ�㽱��
            for (int i = 0; i < rewardNameList.size(); i++) {
                String name = rewardNameList.get(i);
                if (!TestSetting.test) {
                    if (name.equals("���ֵ")) {
                        int level = sp.getInt("level", 0);
                        int now_exp = sp.getInt("now_exp", 0);
                        int total_exp = level * 10 + 50;
                        now_exp += rewardNumberList.get(i);
                        if (now_exp >= total_exp) {
                            level += 1;
                            editor.putInt("level", level);
                            now_exp -= total_exp;
                        }
                        editor.putInt("now_exp", now_exp);
                        editor.commit();
                    } else if (name.equals("����")) {
                        int gold = sp.getInt("gold", 0);
                        gold += rewardNumberList.get(i);
                        editor.putInt("gold", gold);
                        editor.commit();
                    } else {
                        db.updatePersonAttrByName(name, rewardNumberList.get(i));
                    }
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // �������뽱����Ŀ������
            iterator = rewardNameList.iterator();
            iterator2 = rewardNumberList.iterator();
            // ��ʼ������ɶ���
            handler.post(runnable);

        } else { // ����δ���
            soundPool.unload(soundId_start);
            soundPool.unload(soundId_end);
            soundPool.unload(soundId_finish);
            confirm.setVisibility(View.VISIBLE);
        }
    }

    private void initialTaskFinishView() {
        int level = sp.getInt("level", 0);
        int exp_now = sp.getInt("now_exp", 0);
        int exp_total = level * 10 + 50;
        int gold = sp.getInt("gold", 0);
        person_level.setText(Integer.toString(level));
        // exp
        person_exp_progressBar.setProgress(exp_now);
        person_exp_progressBar.setMax(exp_total);
        exp_now_progress.setText(Integer.toString(exp_now));
        exp_total_progress.setText(Integer.toString(exp_total));
        // gold
        person_gold.setText(Integer.toString(gold));

        //���ƽ�������
        List<PersonAttr> list = new ArrayList<PersonAttr>();
        String[] reward = task.getReward().split("\\|");
        for (int i = 0; i < reward.length; i++) {
            String[] singleReward = reward[i].split("_");
            if (!(singleReward[0].equals("���ֵ") || singleReward[0].equals("����"))) {
                list.add(db.getPersonAttrByName(singleReward[0]));
            }
        }
        // ��layout��ɺ������ƽ�������
        final List<PersonAttr> finalList2 = list;
        finishRewardAttrLayout.post(new Runnable() {
            @Override
            public void run() {
                viewMap = DrawView
                        .drawShowRewardLayout(ShowWorkingTaskDetailActivity.this,
                                finishRewardAttrLayout,
                                finishRewardAttrLayout.getMeasuredWidth(), 90, 2, 10,
                                finalList2, 1);
                // ������ϸ�Ϊ���ɼ�
                finishRewardLayout.setVisibility(View.GONE);
            }
        });

    }

}
