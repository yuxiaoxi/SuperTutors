package com.yuzhuo.easyupbring.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;

import org.json.JSONObject;

import android.R.integer;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.RemoteViews;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.activity.AppStartActivity;
import com.yuzhuo.easyupbring.activity.MainActivity;
import com.yuzhuo.easyupbring.activity.SingleChatAct;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.app.EasyUpApplication;
import com.yuzhuo.easyupbring.app.MessageManager;
import com.yuzhuo.easyupbring.app.NoticeManager;
import com.yuzhuo.easyupbring.bean.IMMessage;
import com.yuzhuo.easyupbring.bean.MessageData;
import com.yuzhuo.easyupbring.bean.MsgInfoBean;
import com.yuzhuo.easyupbring.bean.Notice;
import com.yuzhuo.easyupbring.utils.MyProperUtil;

/**
 * 
 * @ClassName: LBService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 余卓
 * @date 2014年12月24日 下午5:26:39
 * 
 */
public class MessageReceiveService extends Service {

	private IBinder binder = new MessageReceiveService.LocalBinder();
	public static boolean newMsg = false;
	public static boolean warnRed = false;
	public static Gson gson = new GsonBuilder()
			.enableComplexMapKeySerialization().create();
	public static boolean success;
	public static int syscounts;
	public static int messagecounts = 0;
	public static Set<Integer> systemSet = new HashSet<Integer>();
	public static Set<Integer> dynmicSet = new HashSet<Integer>();
	
	// public ProgressDialog dialog = null;
	private int msgTIME = 2500;
	private CusPerference cusPerference;
	private final String TAG = "SUPERTu";
	private int getCount = 0;

	@Override
	public IBinder onBind(Intent intent) {

		return binder;
	}

	@Override
	public void onCreate() {
		Log.e("start", "service-start");

		cusPerference = new CusPerference();
		cusPerference.load();
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		gethandler.postDelayed(getrunnable, msgTIME); // 每隔2.5s执行
		return START_STICKY_COMPATIBILITY;
	}

	@Override
	public void onDestroy() {
		Log.e("stop", "service-stop");
		super.onDestroy();
	}

	public void setTime() {
		if (getCount == 10) {
			msgTIME = getCount * 2500;
		}
	}

	Handler gethandler = new Handler();
	Runnable getrunnable = new Runnable() {

		@Override
		public void run() {
			// handler自带方法实现定时器
			try {
				setTime();
				getCount++;
				Log.e("msg_time", cusPerference.msg_time);
				getMsgInfoListById(AppContext.uid, "0", "1",
						cusPerference.msg_time);

				gethandler.postDelayed(this, msgTIME);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	// 定义内容类继承Binder
	public class LocalBinder extends Binder {
		// 返回本地服务
		MessageReceiveService getService() {
			return MessageReceiveService.this;
		}
	}

	private synchronized void processCustomMessage(MsgInfoBean msgInfoBean) {

		IMMessage newMessages = new IMMessage();
		Notice notice = new Notice();

		int type = Integer.parseInt(msgInfoBean.getContent_type());
		int flag = Integer.parseInt(msgInfoBean.getFlag());
		String from = msgInfoBean.getMsg_from();
		String to = msgInfoBean.getMsg_to();
		Long timeLong = new Date().getTime();
		int notype = (int) (timeLong % 1000);
		//
		// String time = DateUtil.date2Str(Calendar.getInstance(),
		// AppContext.MS_FORMART);
		long msgtime = new Date().getTime();

		if (MessageData.TYPE_TEXT == type) {
			newMessages.setType(MessageData.TYPE_TEXT);
			newMessages.setContent(msgInfoBean.getMsg_content());
		} else if (MessageData.TYPE_IMAGE == type) {
			newMessages.setType(MessageData.TYPE_IMAGE);

			newMessages.setContent(msgInfoBean.getMsg_content() + ","
					+ msgInfoBean.getMsg_width() + ","
					+ msgInfoBean.getMsg_height());
			Log.e("imgcontent---->", newMessages.getContent());
		} else if (MessageData.TYPE_RECORD == type) {
			newMessages.setType(MessageData.TYPE_RECORD);
			newMessages.setContent(msgInfoBean.getMsg_content() + ","
					+ msgInfoBean.getMsg_seconds());

		}
		newMessages.setFromSubJid("single" + "@" + from);

		newMessages.setMsgType(2);
		newMessages.setFromUid(from);
		newMessages.setTime(String.valueOf(msgtime));

		notice.setTitle("会话信息");
		notice.setNoticeType(Notice.CHAT_MSG);
		notice.setContent(newMessages.getContent());
		notice.setFrom(newMessages.getFromSubJid());
		notice.setTo("single@" + AppContext.uid);
		notice.setStatus(Notice.UNREAD);
		notice.setNoticeTime(String.valueOf(msgtime));
		getUserI(from, to, MessageReceiveService.this, msgInfoBean.getMsg_content(),
				notype, newMessages, notice);

	}

	void getMsgInfoListById(final String msg_to, final String offset,
			final String num, final String msg_time) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("rid", msg_to);
		map.put("offset", offset);
		map.put("num", num);
		map.put("msg_time", msg_time);
		String url = MyProperUtil.getProperties(MessageReceiveService.this,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(MessageReceiveService.this,
						"appConfigDebug.properties").getProperty(
						"getMsgInfoListById")
				+ "&data="
				+ MessageReceiveService.makeParam(map);
		DhNet net = new DhNet(url);
		Log.e("UrlString", "ulr = " + url);

		net.doGet(new NetTask(MessageReceiveService.this) {
			@Override
			public void onErray(Response response) {
				super.onErray(response);
				// 错误处理,出错后会先关闭对话框然后调用这个方法,默认不处理
				Log.e("JoJo", "onErray");
			}

			@Override
			public void doInUI(Response response, Integer transfer) {
				// String message = JSONUtil.getString(response.jSON(),
				// "data.message");
				Log.e("msgString---->", response.plain().toString());
				if ("1".equals(JSONUtil.getString(response.jSON(), "data.code"))) {
					List<MsgInfoBean> list = response.listFrom(
							MsgInfoBean.class, "data.item");
					if (list.size() > 0)
						msgTIME = 2500;
					for (int i = 0; i < list.size(); i++) {
						processCustomMessage(list.get(i));
						if (i == list.size() - 1) {
							cusPerference.msg_time = list.get(i).getMsg_time();
							cusPerference.commit();
						}
					}

				}

			}
		});

	}

	private String getRunningActivityName() {
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity
				.getClassName();
		return runningActivity;
	}

	public void processIMMessage(String from, String to, Context context,
			String message, int notype, String name,
			final IMMessage newMessages, final Notice notice) {

		if (isRunningForeground(context)) {
			Log.e(TAG, "在运行的时候");
			try {

				Log.e("activityname", getRunningActivityName());
				if (!getRunningActivityName().equals(
						"com.yuzhuo.easyupbring.activity.MainActivity")
						&& !getRunningActivityName()
								.equals("com.yuzhuo.easyupbring.activity.SingleChatAct")) {
					Intent mIntent = new Intent(AppContext.REFRESH_WARN_RECORD);
					sendStickyBroadcast(mIntent);
					Intent intent = new Intent();
					intent.setClass(context, SingleChatAct.class);
					intent.putExtra("fid", "single" + "@" + from);
					intent.putExtra("name", name);
					if (!from.equals(AppContext.uid)) {
						showCustomizeNotification(context,
								R.drawable.ic_launcher, "聊天消息", message,
								intent, Integer.parseInt(from) + 10000);
					}

					messagecounts++;

				}
				if (getRunningActivityName().equals(
						"com.yuzhuo.easyupbring.activity.MainActivity")) {
					if (MainActivity.tabHost.getCurrentTabTag().equals("near")) {
						Log.e("jojo", "进来了");
						Intent mIntent = new Intent(
								AppContext.REFRESH_MESSAGE_RECORD);
						sendBroadcast(mIntent);
					} else {
						messagecounts++;
						if (!from.equals(AppContext.uid)) {
							// messagecounts++;
							Intent mIntent = new Intent(
									AppContext.REFRESH_WARN_RECORD);
							sendBroadcast(mIntent);

							// MainAct.main_tab_new_message
							// .setVisibility(View.VISIBLE);
						}

					}
					Intent intent = new Intent();

					intent.setClass(context, SingleChatAct.class);
					intent.putExtra("fid", "single" + "@" + from);
					intent.putExtra("name", name);
					if (!from.equals(AppContext.uid)) {
						showCustomizeNotification(context,
								R.drawable.ic_launcher, "聊天消息", message,
								intent, Integer.parseInt(from) + 10000);
					}

				}

				if (getRunningActivityName().equals(
						"com.yuzhuo.easyupbring.activity.SingleChatAct")) {

					if (name.equals(SingleChatAct.titelTextView.getText())) {
						newMsg = true;

						Log.e("messageString", newMessages.toString());
						// message_pool.add(newMessages);

						Intent mIntent = new Intent(
								AppContext.SINGLE_CHAT_NEW_MESSAGE);
						mIntent.putExtra("IMMessage", newMessages);
						sendBroadcast(mIntent);
						NoticeManager.getInstance(MessageReceiveService.this)
								.updateStatusByFrom(
										newMessages.getFromSubJid(),
										Notice.READ);

					} else {
						Log.e("提示", "不在当前activity");

						Intent intent = new Intent(context, SingleChatAct.class);
						intent.putExtra("fid", "single" + "@" + from);
						intent.putExtra("name", name);
						showCustomizeNotification(context,
								R.drawable.ic_launcher, "聊天消息", message,
								intent, Integer.parseInt(from) + 10000);
						messagecounts++;

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			messagecounts = 0;

			Intent intent = new Intent(context, AppStartActivity.class);
			intent.putExtra("fid", "single" + "@" + from);
			intent.putExtra("name", name);
			showCustomizeNotification(context, R.drawable.ic_launcher, "聊天消息",
					message, intent, Integer.parseInt(from) + 10000);
			Intent mIntent = new Intent(AppContext.MESSAGE_WARN_RECORD);
			mIntent.putExtra("IMMessage", newMessages);
			sendStickyBroadcast(mIntent);

		}
	}

	// 自定义显示的通知 ，创建RemoteView对象
	private synchronized void showCustomizeNotification(Context context,
			int iconId, String contentTitle, String contentText, Intent intent,
			int type) {
		int icon = R.drawable.ic_launcher;
		long when = new Date().getTime() / 1000;
		Notification noti = new Notification(icon, contentTitle, when);
		noti.flags = Notification.FLAG_AUTO_CANCEL;
		cusPerference.load();

		noti.defaults = Notification.DEFAULT_ALL;
		// 1、创建一个自定义的消息布局 view.xml
		// 2、在程序代码中使用RemoteViews的方法来定义image和text。然后把RemoteViews对象传到contentView字段
		RemoteViews remoteView = new RemoteViews(this.getPackageName(),
				R.layout.notification);
		remoteView.setImageViewResource(R.id.notification_img,
				R.drawable.ic_launcher);
		remoteView.setTextViewText(R.id.notification_txt, contentText);
		remoteView.setTextViewText(R.id.notification_time,
				getTime(String.valueOf(when)));
		noti.contentView = remoteView;
		// 3、为Notification的contentIntent字段定义一个Intent(注意，使用自定义View不需要setLatestEventInfo()方法)

		// 这儿点击后简单启动Settings模块
		PendingIntent contentIntent = PendingIntent.getActivity(context, type,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		noti.contentIntent = contentIntent;
		NotificationManager mnotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mnotiManager.notify(type, noti);
	}

	private void getUserI(final String uid, final String to,
			final Context context, final String message, final int notype,
			final IMMessage newMessages, final Notice notice) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("rid", uid);
		String url = MyProperUtil.getProperties(this,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(this, "appConfigDebug.properties")
						.getProperty("getUserInfoById")
				+ "&data="
				+ makeParam(map);
		DhNet net = new DhNet(url);
		// 添加参数
		// net.addParam("data", makeParameters(uid, type, page,num));
		Log.e("XIaoxi", "notice ulr = " + url);
		net.doGet(new NetTask(this) {

			@Override
			public void doInBackground(Response response) {

				String name = JSONUtil.getString(response.jSON(),
						"data.item.username");

				Log.e("name", name);

				newMessages.setPicPath(setFull(JSONUtil.getString(
						response.jSON(), "data.item.avatar"))
						+ "," + setFull(AppContext.userInfoBean.getAvatar()));

				// newMessages.setPicPath(JSONUtil.getString(response.jSON(),
				// "data.item.avatar") + "," + AppContext.AVATER);

				newMessages.setName(name + ","
						+ AppContext.userInfoBean.getUsername()); //

				notice.setName(name);
				if (!"".equals(JSONUtil.getString(response.jSON(),
						"data.item.avatar"))) {
					notice.setPicPath(JSONUtil.getString(response.jSON(),
							"data.item.avatar") + "," + "aaaaa");
				} else {
					notice.setPicPath("bbb" + "," + "aaaaa");
				}
				if (!uid.equals(AppContext.uid)) {

					MessageManager.getInstance(MessageReceiveService.this).saveIMMessage(
							newMessages);
					long n = NoticeManager.getInstance(MessageReceiveService.this)
							.saveNotice(notice);
					Log.e("long", "" + n);
				}
				Log.e("recordmsg", newMessages.toString());

				processIMMessage(uid, to, context, message, notype, name,
						newMessages, notice);
			}

			@Override
			public void doInUI(Response response, Integer transfer) {

			}
		});
	}

	private boolean isRunningForeground(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String currentPackageName = cn.getPackageName();
		if (!TextUtils.isEmpty(currentPackageName)
				&& currentPackageName.equals(context.getPackageName())) {
			return true;
		}

		return false;
	}

	public String setFull(String s) {
		if ("".equals(s) || s == null) {
			s = "aaaaa";
		}
		return s;
	}

	/**
	 * 
	 * @Title: makeParam
	 * @Description: 上传文本转换成base64
	 * @param @param map
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String makeParam(Map<String, String> map) {
		JSONObject arr = null;
		String parameters;
		try {
			arr = new JSONObject(map);
			Log.e("json-->", arr.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			parameters = new String(Base64.encode(gson.toJson(map).getBytes(),
					Base64.NO_WRAP));
			return URLEncoder.encode(parameters, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;

	}

	private String getTime(String time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = null;
		if (time.equals("")) {
			return "";
		}
		sdf = new SimpleDateFormat("HH:mm:ss");
		long loc_time = Long.valueOf(time);
		re_StrTime = sdf.format(new Date(loc_time * 1000L));
		return re_StrTime;
	}

}
