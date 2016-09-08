package com.yuzhuo.easyupbring.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.utils.DateUtil;

/**
 * 
 * 最近联系人显示的与某个的聊天记录bean，包括 收到某个人的最后一条信息的全部内容，收到某人未读信息的数量总和
 * 
 * @author shimiso
 */
public class ChartHisBean implements Comparable<ChartHisBean> {
	public static final int ADD_FRIEND = 1;// 好友请求
	public static final int SYS_MSG = 2; // 系统消息
	public static final int CHAT_MSG = 3;// 聊天消息

	public static final int READ = 0;
	public static final int UNREAD = 1;
	private String id; // 主键
	private String title; // 标题
	private String content; // 最后内容
	private Integer status; // 最后状态 0已读 1未读
	private String from; // 最后通知来源
	private String to; // 最后通知去想
	private String noticeTime; // 最后通知时间
	private Integer noticeSum;// 收到未读消息总数、
	private Integer noticeType; // 消息类型 1.好友请求 2.系统消息
	private Integer type; // 1 send
	private Integer msg_type;

	public Integer getMsg_type() {
		return msg_type;
	}

	public void setMsg_type(Integer msg_type) {
		this.msg_type = msg_type;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	private String picPath;

	public Integer getNoticeSum() {
		return noticeSum;
	}

	public void setNoticeSum(Integer noticeSum) {
		this.noticeSum = noticeSum;
	}

	public Integer getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(Integer noticeType) {
		this.noticeType = noticeType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getNoticeTime() {
		return noticeTime;
	}

	public void setNoticeTime(String noticeTime) {
		this.noticeTime = noticeTime;
	}

	@Override
	public int compareTo(ChartHisBean oth) {

		if (null == this.noticeTime || null == oth.noticeTime) {
			return 0;
		}
		String time1 = this.noticeTime;
		String time2 = oth.noticeTime;
		if (this.from.startsWith("class@")) {
			return 1;
		}
		if (oth.from.startsWith("class@")) {
			return 1;
		}
		Date da1 = DateUtil.str2Date(getTime(time1), AppContext.MS_FORMART);
		Date da2 = DateUtil.str2Date(getTime(time2), AppContext.MS_FORMART);
	
		if (da1.before(da2)) {
			return 1;
		}
		if (da2.before(da1)) {
			return -1;
		}

		return 0;

	}

	public ChartHisBean(String id, String title, String content,
			Integer status, String from, String to, String noticeTime,
			Integer noticeSum, Integer noticeType, Integer type,
			Integer msg_type, String name, String picPath) {
		super();
		this.id = id;
		this.title = title;
		this.content = content;
		this.status = status;
		this.from = from;
		this.to = to;
		this.noticeTime = noticeTime;
		this.noticeSum = noticeSum;
		this.noticeType = noticeType;
		this.type = type;
		this.msg_type = msg_type;
		this.name = name;
		this.picPath = picPath;
	}

	public ChartHisBean() {
		super();
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
