package com.yuzhuo.easyupbring.adapter;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.bean.ChartHisBean;
import com.yuzhuo.easyupbring.bean.MessageData;
import com.yuzhuo.easyupbring.utils.FaceTextUtils;
import com.yuzhuo.easyupbring.utils.ImageLoader;

public class MessageUserAdapter extends BaseAdapter {
	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private List<ChartHisBean> inviteUsers;
	ViewHolder viewHolder;
	private ImageLoader imageLoader;

	public MessageUserAdapter(ArrayList<ChartHisBean> list, Context context) {
		this.mContext = context;
		this.inviteUsers = list;
		if (context != null) {
			this.mInflater = LayoutInflater.from(context);
			imageLoader = ImageLoader.getIncetence(context);
		}

		/*
		 * messageManager = MessageManager.getInstance(context); inviteUsers =
		 * messageManager .getRecentContactsWithLastMsg();
		 */
	}

	@Override
	public int getCount() {

		if (inviteUsers == null) {
			return 0;
		} else {
			return inviteUsers.size();
		}

	}

	/***** 设置数据 *******/
	public void setData(List<ChartHisBean> list) {
		this.inviteUsers = list;

	}

	@Override
	public Object getItem(int position) {

		return position;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int position1 = position;
		ChartHisBean notice = inviteUsers.get(position);
		Integer ppCount = notice.getNoticeSum();
		viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.message_list_item_record,
					null);
			viewHolder = new ViewHolder();
			viewHolder.nicknameTextView = (TextView) convertView
					.findViewById(R.id.ztgame_txt_friends_listitem_nickname);
			viewHolder.paopao = (TextView) convertView
					.findViewById(R.id.paopao);
			viewHolder.lastTextView = (TextView) convertView
					.findViewById(R.id.ztgame_txt_friends_listitem_lasttime);
			viewHolder.signTextView = (TextView) convertView
					.findViewById(R.id.ztgame_txt_friends_listitem_sign);

			viewHolder.logoImageView = (net.duohuo.dhroid.view.megwidget.CircleImageView) convertView
					.findViewById(R.id.ztgame_img_friends_listitem_logo);

			viewHolder.linearLayout = (LinearLayout) convertView
					.findViewById(R.id.ztgame_linear_friends_listitem_liner);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (inviteUsers.size() > 0) {
			if (ppCount != null && ppCount > 0) {
				viewHolder.paopao.setText(ppCount + "");
				viewHolder.paopao.setVisibility(View.VISIBLE);
			} else {
				viewHolder.paopao.setVisibility(View.GONE);
			}

			String loaclName = "";
			String name = "";
			String titleName = inviteUsers.get(position1).getName();
			String classname = "";
			Log.e("MJJ", "name  " + titleName + "   ==>  type"
					+ inviteUsers.get(position1).getType());
			if (inviteUsers.get(position1).getType() == 1
					&& titleName.contains(",")) {
				name = titleName.split(",")[1];
				loaclName = titleName.split(",")[1];
				classname = titleName.split(",")[1];
			} else {
				if (titleName.contains(",")) {
					name = titleName.split(",")[0];
					loaclName = titleName.split(",")[0];
					classname = titleName.split(",")[1];
				}
			}
			/*
			 * if(inviteUsers.get(position).getFrom().contains("_")){ name =
			 * inviteUsers.get(position).getName().split(",")[1]; }else{ name =
			 * inviteUsers.get(position).getName(); }
			 */
			String jid = inviteUsers.get(position1).getFrom();
			if (jid.contains("group@")) {
				viewHolder.logoImageView.setVisibility(View.VISIBLE);
				viewHolder.nicknameTextView.setText(classname);
				//
				// getChildAndParentInfo(jid.split("@")[1].split("_")[1],
				// viewHolder.nicknameTextView, viewHolder.logoImageView);
			} else if (jid.contains("class@")) {
				// getRoomInfoById(jid.split("@")[1], "0",
				// viewHolder.nicknameTextView);
//				viewHolder.nicknameTextView.setText(classname);
//				ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
//				for (int i = 0; i < AppContext.avatarList.size() && i < 5; i++) {
//					Bitmap bitmap = imageLoader.getBitmap(AppContext.avatarList
//							.get(i));
//					if (bitmap != null) {
//						bitmaps.add(bitmap);
//					} else {
//						Resources r = mContext.getResources();
//						InputStream is = r
//								.openRawResource(R.drawable.ic_launcher);
//						BitmapDrawable bmpDraw = new BitmapDrawable(is);
//						Bitmap bmp = bmpDraw.getBitmap();
//						bitmaps.add(bmp);
//					}
//
//				}
				viewHolder.logoImageView.setVisibility(View.GONE);
			} else {
				viewHolder.nicknameTextView.setText(loaclName);
			}
			String noticeTime = getTime(inviteUsers.get(position)
					.getNoticeTime());
			Calendar c = Calendar.getInstance();// 可以对每个时间域单独修改
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH) + 1;
			int date = c.get(Calendar.DATE);
			int years = 0;
			int months = 0;
			int dates = 0;
			if (noticeTime.contains("-")) {
				// noticeTime =
				// noticeTime.split("-")[1]+"-"+noticeTime.split("-")[2];
				years = Integer.parseInt(noticeTime.split("-")[0]);
				months = Integer.parseInt(noticeTime.split("-")[1]);
				dates = Integer
						.parseInt(noticeTime.split("-")[2].split(" ")[0]);
				if (year == years && month == months && date == dates) {
					noticeTime = noticeTime.split("-")[2].split(" ")[1];
				} else {
					noticeTime = noticeTime.split("-")[1] + "-"
							+ noticeTime.split("-")[2];
				}
			}
			viewHolder.lastTextView.setText(noticeTime);
			String contents = inviteUsers.get(position).getContent();
			int type = inviteUsers.get(position).getMsg_type();
			if (type == MessageData.TYPE_IMAGE) {
				contents = "[图片]";
			} else if (type == MessageData.TYPE_INVITE) {
				contents = "[文件]";
			} else if (type == MessageData.TYPE_LOCATION) {
				contents = "[位置]";
			} else if (type == MessageData.TYPE_RECORD) {
				contents = "[语音]";
			}
			if (type == MessageData.TYPE_TEXT) {
				SpannableString spannableString = FaceTextUtils
						.toSpannableString(mContext, contents);
				viewHolder.signTextView.setText(spannableString);
			} else {
				viewHolder.signTextView.setText(contents);
			}

			String avatar = inviteUsers.get(position).getPicPath();

			String logoavatar = "";
			String groupavatar = "aaa";
			if (avatar != null && avatar.contains(",") && !avatar.equals(",")) {
				if (1 == inviteUsers.get(position).getType()) {
					logoavatar = avatar.split(",")[1];
				} else if (2 == inviteUsers.get(position).getType()) {
					logoavatar = avatar.split(",")[0];
				}
				groupavatar = avatar.split(",")[1];
			}
			if (jid.contains("single@")) {
				Log.e("avatar", logoavatar);
				imageLoader.displayImage(logoavatar, viewHolder.logoImageView);
				viewHolder.logoImageView.setVisibility(View.VISIBLE);
			} else if (jid.contains("group@")) {
				imageLoader.displayImage(groupavatar, viewHolder.logoImageView);

			}
		}
		// viewHolder.logoImageView.setOnClickListener(new
		// View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// }
		// });
		return convertView;
	}

	private class ViewHolder {
		public TextView nicknameTextView, signTextView, lastTextView, paopao;
		public ImageView logoImageView;
		public LinearLayout linearLayout;
	}

	/**
	 * 
	 * @Title: getTime
	 * @Description: 将时间戳转换成时间字符串
	 * @param @param time
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
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
