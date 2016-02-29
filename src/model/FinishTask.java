package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Superxlcr
 * 已完成任务
 */
public class FinishTask {
	private int id;
	private String description; // 描述
	private int total_progress; // 任务总进度
	private String unit; // 单位
	private String reward; // 奖励
	private String start_time; // 任务开始时间
	private String finish_time; // 任务结束时间
	
	public FinishTask(int id , String description , int total_progress , String unit , String reward , String start_time , String finish_time) {
		this.id = id;
		this.description = description;
		this.total_progress = total_progress;
		this.unit = unit;
		this.reward = reward;
		// 转换时间戳为格式时间
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
		String sd = sdf.format(new Date(Long.parseLong(start_time)));
		this.start_time = sd;
		// 转换时间戳为格式时间 
		sd = sdf.format(new Date(Long.parseLong(finish_time)));
		this.finish_time = sd;
	}
	
	public int getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
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
	
	public String getFinishTime() {
		return finish_time;
	}
	
}
