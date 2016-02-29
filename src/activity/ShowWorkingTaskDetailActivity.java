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
 * 显示未完成任务细节界面
 */
public class ShowWorkingTaskDetailActivity extends Activity
        implements OnClickListener {

    // 普通界面view
    private TextView task_description;
    private TextView task_note;
    private TextView task_start_time;
    private TextView task_now_progress;
    private TextView task_total_progress;
    private LinearLayout task_reward;
    private ProgressBar task_progressbar;
    private Button confirm;

    // 隐藏界面view
    private LinearLayout finishRewardLayout;
    private LinearLayout finishRewardAttrLayout;
    private TextView person_level, person_gold;
    private TextView exp_now_progress, exp_total_progress;
    private ProgressBar person_exp_progressBar;

    // 进度条变量
    private int now_progress, new_progress, total_progress;
    // 进度条放大系数
    final private int progress_coefficient = 1000;
    // 进度条单位
    private String unit;

    // 声效
    private SoundPool soundPool;
    private int soundId_start, soundId_end, soundId_finish, soundId_finish_reward;

    // 任务实例
    private int taskId;
    private WorkingTask task;

    // 存储数据库与SP
    private SuperxlcrNoteDB db = null;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    // 任务奖励内容列表
    private List<String> rewardNameList = new ArrayList<String>();
    private List<Integer> rewardNumberList = new ArrayList<Integer>();
    private Map<String, List<TextView>> viewMap = new HashMap<String,
            List<TextView>>();
    private Iterator<String> iterator;
    private Iterator<Integer> iterator2;
    private String finishRewardName = "";
    private int finishRewardNumber = 0;

    // 奖励界面字体放大大小
    final int textBigSize = 20;

    // 是否修改备注
    String intentNote = null;

    //第一次加载完成界面
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 开启全屏模式
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_working_task_detail);

        // 初始化
        initial();
        // 获取任务实例
        taskId = getIntent().getIntExtra("taskId", -1);
        if (taskId != -1) {
            // 初始化普通view数据
            initialTaskView();
        } else { // 获取任务实例失败
            confirm.setVisibility(View.VISIBLE);
        }
    }

    // 点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.confirm:
            finish();
            break;
        }
    }

    // 界面加载完毕后，加载动画与音效
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isFirst) {
            if (new_progress != now_progress) {
                // 进度条音效
                soundPool.play(soundId_start, 1, 1, 0, -1, 1.6f);
                // 进度条动画
                ObjectAnimator oaProgressBar = ObjectAnimator
                        .ofInt(task_progressbar, "progress",
                                now_progress * progress_coefficient,
                                new_progress * progress_coefficient);
                // 进度条文字动画
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
                // 设置动画属性
                AnimatorSet as = new AnimatorSet();
                as.play(oaProgressBar).with(vaTextView);
                as.setDuration(2000);
                as.setInterpolator(new LinearInterpolator());
                as.start();
                // 当动画结束后
                as.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        progressAnimationFinish();
                    }
                });
            }
            // 设置标志位
            isFirst = false;
        }
    }

    // 对象初始化
    private void initial() {
        // 存储变量初始化
        db = SuperxlcrNoteDB.getInstance(ShowWorkingTaskDetailActivity.this);
        // 取得sharedPreferences
        sp = getSharedPreferences("Person_info", 0);
        editor = sp.edit();

        // 初始化普通view对象
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

        // 初始化隐藏view对象
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

    // 初始化普通View
    private void initialTaskView() {
        // 初始化各项view内容
        intentNote = getIntent().getStringExtra("note");
        if (intentNote != null) {
            db.changeWorking_taskNoteById(taskId, intentNote);
        }
        task = db.getWorking_taskById(taskId);
        task_description.setText(task.getDescription());
        task_note.setText(task.getNote());
        task_start_time.setText(task.getStartTime());
        unit = task.getUnit();

        // 初始化进度条数据
        now_progress = task.getNowProgress();
        total_progress = task.getTotalProgress();
        task_now_progress.setText(Integer.toString(now_progress) + unit);
        task_total_progress.setText(Integer.toString(total_progress) + unit);
        // 进度条
        task_progressbar.setMax(total_progress * progress_coefficient);
        task_progressbar.setProgress(now_progress * progress_coefficient);
        new_progress = getIntent().getIntExtra("new_progress", now_progress);
        if (new_progress > total_progress) {
            new_progress = total_progress;
        } else if (new_progress < 0) {
            new_progress = 0;
        }

        // 生成列表并生成奖励界面
        List<PersonAttr> list = new ArrayList<PersonAttr>();
        String[] reward = task.getReward().split("\\|");
        for (int i = 0; i < reward.length; i++) {
            String[] singleReward = reward[i].split("_");
            rewardNameList.add(singleReward[0]);
            rewardNumberList.add(Integer.parseInt(singleReward[1]));
            PersonAttr personAttr;
            if (singleReward[0].equals("經驗值")) { // 經驗值
                personAttr = new PersonAttr("經驗值", Integer.parseInt(singleReward[1]),
                        "#fcfe66", R.drawable.exp, true, "");
            } else if (singleReward[0].equals("金幣")) { // 金幣
                personAttr = new PersonAttr("金幣", Integer.parseInt(singleReward[1]),
                        "#dde000", R.drawable.gold, true, "");
            } else {
                personAttr = db.getPersonAttrByName(singleReward[0]);
                personAttr.setNumber(Integer.parseInt(singleReward[1]));
            }
            list.add(personAttr);
        }
        // layout绘制完成后加入奖励界面内容
        final List<PersonAttr> finalList = list;
        task_reward.post(new Runnable() {
            @Override
            public void run() {
                DrawView.drawShowRewardLayout(ShowWorkingTaskDetailActivity.this,
                        task_reward, task_reward.getMeasuredWidth(), 90, 2, 10,
                        finalList, 0);
            }
        });

        // 加载隐藏界面
        initialTaskFinishView();

        // 检测是否有进度改变
        if (new_progress == now_progress) { // 进度不变
            confirm.setVisibility(View.VISIBLE);
        } else {
            // 初始化音效
            initialSoundPool();
            // 更新数据与发送广播更新widget
            db.changeWorking_taskNowProgressById(taskId, new_progress,
                    new_progress - now_progress);
            // 刷新widget
            Intent intent = new Intent(ShowWorkingTaskDetailActivity
                    .this, WidgetProvider.class);
            intent.putExtra("workingTask", db.getWorkingTaskNumber());
            intent.putExtra("workingTaskTodayWork",
                    db.getWorkingTaskTodayWorkNumber());
            sendBroadcast(intent);
        }
    }

    // 初始化音效
    private void initialSoundPool() {
        // 音效设置（绘图完毕后再处理音效防止卡顿出现）
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        soundId_start = soundPool
                .load(getApplicationContext(), R.raw.count_start, 1);
        soundId_end = soundPool.load(getApplicationContext(), R.raw.count_end, 1);
        soundId_finish = soundPool
                .load(getApplicationContext(), R.raw.task_finish, 1);
        soundId_finish_reward = soundPool
                .load(getApplicationContext(), R.raw.sound_finish_reward, 1);
    }

    // 处理完成任务后动画
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == 0) { // 动画操作
                if (finishRewardName.equals("經驗值")) {
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
                } else if (finishRewardName.equals("金幣")) {
                    int gold = Integer.parseInt(person_gold.getText().toString());
                    gold++;
                    person_gold.setText(Integer.toString(gold));
                    if (finishRewardNumber > 1) {
                        // 数字放大
                        person_gold.setTextSize(textBigSize);
                    } else {
                        // 大小复原
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
                        // 数字放大
                        numberTv.setTextSize(textBigSize);
                    } else {
                        // 大小复原
                        numberTv.setTextSize(14);
                    }
                } else {
                    finishRewardNumber = 1;
                }

                finishRewardNumber -= 1;
                post(runnable);
            } else { // 动画结束
                handler.removeCallbacks(runnable);
                confirm.setVisibility(View.VISIBLE);
            }
        }
    };

    // 发送完成后动画
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

    // 进度条动画结束后操作
    private void progressAnimationFinish() {
        // 暂停进度条音效，播放完成音效
        soundPool.stop(soundId_start);
        soundPool.play(soundId_end, 1, 1, 0, 0, 1);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 检测任务是否完成
        if (new_progress == total_progress) { // 完成
            // 展示隐藏界面
            finishRewardLayout.setVisibility(View.VISIBLE);

            // 播放任务完成音效
            soundPool.play(soundId_finish, 1, 1, 0, 0, 1);
            db.deleteWorking_task(task);
            String finish_time = Long.toString(System.currentTimeMillis());
            db.saveFinish_task(task.getDescription(), task.getTotalProgress(),
                    task.getUnit(), task.getReward(), task.getOriginalStartTime(),
                    finish_time);

            // 刷新widget
            Intent intent = new Intent(ShowWorkingTaskDetailActivity.this,
                    WidgetProvider.class);
            intent.putExtra("workingTask", db.getWorkingTaskNumber());
            intent.putExtra("workingTaskTodayWork",
                    db.getWorkingTaskTodayWorkNumber());
            sendBroadcast(intent);

            // 計算奖励
            for (int i = 0; i < rewardNameList.size(); i++) {
                String name = rewardNameList.get(i);
                if (!TestSetting.test) {
                    if (name.equals("經驗值")) {
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
                    } else if (name.equals("金幣")) {
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

            // 奖励名与奖励数目迭代器
            iterator = rewardNameList.iterator();
            iterator2 = rewardNumberList.iterator();
            // 开始任务完成动画
            handler.post(runnable);

        } else { // 任务未完成
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

        //绘制奖励界面
        List<PersonAttr> list = new ArrayList<PersonAttr>();
        String[] reward = task.getReward().split("\\|");
        for (int i = 0; i < reward.length; i++) {
            String[] singleReward = reward[i].split("_");
            if (!(singleReward[0].equals("經驗值") || singleReward[0].equals("金幣"))) {
                list.add(db.getPersonAttrByName(singleReward[0]));
            }
        }
        // 在layout完成后加入绘制奖励内容
        final List<PersonAttr> finalList2 = list;
        finishRewardAttrLayout.post(new Runnable() {
            @Override
            public void run() {
                viewMap = DrawView
                        .drawShowRewardLayout(ShowWorkingTaskDetailActivity.this,
                                finishRewardAttrLayout,
                                finishRewardAttrLayout.getMeasuredWidth(), 90, 2, 10,
                                finalList2, 1);
                // 绘制完毕改为不可见
                finishRewardLayout.setVisibility(View.GONE);
            }
        });

    }

}
