package activity;

import java.util.List;

import model.SuperxlcrNoteDB;
import model.WorkingTask;
import util.listener.BaseSwipeListViewListener;
import util.listener.SwipeListView;
import util.adapter.WorkingTaskSwipeAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.superxlcrnote.app.R;

public class WorkingTaskActivity {
	private Activity activity = null;

	private SwipeListView mSwipeListView;
	private WorkingTaskSwipeAdapter mAdapter;
	public static int deviceWidth;
	private List<WorkingTask> workingTaskData;

	private Button new_task;

	private SuperxlcrNoteDB db;

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public WorkingTaskActivity(Activity activity) {
		this.activity = activity;
	}

	public void onCreate() {

		// 初始化数据库
		db = SuperxlcrNoteDB.getInstance(activity);

		// 取得sharedPreferences
		sp = activity.getSharedPreferences("Person_info", 0);
		editor = sp.edit();

		// 初始化view
		mSwipeListView = (SwipeListView) activity
				.findViewById(R.id.working_task_list);
		workingTaskData = db.getWorking_tasks();
		mAdapter = new WorkingTaskSwipeAdapter(activity,
				R.layout.working_task_row, workingTaskData, mSwipeListView);
		deviceWidth = getDeviceWidth();
		mSwipeListView.setAdapter(mAdapter);
		mSwipeListView
				.setSwipeListViewListener(new TestBaseSwipeListViewListener());
		mSwipeListView.setDivider(null);
		// 设置属性
		reload();

		// 设置新建按钮
		new_task = (Button) activity.findViewById(R.id.new_task);
		new_task.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(activity, NewWorkingTaskActivity.class);
				activity.startActivity(intent);
			}
		});
	}

	public void onResume() {
		// 刷新列表数据
		workingTaskData.clear();
		workingTaskData.addAll(db.getWorking_tasks());
		mAdapter.notifyDataSetChanged();
	}

	// 取得设备长度
	private int getDeviceWidth() {
		return activity.getResources().getDisplayMetrics().widthPixels;
	}

	// 设置属性
	private void reload() {
		mSwipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
		mSwipeListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
		// mSwipeListView.setSwipeActionRight(settings.getSwipeActionRight());
		mSwipeListView.setOffsetLeft(deviceWidth * 6 / 10);
		// mSwipeListView.setOffsetRight(convertDpToPixel(settings.getSwipeOffsetRight()));
		mSwipeListView.setAnimationTime(0);
		mSwipeListView.setSwipeOpenOnLongPress(false);
	}

	// 列表点击监听器
	class TestBaseSwipeListViewListener extends BaseSwipeListViewListener {

		@Override
		public void onClickFrontView(int position) {
			super.onClickFrontView(position);
			Intent intent = new Intent(activity,
					ShowWorkingTaskDetailActivity.class);
			intent.putExtra("taskId", workingTaskData.get(position).getId());
			intent.putExtra("new_progress", workingTaskData.get(position)
					.getNowProgress());
			intent.putExtra("note", workingTaskData.get(position).getNote());
			activity.startActivity(intent);
			mSwipeListView.closeOpenedItems();
		}

		@Override
		public void onDismiss(int[] reverseSortedPositions) {
			for (int position : reverseSortedPositions) {
				workingTaskData.remove(position);
			}
			mAdapter.notifyDataSetChanged();
		}
	}
}
