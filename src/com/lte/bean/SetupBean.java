package com.lte.bean;

public class SetupBean {

	/** 根目录 */
	private String logpath;

	/** 按照时间或者次数测试 */
	private String time;

	/** case名称 */
	private String casename;

	/** 定时器 */
	private String clock;

	/** 双通道 */
	private String doubleroute;

	public String getDoubleroute() {
		return doubleroute;
	}

	public void setDoubleroute(String doubleroute) {
		this.doubleroute = doubleroute;
	}

	public String getClock() {
		return clock;
	}

	public void setClock(String clock) {
		this.clock = clock;
	}

	public String getLogpath() {
		return logpath;
	}

	public void setLogpath(String logpath) {
		this.logpath = logpath;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getCasename() {
		return casename;
	}

	public void setCasename(String casename) {
		this.casename = casename;
	}

}
