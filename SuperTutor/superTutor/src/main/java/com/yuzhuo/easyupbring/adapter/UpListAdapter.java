package com.yuzhuo.easyupbring.adapter;

import java.util.Date;
import java.util.List;

import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.bean.UpBringBean;
import com.yuzhuo.easyupbring.service.APIService;
import com.yuzhuo.easyupbring.utils.DateUtil;
import com.yuzhuo.easyupbring.utils.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UpListAdapter extends BaseAdapter {

	private Context mContext;
	private List<UpBringBean> mList;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	public UpListAdapter(Context context, List<UpBringBean> list) {
		this.mContext = context;
		this.mList = list;
		imageLoader = ImageLoader.getIncetence(context);
		if (context != null) {
			this.inflater = LayoutInflater.from(context);
		}
	}

	public void setData(List<UpBringBean> list) {
		this.mList = list;
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
			if ("1".equals(AppContext.CHARACTER)) {
				convertView = inflater.inflate(
						R.layout.item_listview_home_parent, null);
				viewHolder.statusTextView = (TextView) convertView
						.findViewById(R.id.home_txt_listitem_status);
				viewHolder.levelTextView = (TextView) convertView
						.findViewById(R.id.home_txt_listitem_level);
			} else {
				convertView = inflater.inflate(
						R.layout.item_listview_home_teacher, null);
				viewHolder.professionalTextView = (TextView) convertView
						.findViewById(R.id.home_txt_listitem_professiona);
				viewHolder.schoolTextView = (TextView) convertView
						.findViewById(R.id.home_txt_listitem_university);
				viewHolder.levelTextView = (TextView) convertView.findViewById(R.id.home_txt_listitem_level);

			}

			viewHolder.courseTextView = (TextView) convertView
					.findViewById(R.id.home_txt_listitem_course);
			viewHolder.salaryTextView = (TextView) convertView
					.findViewById(R.id.home_txt_listitem_salary);
			viewHolder.nicknameTextView = (TextView) convertView
					.findViewById(R.id.home_txt_listitem_nickname);
			viewHolder.distanceTextView = (TextView) convertView
					.findViewById(R.id.home_txt_listitem_distance);
			viewHolder.creatTimeTextView = (TextView) convertView
					.findViewById(R.id.home_txt_listitem_createtime);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if ("1".equals(AppContext.CHARACTER)) {
			if ("0".equals(mList.get(position).getStatus())) {
				viewHolder.statusTextView.setText("未结束");
			} else if ("1".equals(mList.get(position).getStatus())) {
				viewHolder.statusTextView.setText("已结束");
			} else if ("2".equals(mList.get(position).getStatus())) {
				viewHolder.statusTextView.setText("已过期");
			}
			viewHolder.levelTextView.setText(mList.get(position).getGrade());
		} else {
			viewHolder.professionalTextView.setText(mList.get(position)
					.getProfessional());
			viewHolder.schoolTextView.setText(mList.get(position).getSchool());
			viewHolder.levelTextView.setText(mList.get(position).getGrade());
		}
		viewHolder.avatarImageView = (ImageView) convertView.findViewById(R.id.home_img_listitem_avatar);
		viewHolder.nicknameTextView.setText(mList.get(position).getNickname());
		viewHolder.creatTimeTextView.setText(DateUtil.getLeaveNow(
				Long.parseLong(mList.get(position).getCreatetime()),
				new Date().getTime()/1000));
		viewHolder.courseTextView.setText(mList.get(position).getCourse());
		viewHolder.salaryTextView.setText(mList.get(position).getSalary());
		imageLoader.displayImage(mList.get(position).getAvatar(), viewHolder.avatarImageView);
		return convertView;
	}

	public class ViewHolder {
		private TextView courseTextView, salaryTextView, nicknameTextView,
				professionalTextView, schoolTextView, levelTextView,
				creatTimeTextView, distanceTextView, statusTextView;
		private ImageView avatarImageView;
	}

}
