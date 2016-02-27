//package util.adapter;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.superxlcrnote.app.R;
//
//import java.util.List;
//
//import model.FinishTask;
//import model.PersonAttr;
//import model.SuperxlcrNoteDB;
//
//public class FinishTaskAdapter extends ArrayAdapter<FinishTask> {
//    private LayoutInflater mInflater;
//
//    private int TotalShortNameNumber = 4;
//
//    private SuperxlcrNoteDB db;
//
//    private boolean showExp = false;
//    private boolean showGold = false;
//
//    public FinishTaskAdapter(Context context, int textViewResourceId,
//            List<FinishTask> objects) {
//        super(context, textViewResourceId, objects);
//        mInflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        db = SuperxlcrNoteDB.getInstance(getContext());
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//        if (convertView == null) {
//            convertView = mInflater.inflate(R.layout.finish_task_row, parent, false);
//            holder = new ViewHolder();
//            holder.task_id = (TextView) convertView.findViewById(R.id.task_id);
//            holder.task_description = (TextView) convertView
//                    .findViewById(R.id.task_description);
//            holder.task_start_time = (TextView) convertView
//                    .findViewById(R.id.task_start_time);
//            holder.task_finish_time = (TextView) convertView
//                    .findViewById(R.id.task_finish_time);
//            holder.task_total_progress = (TextView) convertView
//                    .findViewById(R.id.task_total_progress);
//            holder.task_reward_layout = (LinearLayout) convertView
//                    .findViewById(R.id.task_reward_layout);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        FinishTask item = getItem(position);
//        holder.task_id.setText(Integer.toString(item.getId()));
//        holder.task_description.setText(item.getDescription());
//        holder.task_start_time.setText(item.getStartTime());
//        holder.task_finish_time.setText(item.getFinishTime());
//        String unit = item.getUnit();
//        holder.task_total_progress
//                .setText(Integer.toString(item.getTotalProgress()) + unit);
//        holder.task_reward_layout.removeAllViews();
//        String[] reward = null;
//        reward = item.getReward().split("\\|");
//        int shortNameNumber = 0;
//        for (int i = 0; i < reward.length; i++) {
//            String[] singleReward = reward[i].split("_");
//            PersonAttr personAttr = null;
//            TextView task_reward = new TextView(getContext());
//            task_reward.setPadding(0, 0, 5, 0);
//            task_reward.setText("");
//            if (singleReward[0].equals("經驗值") && showExp == true) { // 經驗值
//                task_reward.setText("Exp");
//                task_reward.setTextColor(Color.parseColor("#fcfe66"));
//            } else if (singleReward[0].equals("金幣") && showGold == true) { // 金幣
//                task_reward.setText("金");
//                task_reward.setTextColor(Color.parseColor("#dde000"));
//            } else { // 其他
//                personAttr = db.getPersonAttrByName(singleReward[0]);
//                if (personAttr != null) {
//                    task_reward.setText(personAttr.getShortName());
//                    task_reward
//                            .setTextColor(Color.parseColor(personAttr.getColor()));
//                }
//            }
//            if (!task_reward.getText().toString().equals("") &&
//                    shortNameNumber < TotalShortNameNumber) {
//                if (shortNameNumber == 0) {
//                    TextView tv = new TextView(getContext());
//                    tv.setText("任務獎勵:");
//                    tv.setTextColor(Color.parseColor("#ad9281"));
//                    tv.setPadding(0, 0, 10, 0);
//                    holder.task_reward_layout.addView(tv);
//                }
//                shortNameNumber++;
//                holder.task_reward_layout.addView(task_reward);
//            }
//        }
//        return convertView;
//    }
//
//    class ViewHolder {
//        TextView task_id, task_description, task_start_time, task_finish_time,
//                task_total_progress;
//        LinearLayout task_reward_layout;
//    }
//}
