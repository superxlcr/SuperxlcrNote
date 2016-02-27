package model;

public class DailyTask {
	private int id;
	private String description;
	private int continuous;
	private int finishTimes;
	private int finish; // 0 unfinish, 1 finish
	private String reward;

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
