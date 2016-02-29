package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Superxlcr
 * 未完成任务类
 */
public class WorkingTask {
	private int id;
	private String description; // 任务描述
	private int now_progress; // 任务目前进度
	private int total_progress; // 任务总进度
	private String unit; // 任务单位
	private String reward; // 任务奖励
	private String start_time; // 任务开始时间
	private String original_start_time; // 任务开始时间戳
	private String note; // 任务注释
	private int todayWork; // 今日是否完成该任务
	
	public WorkingTask(int id , String description , int now_progress , int total_progress , String unit , String reward , String start_time , String note , int todayWork) {
		this.id = id;
		this.description = description;
		this.now_progress = now_progress;
		this.total_progress = total_progress;
		this.unit = unit;
		this.reward = reward;
		this.original_start_time = start_time;
		// 转换时间戳为格式时间
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
		String sd = sdf.format(new Date(Long.parseLong(start_time)));
		this.start_time = sd;
		this.note = note;
		this.todayWork = todayWork;
	}
	
	public int getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getNowProgress() {
		return this.now_progress;
	}
	
	public int getTotalProgress() {
		return total_progress;
	}
	
	public String getUnit() {
		return unit;
	}
	
	public String getReward() {
		return reward;
	}
	
	public String getStartTime() {
		return start_time;
	}
	
	public String getOriginalStartTime() {
		return original_start_time;
	}
	
	public void setNowProgress(int progress) {
		now_progress += progress;
		if (now_progress < 0) {
			now_progress = 0;
		}
		if (now_progress > total_progress) {
			now_progress = total_progress;
		}
	}
	
	public String getNote() {
		return note;
	}
	
	public boolean isTodayWork() {
		return todayWork == -1 ? false : true;
	}
	
	public int getTodayWork() {
		return todayWork;
	}
	
}
