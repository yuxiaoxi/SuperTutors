package com.yuzhuo.easyupbring.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.utils.DateUtil;

public class IMMessage implements Parcelable, Comparable<IMMessage> {
	public static final String IMMESSAGE_KEY = "immessage.key";
	public static final String KEY_TIME = "immessage.time";
	public static final int SUCCESS = 0;
	public static final int ERROR = 1;
	private int type;
	private String content;
	private String time;
	private String  picPath;
	private String name;
	private String fromUid;
	/**
	 * 存在本地，表示与谁聊天
	 */
	private String fromSubJid;
	/**
	 * 0:接受 1：发送
	 */
	private int msgType = 0;
	
	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public IMMessage() {
		this.type = SUCCESS;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getFromSubJid() {
		return fromSubJid;
	}

	public void setFromSubJid(String fromSubJid) {
		this.fromSubJid = fromSubJid;
	}

	public String getFromUid() {
		return fromUid;
	}

	public void setFromUid(String fromUid) {
		this.fromUid = fromUid;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(type);
		dest.writeString(content);
		dest.writeString(time);
		dest.writeString(fromSubJid);
		dest.writeString(fromUid);
		dest.writeInt(msgType);
		dest.writeString(picPath);
		dest.writeString(name);
	}

	public static final Parcelable.Creator<IMMessage> CREATOR = new Parcelable.Creator<IMMessage>() {

		@Override
		public IMMessage createFromParcel(Parcel source) {
			IMMessage message = new IMMessage();
			message.setType(source.readInt());
			message.setContent(source.readString());
			message.setTime(source.readString());
			message.setFromSubJid(source.readString());
			message.setFromUid(source.readString());
			message.setMsgType(source.readInt());
			message.setPicPath(source.readString());
			message.setName(source.readString());			
			return message;
		}

		@Override
		public IMMessage[] newArray(int size) {
			return new IMMessage[size];
		}

	};

	/**
	 * 新消息的构造方法.
	 * 
	 * @param content
	 * @param time
	 */
	public IMMessage(String content, String time, String withSb, int msgType,String witeName,String witePicPath,String fromUid) {
		super();
		this.content = content;
		this.time = time;
		this.msgType = msgType;
		this.fromSubJid = withSb;
		this.fromUid = fromUid;
		this.name = witeName;
		this.picPath = witePicPath;
	}

	/**
	 * shimiso 按时间降序排列
	 */
	@Override
	public int compareTo(IMMessage oth) {
		if (null == this.getTime() || null == oth.getTime()) {
			return 0;
		}
		String format = null;
		String time1 = this.getTime();
		String time2 = oth.getTime();
//		if (this.getTime().length() == oth.getTime().length()
//				&& this.getTime().length() == 23) {
//			time1 = this.getTime();
//			time2 = oth.getTime();
//			format = AppContext.MS_FORMART;
//		} else {
//			time1 = this.getTime().substring(0, 19);
//			time2 = oth.getTime().substring(0, 19);
//		}
		Date da1 = DateUtil.str2Date(getTime(time1), AppContext.MS_FORMART);
		Date da2 = DateUtil.str2Date(getTime(time2), AppContext.MS_FORMART);
		if (da1.before(da2)) {
			return -1;
		}
		if (da2.before(da1)) {
			return 1;
		}

		return 0;
	}

	@Override
	public String toString() {
		return "IMMessage [type=" + type + ", content=" + content + ", time="
				+ time + ", fromSubJid=" + fromSubJid + ",fromUid="+fromUid+", msgType=" + msgType
				+ ", picPath=" + picPath + ", name=" + name + "]";
	}
	
	private String getTime(String time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = null;
		if (time.equals("")) {
			return "";
		}
		sdf = new SimpleDateFormat(AppContext.MS_FORMART);
		long loc_time = Long.valueOf(time);
		re_StrTime = sdf.format(new Date(loc_time));
		return re_StrTime;
	}
	
}
