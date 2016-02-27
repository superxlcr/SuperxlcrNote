package activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.superxlcrnote.app.R;

import java.util.ArrayList;
import java.util.List;

import model.FinishTask;
import model.PersonAttr;
import model.SuperxlcrNoteDB;
import util.DrawView;

public class ShowFinishTaskDetailActivity extends Activity {

    private TextView task_description;
    private TextView task_start_time;
    private TextView task_finish_time;
    private TextView task_total_progress;
    private LinearLayout task_reward;
    private Button confirm;
    private SuperxlcrNoteDB db = null;

    private String unit;

    private int taskId;
    private FinishTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_finish_task_detail);
        db = SuperxlcrNoteDB.getInstance(ShowFinishTaskDetailActivity.this);

        // 初始化对象
        task_description = (TextView) findViewById(R.id.task_description);
        task_start_time = (TextView) findViewById(R.id.task_start_time);
        task_finish_time = (TextView) findViewById(R.id.task_finish_time);
        task_total_progress = (TextView) findViewById(R.id.task_total_progress);
        task_reward = (LinearLayout) findViewById(R.id.task_reward);
        confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /**
         * 初始化數據
         */

        taskId = getIntent().getIntExtra("taskId", -1);
        if (taskId != -1) {
            task = db.getFinishTaskById(taskId);
            task_description.setText(task.getDescription());
            task_start_time.setText(task.getStartTime());
            task_finish_time.setText(task.getFinishTime());
            unit = task.getUnit();
            task_total_progress
                    .setText(Integer.toString(task.getTotalProgress()) + unit);

        }

        // 生成列表并生成奖励界面
        List<PersonAttr> list = new ArrayList<PersonAttr>();
        String[] reward = task.getReward().split("\\|");
        for (int i = 0; i < reward.length; i++) {
            String[] singleReward = reward[i].split("_");
            PersonAttr personAttr = null;
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
        // layout绘制完成后加入内容
        final List<PersonAttr> finalList = list;
        task_reward.post(new Runnable() {
            @Override
            public void run() {
                DrawView.drawShowRewardLayout(ShowFinishTaskDetailActivity.this,
                        task_reward, task_reward.getMeasuredWidth(), 90, 2,
                        10, finalList, 0);
            }
        });

    }

}
