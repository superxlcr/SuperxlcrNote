package activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.superxlcrnote.app.R;

import java.util.List;
import java.util.Random;

import broadcast.WidgetProvider;
import model.PersonAttr;
import model.SuperxlcrNoteDB;

/**
 * Created by Superxlcr
 * 新建未完成任务界面
 */
public class NewWorkingTaskActivity extends Activity {

	private EditText task_description;
	private EditText task_note;
	private EditText task_total_progress;
	private EditText task_unit;
	private LinearLayout task_reward;
	private TextView task_reward_point;
	private Button back;
	private Button confirm;
	private String description, note, total_progress, unit, reward;
	private SuperxlcrNoteDB db = null;

	private int rewardRowNumber = 2;
	private int rewardPadding = 20;
	private int totalReward;
	private int rewardPoint;
	private boolean[] rewardChecked;

	private List<PersonAttr> list = null;

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_working_task);

		// 取得sharedPreferences
		sp = getSharedPreferences("Person_info", 0);
		editor = sp.edit();

		db = SuperxlcrNoteDB.getInstance(NewWorkingTaskActivity.this);
		task_description = (EditText) findViewById(R.id.task_description);
		task_note = (EditText) findViewById(R.id.task_note);
		task_total_progress = (EditText) findViewById(R.id.task_total_progress);
		task_unit = (EditText) findViewById(R.id.task_unit);
		task_reward = (LinearLayout) findViewById(R.id.task_reward);
		task_reward_point = (TextView) findViewById(R.id.task_reward_point);
		back = (Button) findViewById(R.id.back);
		confirm = (Button) findViewById(R.id.confirm);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				description = task_description.getText().toString();
				note = task_note.getText().toString();
				total_progress = task_total_progress.getText().toString();
				unit = task_unit.getText().toString();
				if (description.isEmpty()) {
					toastShow("發佈失敗：任務名稱為空！");
				} else if (total_progress.isEmpty()) {
					toastShow("發佈失敗：任務目標為空！");
				} else if (Integer.parseInt(total_progress) == 0) {
					toastShow("發佈失敗：任務目標為零！");
				} else {
					if (unit.isEmpty()) {
						unit = "%";
					}

					// 生成奖励
					Random rand = new Random();
					reward = "";
					reward = "經驗值_"
							+ Integer.toString(rand.nextInt(4) + 5
									+ rewardPoint); // 5 ～10
					reward += "|"; // 分隔符
					reward += "金幣_"
							+ Integer.toString(rand.nextInt(4) + 5
									+ rewardPoint); // 5 ～10
					for (int i = 0; i < totalReward; i++) {
						if (rewardChecked[i] == true && !list.isEmpty()) {
							reward += "|"; // 分隔符
							reward += list.get(i).getName()
									+ "_"
									+ Integer.toString(rand.nextInt(4) + 1
											+ rewardPoint); // 1～5
						}
					}
					String start_time = Long.toString(System
							.currentTimeMillis());
					db.saveWorking_task(description, 0,
							Integer.parseInt(total_progress), unit, reward,
							start_time, note);
					
					// 刷新widget
					Intent intent = new Intent(NewWorkingTaskActivity.this, WidgetProvider.class);
					intent.putExtra("workingTask", db.getWorkingTaskNumber());
					intent.putExtra("workingTaskTodayWork", db.getWorkingTaskTodayWorkNumber());
					sendBroadcast(intent);
					
					toastShow("發佈成功：您已接受該任務！");
					finish();
				}
			}
		});

		rewardPoint = sp.getInt("level", 0) / 5 + 3;
		task_reward_point.setText(Integer.toString(rewardPoint));

		// 注册绘图监听器，在界面渲染完成后再添加布局
		ViewTreeObserver vto = task_reward.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				task_reward.getViewTreeObserver().removeGlobalOnLayoutListener(
						this);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						task_reward.getWidth(),
						LinearLayout.LayoutParams.WRAP_CONTENT);

				// 生成奖励界面
				list = db.getPersonAttr();
				LinearLayout ll;

				totalReward = list.size();
				rewardChecked = new boolean[totalReward];
				for (int i = 0; i < totalReward; i++) {
					rewardChecked[i] = false;
				}

				// 生成布局
				ll = new LinearLayout(NewWorkingTaskActivity.this);
				ll.setLayoutParams(layoutParams);
				ll.setOrientation(LinearLayout.HORIZONTAL);
				ll.setPadding(rewardPadding, 0, rewardPadding, 0);

				LayoutParams param = new LinearLayout.LayoutParams(task_reward
						.getWidth() / rewardRowNumber,
						LayoutParams.WRAP_CONTENT);

				for (int i = 0; i < totalReward; i++) {
					final int i_final = i;
					// 生成对象
					CheckBox cb = new CheckBox(NewWorkingTaskActivity.this);
					cb.setText(list.get(i).getName());
					cb.setTextColor(Color.parseColor(list.get(i).getColor()));
					cb.setLayoutParams(param);
					cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (isChecked == true) {
								if (rewardPoint > 0) {
									rewardPoint--;
									task_reward_point.setText(Integer
											.toString(rewardPoint));
									rewardChecked[i_final] = true;
								} else {
									buttonView.setChecked(false);
								}
							} else {
								rewardPoint++;
								task_reward_point.setText(Integer
										.toString(rewardPoint));
								rewardChecked[i_final] = false;
							}
						}
					});
					if (i % rewardRowNumber == 0) {
						if (i != 0) {
							task_reward.addView(ll);
							ll = new LinearLayout(NewWorkingTaskActivity.this);
							ll.setLayoutParams(layoutParams);
							ll.setOrientation(LinearLayout.HORIZONTAL);
							ll.setPadding(rewardPadding, 0, rewardPadding, 0);
						}
					}
					ll.addView(cb);
				}
				task_reward.addView(ll);
			}
		});
	}

	private void toastShow(String content) {
		Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT)
				.show();
	}

}
