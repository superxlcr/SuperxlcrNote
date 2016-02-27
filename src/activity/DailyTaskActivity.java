package activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.superxlcrnote.app.R;

import java.util.List;

import broadcast.WidgetProvider;
import fragment.NewDailyTaskFragment;
import fragment.ShowDailyTaskDetailFragment;
import model.DailyTask;
import model.SuperxlcrNoteDB;
import model.TestSetting;
import util.adapter.DailyTaskAdapter;

public class DailyTaskActivity {
	private Activity activity;

	private Button newDailyTask;

	private List<DailyTask> dailyTaskData;
	private ListView listView;
	private DailyTaskAdapter dailyTaskAdapter;

	// 列表
	private LinearLayout lastLinearLayoutView = null;
	private TextView lastTextView = null;

	private SuperxlcrNoteDB db;

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	// 测试按钮
	private Button setting;

	public DailyTaskActivity(Activity activity) {
		this.activity = activity;
	}

	public void onCreate() {

		// 初始化数据库
		db = SuperxlcrNoteDB.getInstance(activity);

		// 取得sharedPreferences
		sp = activity.getSharedPreferences("Person_info", 0);
		editor = sp.edit();
		listView = (ListView) activity.findViewById(R.id.daily_task_list);
		dailyTaskData = db.getDailyTasks();
		dailyTaskAdapter = new DailyTaskAdapter(activity,
				R.layout.finish_task_row, dailyTaskData);
		listView.setAdapter(dailyTaskAdapter);
		listView.setDivider(null);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ShowDailyTaskDetailFragment fragment = new ShowDailyTaskDetailFragment();
				FragmentManager fragmentManager = activity.getFragmentManager();
				android.app.FragmentTransaction transaction = fragmentManager
						.beginTransaction();
				transaction.replace(R.id.daily_fagment, fragment);
				transaction.commit();
				fragment.setRefreshData(dailyTaskData.get(position).getId());
				if (lastLinearLayoutView != null) {
					lastLinearLayoutView
							.setBackgroundResource(R.drawable.corners_bg);
				}
				if (lastTextView != null) {
					lastTextView.setTextColor(activity.getResources().getColor(
							R.color.color_text));
				}
				lastLinearLayoutView = (LinearLayout) view
						.findViewById(R.id.daily_task_bg);
				lastTextView = (TextView) view
						.findViewById(R.id.task_description);
				if (lastLinearLayoutView != null) {
					lastLinearLayoutView
							.setBackgroundResource(R.drawable.corners_bg_daily_task);
				}
				if (lastTextView != null) {
					lastTextView.setTextColor(activity.getResources().getColor(
							R.color.color_text_selected));
				}

			}
		});

		newDailyTask = (Button) activity.findViewById(R.id.new_daily_task);
		newDailyTask.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				NewDailyTaskFragment fragment = new NewDailyTaskFragment();
				FragmentManager fragmentManager = activity.getFragmentManager();
				android.app.FragmentTransaction transaction = fragmentManager
						.beginTransaction();
				transaction.replace(R.id.daily_fagment, fragment);
				transaction.commit();
				if (lastLinearLayoutView != null) {
					lastLinearLayoutView
							.setBackgroundResource(R.drawable.corners_bg);
					lastLinearLayoutView = null;
				}
				if (lastTextView != null) {
					lastTextView.setTextColor(activity.getResources().getColor(
							R.color.color_text));
					lastTextView = null;
				}
			}
		});

		setting = (Button) activity.findViewById(R.id.daily_task_setting);
		if (TestSetting.test == true) {
			setting.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					db.refreshDailyTask();
					refreshData();
					refreshWidget();
				}
			});
		} else {
			setting.setVisibility(View.GONE);
		}

	}

	public void refreshData() {
		dailyTaskData.clear();
		dailyTaskData.addAll(db.getDailyTasks());
		dailyTaskAdapter.notifyDataSetChanged();
	}

	private void refreshWidget() {
		Intent intent = new Intent(activity, WidgetProvider.class);
		intent.putExtra("finishDailyTask", db.getFinishDailyTaskNumber());
		intent.putExtra("totalDailyTask", db.getTotalDailyTaskNumber());
		activity.sendBroadcast(intent);
	}
}
