package service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import model.SuperxlcrNoteDB;

import broadcast.WidgetProvider;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class WidgetService extends Service {

	private Timer timer = null;
	private SimpleDateFormat sdf = null;

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	private SuperxlcrNoteDB db;

	/**
	 * ��ر�����ʼ��
	 */
	private void init() {
		timer = new Timer();
		sdf = new SimpleDateFormat("yyyy��MM��dd�� EE");
		sp = getSharedPreferences("DailyTask", 0);
		editor = sp.edit();
		db = SuperxlcrNoteDB.getInstance(getApplicationContext());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// ��ʼ��
		this.init();
		// ��ʱ�����͹㲥
		// �ӳ�һ���ӣ���һ���ӷ�һ��
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// ���͹㲥
				checkTime();
			}
		}, 1000, 60000);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void checkTime() {
		String oldTime = sp.getString("date", "");
		String newTime = sdf.format(new Date());

		Intent intent = new Intent(this, WidgetProvider.class);

		// �µ�һ��~
		if (!oldTime.equals(newTime)) {
			// �޸�sp
			editor.putString("date", newTime);
			editor.commit();

			// ˢ��ÿ������
			db.refreshDailyTask();
			// ˢ����ͨ����������
			db.resetWorkingTaskTodayWork();

			// ��ͨ����
			intent.putExtra("workingTask", db.getWorkingTaskNumber());
			intent.putExtra("workingTaskTodayWork",
					db.getWorkingTaskTodayWorkNumber());

			// ÿ������
			intent.putExtra("finishDailyTask", 0);
			intent.putExtra("totalDailyTask", db.getTotalDailyTaskNumber());

		}

		// ���͹㲥��֪ͨUI��ʱ��ı���
		intent.putExtra("date", newTime);
		sendBroadcast(intent);

	}

}
