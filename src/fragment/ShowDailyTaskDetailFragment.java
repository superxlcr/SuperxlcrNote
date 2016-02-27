package fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.superxlcrnote.app.R;

import java.util.ArrayList;
import java.util.List;

import activity.MainListActivity;
import broadcast.WidgetProvider;
import model.DailyTask;
import model.PersonAttr;
import model.SuperxlcrNoteDB;
import model.TestSetting;
import util.DrawView;

public class ShowDailyTaskDetailFragment extends Fragment {

    private View view;

    private TextView taskDescription;
    private TextView taskFinishTimes;
    private TextView taskContinuous;
    private ImageView taskFinish;

    private LinearLayout task_reward;

    private Button delete;
    private Button confirm;

    private SuperxlcrNoteDB db;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private int rewardPadding = 10;
    private int rewardWidth = 400;

    private int taskId;

    private SoundPool soundPool;
    private int soundId_finish;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.show_daily_task_detail, container, false);

        taskDescription = (TextView) view.findViewById(R.id.task_description);
        taskFinishTimes = (TextView) view.findViewById(R.id.task_finish_times);
        taskContinuous = (TextView) view.findViewById(R.id.task_continuous);
        taskFinish = (ImageView) view.findViewById(R.id.task_finish);

        task_reward = (LinearLayout) view.findViewById(R.id.task_reward);

        delete = (Button) view.findViewById(R.id.delete);
        confirm = (Button) view.findViewById(R.id.confirm);

        db = SuperxlcrNoteDB.getInstance(getActivity());

        // ȡ��sharedPreferences
        sp = getActivity().getSharedPreferences("Person_info", 0);
        editor = sp.edit();

        // ��Ч���ã���ͼ��Ϻ��ٴ�����Ч��ֹ���ٳ��֣�
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundId_finish = soundPool.load(getActivity(), R.raw.task_finish, 1);

        refreshView();

        return view;
    }

    public void setRefreshData(int id) {
        this.taskId = id;
    }

    private void refreshView() {
        final DailyTask task = db.getDailyTaskById(taskId);
        if (task != null) {
            taskDescription.setText(task.getDescription());
            taskFinishTimes.setText(Integer.toString(task.getFinishTimes()));
            taskContinuous.setText(Integer.toString(task.getContinuous()));
            if (task.isFinish() == false) {
                taskFinish.setImageResource(R.drawable.unfinish);
                confirm.setVisibility(View.VISIBLE);
            } else {
                taskFinish.setImageResource(R.drawable.finish);
                confirm.setVisibility(View.GONE);
            }

            delete.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getActivity()).setTitle("�ŗ��΄�")
                            .setMessage("Ո�����_��Ҫ��ֹ�΄Ն᣿").setPositiveButton("��",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    // �����ݿ�ɾ��task
                                    db.deleteDailyTask(task);
                                    finish();
                                    ((MainListActivity) getActivity()).refreshData();
                                    refreshWidget();
                                }
                            }).setNegativeButton("��", null).show();
                }
            });
            confirm.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    // ������Ч
                    soundPool.play(soundId_finish, 1, 1, 0, 0, 1);

                    db.dailyTaskConfirm(taskId);

                    // ˢ��fragment

                    refreshView();

                    // ˢ��widget

                    refreshWidget();

                    // Ӌ�㽱��

                    String[] reward = null;
                    reward = task.getReward().split("\\|");
                    for (int i = 0; i < reward.length; i++) {
                        String[] singleReward = reward[i].split("_");
                        String name = singleReward[0];
                        if (TestSetting.test == false) {
                            if (name.equals("���ֵ")) {
                                int level = sp.getInt("level", 0);
                                int now_exp = sp.getInt("now_exp", 0);
                                int total_exp = level * 10 + 50;
                                now_exp += Integer.parseInt(singleReward[1]);
                                if (now_exp >= total_exp) {
                                    level += 1;
                                    editor.putInt("level", level);
                                    now_exp -= total_exp;
                                }
                                editor.putInt("now_exp", now_exp);
                                editor.commit();
                            } else if (name.equals("����")) {
                                int gold = sp.getInt("gold", 0);
                                gold += Integer.parseInt(singleReward[1]);
                                editor.putInt("gold", gold);
                                editor.commit();
                            } else {
                                db.updatePersonAttrByName(name,
                                        Integer.parseInt(singleReward[1]));
                            }
                        }
                    }

                    // ˢ����������Ϣ

                    ((MainListActivity) getActivity()).refreshData();

                }
            });

            // ���ɽ�������

            List<PersonAttr> list = new ArrayList<PersonAttr>();
            String[] reward = task.getReward().split("\\|");
            for (int i = 0; i < reward.length; i++) {
                String[] singleReward = reward[i].split("_");
                PersonAttr personAttr;
                if (singleReward[0].equals("���ֵ")) { // ���ֵ
                    personAttr = new PersonAttr("���ֵ",
                            task.getFinishTimes(), "#fcfe66",
                            R.drawable.exp, true, "");
                } else if (singleReward[0].equals("����")) { // ����
                    personAttr = new PersonAttr("����",
                            task.getFinishTimes(), "#dde000",
                            R.drawable.gold, true, "");
                } else {
                    personAttr = db.getPersonAttrByName(singleReward[0]);
                    personAttr.setNumber(task.getFinishTimes());
                }
                list.add(personAttr);
            }

            // layout������ɺ��������
            final List<PersonAttr> finalList = list;
            task_reward.post(new Runnable() {
                @Override
                public void run() {
                    DrawView.drawShowRewardLayout(getActivity(),
                            task_reward, task_reward.getMeasuredWidth(), 90, 1,
                            rewardPadding, finalList, 0);
                }
            });
        }
    }

    private void finish() {
        FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction transaction = fragmentManager
                .beginTransaction();
        transaction.remove(ShowDailyTaskDetailFragment.this);
        transaction.commit();
        soundPool.unload(soundId_finish);
    }

    private void refreshWidget() {
        Intent intent = new Intent(getActivity(), WidgetProvider.class);
        intent.putExtra("finishDailyTask", db.getFinishDailyTaskNumber());
        intent.putExtra("totalDailyTask", db.getTotalDailyTaskNumber());
        getActivity().sendBroadcast(intent);
    }

}
