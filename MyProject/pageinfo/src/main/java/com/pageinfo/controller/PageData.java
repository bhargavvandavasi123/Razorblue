package com.pageinfo.controller;

import java.util.List;

public class PageData {

	private String type;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<String> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<String> permissions) {
		this.permissions = permissions;
	}
	public List<ChartItem> getChartItems() {
		return chartItems;
	}
	public void setChartItems(List<ChartItem> chartItems) {
		this.chartItems = chartItems;
	}
	private List<String> permissions;
	private List<ChartItem> chartItems;

}
