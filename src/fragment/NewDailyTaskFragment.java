package fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
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

import activity.MainListActivity;
import broadcast.WidgetProvider;
import model.PersonAttr;
import model.SuperxlcrNoteDB;

/**
 * Created by Superxlcr
 * �ճ����������½�������Ƭ
 */
public class NewDailyTaskFragment extends Fragment {

	private View view;

	private EditText task_description;
	private LinearLayout task_reward;
	private TextView task_reward_point;
	private Button back;
	private Button confirm;
	private String description, reward;

	private int rewardRowNumber = 1;
	private int rewardPadding = 20;
	private int totalReward;
	private int rewardPoint;
	private boolean[] rewardChecked;

	private List<PersonAttr> list = null;

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private SuperxlcrNoteDB db = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.new_daily_task, container, false);

		sp = getActivity().getSharedPreferences("Person_info", 0);
		editor = sp.edit();

		db = SuperxlcrNoteDB.getInstance(getActivity());

		task_description = (EditText) view.findViewById(R.id.task_description);
		task_reward = (LinearLayout) view.findViewById(R.id.task_reward);
		task_reward_point = (TextView) view
				.findViewById(R.id.task_reward_point);

		rewardPoint = sp.getInt("level", 0) / 5 + 3;
		task_reward_point.setText(Integer.toString(rewardPoint));

		buildRewardLayout();

		// ��ť����
		back = (Button) view.findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		confirm = (Button) view.findViewById(R.id.confirm);
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				description = task_description.getText().toString();
				if (description.isEmpty()) {
					toastShow("�l��ʧ�����΄����Q��գ�");
				} else {

					// ���ɽ���
					Random rand = new Random();
					reward = "";
					reward = "���ֵ_" + Integer.toString(1);
					reward += "|"; // �ָ���
					reward += "����_" + Integer.toString(1);
					for (int i = 0; i < totalReward; i++) {
						if (rewardChecked[i] == true && !list.isEmpty()) {
							reward += "|"; // �ָ���
							reward += list.get(i).getName() + "_"
									+ Integer.toString(1);
						}
					}
					db.saveDailytask(description, reward);
					toastShow("�l�ѳɹ������ѽ���ԓ�΄գ�");
					finish();
					((MainListActivity) getActivity()).refreshData();

					// ˢ��widget
					refreshWidget();
				}
			}
		});

		return view;
	}

	/**
	 * �رձ�����
	 */
	private void finish() {
		FragmentManager fragmentManager = getFragmentManager();
		android.app.FragmentTransaction transaction = fragmentManager
				.beginTransaction();
		transaction.remove(NewDailyTaskFragment.this);
		transaction.commit();
	}

	private void buildRewardLayout() {
		// ���ɽ�������
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				400, LinearLayout.LayoutParams.WRAP_CONTENT);

		list = db.getPersonAttr();
		LinearLayout ll;

		totalReward = list.size();
		rewardChecked = new boolean[totalReward];
		for (int i = 0; i < totalReward; i++) {
			rewardChecked[i] = false;
		}

		// ���ɲ���
		ll = new LinearLayout(getActivity());
		ll.setLayoutParams(layoutParams);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.setPadding(rewardPadding, 0, rewardPadding, 0);

		LayoutParams param = new LinearLayout.LayoutParams(
				400 / rewardRowNumber, LayoutParams.WRAP_CONTENT);

		for (int i = 0; i < totalReward; i++) {
			final int i_final = i;
			// ���ɶ���
			CheckBox cb = new CheckBox(getActivity());
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
						task_reward_point.setText(Integer.toString(rewardPoint));
						rewardChecked[i_final] = false;
					}
				}
			});
			if (i % rewardRowNumber == 0) {
				if (i != 0) {
					task_reward.addView(ll);
					ll = new LinearLayout(getActivity());
					ll.setLayoutParams(layoutParams);
					ll.setOrientation(LinearLayout.HORIZONTAL);
					ll.setPadding(rewardPadding, 0, rewardPadding, 0);
				}
			}
			ll.addView(cb);
		}
		task_reward.addView(ll);
	}

	/**
	 * ����widget��Ϣ
	 */
	private void refreshWidget() {
		Intent intent = new Intent(getActivity(), WidgetProvider.class);
		intent.putExtra("finishDailyTask", db.getFinishDailyTaskNumber());
		intent.putExtra("totalDailyTask", db.getTotalDailyTaskNumber());
		getActivity().sendBroadcast(intent);
	}

	private void toastShow(String content) {
		Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
	}
}
