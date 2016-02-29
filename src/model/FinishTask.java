package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Superxlcr
 * ���������
 */
public class FinishTask {
	private int id;
	private String description; // ����
	private int total_progress; // �����ܽ���
	private String unit; // ��λ
	private String reward; // ����
	private String start_time; // ����ʼʱ��
	private String finish_time; // �������ʱ��
	
	public FinishTask(int id , String description , int total_progress , String unit , String reward , String start_time , String finish_time) {
		this.id = id;
		this.description = description;
		this.total_progress = total_progress;
		this.unit = unit;
		this.reward = reward;
		// ת��ʱ���Ϊ��ʽʱ��
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
		String sd = sdf.format(new Date(Long.parseLong(start_time)));
		this.start_time = sd;
		// ת��ʱ���Ϊ��ʽʱ�� 
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
