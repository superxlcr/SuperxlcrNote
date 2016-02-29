package activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.superxlcrnote.app.R;

import java.util.List;

import model.SuperxlcrNoteDB;
import service.WidgetService;

/**
 * Created by Superxlcr
 * 主界面
 */
public class MainListActivity extends Activity {

	public static boolean activityRunning = false;

	private SuperxlcrNoteDB db;

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	// 用TabHost管理所有界面，把每个界面的内容封装到对应类中管理
	private WorkingTaskActivity workingTaskActivity;
	private DailyTaskActivity dailyTaskActivity;
	private FinishTaskActivity finishTaskActivity;
	private PersonInfoActivity personInfoActivity;

	// 两次点击退出的时间间隔
	private long clickTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 全屏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_list);

		// 初始化数据库
		db = SuperxlcrNoteDB.getInstance(MainListActivity.this);

		// 取得sharedPreferences
		sp = getSharedPreferences("Person_info", 0);
		editor = sp.edit();

		// 初始化working_task_list
		workingTaskActivity = new WorkingTaskActivity(this);
		workingTaskActivity.onCreate();

		// 初始化daily_task_list
		dailyTaskActivity = new DailyTaskActivity(this);
		dailyTaskActivity.onCreate();

		// 初始化finish_task_list
		finishTaskActivity = new FinishTaskActivity(this);
		finishTaskActivity.onCreate();

		// 个人信息以及进行任务和完成任务的头部
		// 初始化个人信息
		personInfoActivity = new PersonInfoActivity(this);
		personInfoActivity.onCreate();

		// 初始化TabHost
		// 获取TabHost对象
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		// 如果没有继承TabActivity时，通过该种方法加载启动tabHost
		LocalActivityManager mLocalActivityManager = new LocalActivityManager(this,
				false);
		mLocalActivityManager.dispatchCreate(savedInstanceState);
		tabHost.setup(mLocalActivityManager);
		// 自定义tabWidget
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		// 标签名
		String[] tabSpec = { "working_task", "daily_task", "finish_task",
				"person_info" };
		// 标签内容
		int[] content = { R.id.show_working_task_tab, R.id.show_daily_task_tab,
				R.id.show_finish_task_tab, R.id.show_person_info_tab };
		// 显示名称
		String[] text = { "未完成", "每日任務", "已完成", "玩家信息" };
		// 显示图片
		int[] image = { R.drawable.task_working, R.drawable.task_daily,
				R.drawable.task_finish, R.drawable.person };
		for (int i = 0; i < tabSpec.length; i++) {
			View view = layoutInflater.inflate(R.layout.tab_item_view, null);
			TextView tabText = (TextView) view.findViewById(R.id.tab_text);
			ImageView tabImage = (ImageView) view.findViewById(R.id.tab_image);
			tabText.setText(text[i]);
			tabImage.setImageResource(image[i]);
			tabHost.addTab(tabHost.newTabSpec(tabSpec[i]).setIndicator(view)
					.setContent(content[i]));
			tabHost.getTabWidget().getChildAt(i)
					.setBackgroundResource(R.drawable.tab_button);
		}

		// 判断后台时间Service是否在运行
		Intent service = new Intent(this, WidgetService.class);
		if (isServiceRunning(this, "WidgetService") == false) {
			this.startService(service);
		}

		// 修改标志位activity正在运行
		activityRunning = true;

	}

	@Override
	protected void onResume() {
		super.onResume();
		workingTaskActivity.onResume();
		finishTaskActivity.onResume();
		personInfoActivity.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityRunning = false;
	}

	@Override
	public void onBackPressed() {
		long nowTime = System.currentTimeMillis();
		if (nowTime - clickTime > 2000) {
			Toast.makeText(this,"再次点击退出",Toast.LENGTH_SHORT).show();
			clickTime = nowTime;
		} else {
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		personInfoActivity.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void refreshData() {
		dailyTaskActivity.refreshData();
		personInfoActivity.onResume();
	}

	/**
	 * 用来判断服务是否运行.
	 *
	 * @param mContext
	 * @param className
	 *            判断的服务名字
	 * @return true 在运行 false 不在运行
	 */
	private boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

}
