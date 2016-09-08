package com.yuzhuo.easyupbring.adapter;

import java.util.Date;
import java.util.List;

import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.bean.AttentionBean;
import com.yuzhuo.easyupbring.utils.DateUtil;
import com.yuzhuo.easyupbring.utils.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FansOrFocusAdapter extends BaseAdapter {

	private Context mContext;
	private List<AttentionBean> mList;
	private LayoutInflater inflater;
	ImageLoader imageLoader;

	public FansOrFocusAdapter(Context context, List<AttentionBean> list) {
		this.mContext = context;
		this.mList = list;
		imageLoader = ImageLoader.getIncetence(context);
		if (context != null) {
			this.inflater = LayoutInflater.from(context);
		}
	}

	public void setData(List<AttentionBean> list) {
		mList = list;
	}

	@Override
	public int getCount() {
		if (mList == null) {
			return 0;
		}
		return mList.size();
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
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();

			convertView = inflater.inflate(R.layout.item_listview_fans_focus,
					null);
			viewHolder.avatarImageView = (ImageView) convertView
					.findViewById(R.id.fans_focus_listitem_img_avatar);
			viewHolder.nameTextView = (TextView) convertView
					.findViewById(R.id.fans_focus_listitem_txt_nickname);
			viewHolder.salaryTextView = (TextView) convertView
					.findViewById(R.id.fans_focus_listitem_txt_salary);
			viewHolder.addrTextView = (TextView) convertView
					.findViewById(R.id.fans_focus_listitem_txt_addr);
			viewHolder.createtimeTextView = (TextView) convertView
					.findViewById(R.id.fans_focus_listitem_txt_createtime);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.createtimeTextView.setText(DateUtil.getLeaveNow(
				Long.parseLong(mList.get(position).getCreatetime()),
				new Date().getTime() / 1000));
		viewHolder.salaryTextView.setText(mList.get(position).getMsalary());
		viewHolder.addrTextView.setText(mList.get(position).getMlocation());
		viewHolder.nameTextView.setText(mList.get(position).getMnickname());
		imageLoader.displayImage(mList.get(position).getMavatar(),
				viewHolder.avatarImageView);
		return convertView;
	}

	public class ViewHolder {
		private TextView nameTextView, addrTextView, salaryTextView,
				createtimeTextView;
		private ImageView avatarImageView;
	}

}
