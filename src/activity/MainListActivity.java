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
 * ������
 */
public class MainListActivity extends Activity {

	public static boolean activityRunning = false;

	private SuperxlcrNoteDB db;

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	// ��TabHost�������н��棬��ÿ����������ݷ�װ����Ӧ���й���
	private WorkingTaskActivity workingTaskActivity;
	private DailyTaskActivity dailyTaskActivity;
	private FinishTaskActivity finishTaskActivity;
	private PersonInfoActivity personInfoActivity;

	// ���ε���˳���ʱ����
	private long clickTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ȫ��
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_list);

		// ��ʼ�����ݿ�
		db = SuperxlcrNoteDB.getInstance(MainListActivity.this);

		// ȡ��sharedPreferences
		sp = getSharedPreferences("Person_info", 0);
		editor = sp.edit();

		// ��ʼ��working_task_list
		workingTaskActivity = new WorkingTaskActivity(this);
		workingTaskActivity.onCreate();

		// ��ʼ��daily_task_list
		dailyTaskActivity = new DailyTaskActivity(this);
		dailyTaskActivity.onCreate();

		// ��ʼ��finish_task_list
		finishTaskActivity = new FinishTaskActivity(this);
		finishTaskActivity.onCreate();

		// ������Ϣ�Լ������������������ͷ��
		// ��ʼ��������Ϣ
		personInfoActivity = new PersonInfoActivity(this);
		personInfoActivity.onCreate();

		// ��ʼ��TabHost
		// ��ȡTabHost����
		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		// ���û�м̳�TabActivityʱ��ͨ�����ַ�����������tabHost
		LocalActivityManager mLocalActivityManager = new LocalActivityManager(this,
				false);
		mLocalActivityManager.dispatchCreate(savedInstanceState);
		tabHost.setup(mLocalActivityManager);
		// �Զ���tabWidget
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		// ��ǩ��
		String[] tabSpec = { "working_task", "daily_task", "finish_task",
				"person_info" };
		// ��ǩ����
		int[] content = { R.id.show_working_task_tab, R.id.show_daily_task_tab,
				R.id.show_finish_task_tab, R.id.show_person_info_tab };
		// ��ʾ����
		String[] text = { "δ���", "ÿ���΄�", "�����", "�����Ϣ" };
		// ��ʾͼƬ
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

		// �жϺ�̨ʱ��Service�Ƿ�������
		Intent service = new Intent(this, WidgetService.class);
		if (isServiceRunning(this, "WidgetService") == false) {
			this.startService(service);
		}

		// �޸ı�־λactivity��������
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
			Toast.makeText(this,"�ٴε���˳�",Toast.LENGTH_SHORT).show();
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
	 * �����жϷ����Ƿ�����.
	 *
	 * @param mContext
	 * @param className
	 *            �жϵķ�������
	 * @return true ������ false ��������
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
