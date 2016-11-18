package com.lte.bean;

import java.util.List;

public class CaseBean {
	/**
	 * behavior 的步骤
	 */
	private String index;
	/**
	 * behavior 的描述
	 */
	private String description;
	/**
	 * 测试behavior
	 */
	private List<ActionBean> behavior;

	public List<ActionBean> getBehavior() {
		return behavior;
	}

	public void setBehavior(List<ActionBean> behavior) {
		this.behavior = behavior;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
