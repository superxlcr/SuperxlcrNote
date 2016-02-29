package util.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.superxlcrnote.app.R;

import java.util.List;

import model.DailyTask;

/**
 * Created by Superxlcr
 * 日常任务适配器
 */
public class DailyTaskAdapter extends ArrayAdapter<DailyTask> {

	private LayoutInflater mInflater;

	public DailyTaskAdapter(Context context, int textViewResourceId,
			List<DailyTask> objects) {
		super(context, textViewResourceId, objects);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.daily_task_row, parent,
					false);
			holder = new ViewHolder();
			holder.task_id = (TextView) convertView.findViewById(R.id.task_id);
			holder.task_description = (TextView) convertView
					.findViewById(R.id.task_description);
			holder.dailyFinish = (ImageView) convertView.findViewById(R.id.daily_finish);
			// TODO
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		DailyTask item = getItem(position);
		holder.task_id.setText(Integer.toString(item.getId()));
		holder.task_description.setText(item.getDescription());

		if (item.isFinish()) {
			holder.dailyFinish.setVisibility(View.VISIBLE);
		} else {
			holder.dailyFinish.setVisibility(View.GONE);
		}
		
		return convertView;
	}

	class ViewHolder {
		TextView task_id, task_description;
		ImageView dailyFinish;
	}
}
