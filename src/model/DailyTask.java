package model;

/**
 * Created by Superxlcr
 * 日常任务类
 */
public class DailyTask {
	private int id;
	private String description; // 任务描述
	private int continuous; // 任务连续完成天数
	private int finishTimes; // 任务完成总天数
	private int finish; // 0 unfinish, 1 finish
	private String reward; // 奖励

	public DailyTask(int id, String description, int continuous,
			int finishTimes, int finish, String reward) {
		this.id = id;
		this.description = description;
		this.continuous = continuous;
		this.finishTimes = finishTimes;
		this.finish = finish;
		this.reward = reward;
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public int getContinuous() {
		return continuous;
	}

	public int getFinishTimes() {
		return finishTimes;
	}

	public boolean isFinish() {
		if (finish == 0) {
			return false;

		} else {
			return true;
		}
	}

	public String getReward() {
		return reward;
	}

}
