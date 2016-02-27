package util.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.superxlcrnote.app.R;

import java.util.List;

import activity.ShowWorkingTaskDetailActivity;
import broadcast.WidgetProvider;
import model.PersonAttr;
import model.SuperxlcrNoteDB;
import model.WorkingTask;
import util.listener.SwipeListView;

public class WorkingTaskSwipeAdapter extends ArrayAdapter<WorkingTask> {
	private LayoutInflater mInflater;
	private SwipeListView mSwipeListView;
	private SuperxlcrNoteDB db;

	private int TotalShortNameNumber = 4;

	private boolean showExp = false;
	private boolean showGold = false;

	public WorkingTaskSwipeAdapter(Context context, int textViewResourceId,
			List<WorkingTask> objects, SwipeListView mSwipeListView) {
		super(context, textViewResourceId, objects);
		this.mSwipeListView = mSwipeListView;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		db = SuperxlcrNoteDB.getInstance(getContext());
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.working_task_row, parent,
					false);
			holder = new ViewHolder();
			holder.task_description = (TextView) convertView
					.findViewById(R.id.task_description);
			holder.task_start_time = (TextView) convertView
					.findViewById(R.id.task_start_time);
			holder.task_now_progress = (TextView) convertView
					.findViewById(R.id.task_now_progress);
			holder.task_total_progress = (TextView) convertView
					.findViewById(R.id.task_total_progress);
			holder.task_progressBar = (ProgressBar) convertView
					.findViewById(R.id.task_progressBar);
			holder.task_reward_layout = (LinearLayout) convertView
					.findViewById(R.id.task_reward_layout);
			holder.mBackWork = (ImageView) convertView
					.findViewById(R.id.task_work_btn);
			holder.mBackEdit = (ImageView) convertView
					.findViewById(R.id.task_edit_btn);
			holder.mBackDelete = (ImageView) convertView
					.findViewById(R.id.givp_up_task_btn);
			holder.todayWork = (ImageView) convertView
					.findViewById(R.id.task_today_work);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.mBackWork.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);

				// 任務進度
				final EditText etNumber = new EditText(getContext());
				etNumber.setLayoutParams(lp);
				etNumber.setKeyListener(new DigitsKeyListener(false, true));
				etNumber.setHint(Integer.toString(getItem(position)
						.getNowProgress()));

				new AlertDialog.Builder(getContext())
						.setTitle("請輸入目前任務進度")
						.setIcon(R.drawable.task_work)
						.setView(etNumber)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (etNumber.getText().toString()
												.isEmpty()) {
											Toast.makeText(getContext(),
													"任務進度為空，修改失敗！",
													Toast.LENGTH_SHORT).show();
										} else {
											int now_progress = Integer
													.parseInt(etNumber
															.getText()
															.toString());
											Intent intent = new Intent(
													getContext(),
													ShowWorkingTaskDetailActivity.class);
											intent.putExtra("taskId",
													getItem(position).getId());
											intent.putExtra("new_progress",
													now_progress);
											getContext().startActivity(intent);
										}
									}
								}).setNegativeButton("取消", null).show();
				mSwipeListView.closeOpenedItems();
			}
		});

		holder.mBackEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 任務備註
				final EditText etNote = new EditText(getContext());
				etNote.setText(getItem(position).getNote());
				etNote.setSelection(etNote.getText().length());

				new AlertDialog.Builder(getContext())
						.setTitle("請輸入新的任務備註")
						.setIcon(R.drawable.task_edit)
						.setView(etNote)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										String note = etNote.getText()
												.toString();
										Intent intent = new Intent(
												getContext(),
												ShowWorkingTaskDetailActivity.class);
										intent.putExtra("taskId",
												getItem(position).getId());
										intent.putExtra("note", note);
										getContext().startActivity(intent);

									}
								}).setNegativeButton("取消", null).show();
				mSwipeListView.closeOpenedItems();
			}
		});

		/**
		 * 删除task
		 */

		holder.mBackDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getContext())
						.setTitle("放棄任務")
						.setMessage("請問您確定要放棄任務嗎？")
						.setPositiveButton("是",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// 从数据库删除task
										WorkingTask task = getItem(position);
										db.deleteWorking_task(task);
										mSwipeListView.closeAnimate(position);
										mSwipeListView.dismiss(position);
										// 刷新widget
										Intent intent = new Intent(
												getContext(),
												WidgetProvider.class);
										intent.putExtra("workingTask", db.getWorkingTaskNumber());
										intent.putExtra("workingTaskTodayWork", db.getWorkingTaskTodayWorkNumber());
										getContext().sendBroadcast(intent);
									}
								}).setNegativeButton("否", null).show();
				mSwipeListView.closeOpenedItems();
			}
		});
		WorkingTask item = getItem(position);
		
		if (item.isTodayWork()) {
			holder.todayWork.setVisibility(View.VISIBLE);
		} else {
			holder.todayWork.setVisibility(View.GONE);
		}
		
		holder.task_description.setText(item.getDescription());
		holder.task_start_time.setText(item.getStartTime());
		String unit = item.getUnit();
		holder.task_now_progress
				.setText(Integer.toString(item.getNowProgress()) + unit);
		holder.task_total_progress.setText(Integer.toString(item
				.getTotalProgress()) + unit);
		holder.task_progressBar.setMax(item.getTotalProgress());
		holder.task_progressBar.setProgress(item.getNowProgress());
		holder.task_reward_layout.removeAllViews();
		String[] reward = null;
		reward = item.getReward().split("\\|");
		int shortNameNumber = 0;
		for (int i = 0; i < reward.length; i++) {
			String[] singleReward = reward[i].split("_");
			PersonAttr personAttr = null;
			TextView task_reward = new TextView(getContext());
			task_reward.setPadding(0, 0, 5, 0);
			task_reward.setText("");
			if (singleReward[0].equals("經驗值") && showExp == true) { // 經驗值
				task_reward.setText("Exp");
				task_reward.setTextColor(Color.parseColor("#fcfe66"));
			} else if (singleReward[0].equals("金幣") && showGold == true) { // 金幣
				task_reward.setText("金");
				task_reward.setTextColor(Color.parseColor("#dde000"));
			} else { // 其他
				personAttr = db.getPersonAttrByName(singleReward[0]);
				if (personAttr != null) {
					task_reward.setText(personAttr.getShortName());
					task_reward.setTextColor(Color.parseColor(personAttr
							.getColor()));
				}
			}
			if (!task_reward.getText().toString().equals("")
					&& shortNameNumber < TotalShortNameNumber) {
				if (shortNameNumber == 0) {
					TextView tv = new TextView(getContext());
					tv.setText("任務獎勵:");
					tv.setTextColor(Color.parseColor("#ad9281"));
					tv.setPadding(0, 0, 10, 0);
					holder.task_reward_layout.addView(tv);
				}
				shortNameNumber++;
				holder.task_reward_layout.addView(task_reward);
			}
		}
		return convertView;
	}

	class ViewHolder {
		TextView task_description, task_start_time, task_now_progress,
				task_total_progress;
		ImageView mBackWork, mBackEdit, mBackDelete, todayWork;
		ProgressBar task_progressBar;
		LinearLayout task_reward_layout;
	}
}
