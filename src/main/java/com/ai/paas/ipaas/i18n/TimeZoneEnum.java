package com.ai.paas.ipaas.i18n;

public enum TimeZoneEnum {
	US_EST("GMT-5", 1), CHINA("GMT+8", 2),GMT("GMT",3);
	// 成员变量
	private String zone;
	private int index;

	// 构造方法，注意：构造方法不能为public，因为enum并不可以被实例化
	private TimeZoneEnum(String zone, int index) {
		this.zone = zone;
		this.index = index;
	}

	// 普通方法
	public static String getZone(int index) {
		for (TimeZoneEnum c : TimeZoneEnum.values()) {
			if (c.getIndex() == index) {
				return c.zone;
			}
		}
		return null;
	}

	// get set 方法
	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
