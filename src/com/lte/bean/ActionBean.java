package com.lte.bean;

public class ActionBean {
	private String step;
	private String time;
	private String delay;
	private String duration;
	private String action;

	/**
	 * 
	 * @param step
	 *            步骤编号
	 * @param time
	 *            0代表是根据时间 1代表根据重复次数
	 * @param delay
	 *            延时时间表示在上一步(Step)执行完后延长多长时间执行此步骤
	 * @param duration
	 *            执行时间表示该测试例的规定执行时间（到时间未完成的话就强制停止，当按时间执行时有效，当按次数执行时则是最大的运行时间）
	 * @param action
	 *            测试用例的名字
	 */
	public ActionBean(String step, String time, String delay, String duration, String action) {
		super();
		this.step = step;
		this.time = time;
		this.delay = delay;
		this.duration = duration;
		this.action = action;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDelay() {
		return delay;
	}

	public void setDelay(String delay) {
		this.delay = delay;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public String toString() {
		return "ActionBean [step=" + step + ", time=" + time + ", delay=" + delay + ", duration="
				+ duration + ", action=" + action + "]";
	}

}
