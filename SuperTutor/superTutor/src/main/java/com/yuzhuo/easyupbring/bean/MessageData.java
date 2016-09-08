package com.yuzhuo.easyupbring.bean;

import java.io.Serializable;


public class MessageData implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final int TYPE_TEXT = 1;
	public static final int TYPE_RECORD = 3;
	public static final int TYPE_IMAGE = 2;
	public static final int TYPE_LOCATION = 4;
	public static final int TYPE_INVITE = 5;
	public static final int TYPE_INFOMATION = 6;

	public int type;
	public String massageData;

	public String getMassageData() {
		return massageData;
	}

	public void setMassageData(String massageData) {
		this.massageData = massageData;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
