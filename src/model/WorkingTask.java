package model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkingTask {
	private int id;
	private String description;
	private int now_progress;
	private int total_progress;
	private String unit;
	private String reward;
	private String start_time;
	private String original_start_time;
	private String note;
	private int todayWork;
	
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
