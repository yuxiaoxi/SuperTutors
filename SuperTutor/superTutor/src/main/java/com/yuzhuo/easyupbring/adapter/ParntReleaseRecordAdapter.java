package com.yuzhuo.easyupbring.adapter;

import java.util.Date;
import java.util.List;

import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.bean.UpBringBean;
import com.yuzhuo.easyupbring.bean.UserBean;
import com.yuzhuo.easyupbring.utils.DateUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ParntReleaseRecordAdapter extends BaseAdapter {

	private Context mContext;
	private List<UpBringBean> mList;
	private LayoutInflater inflater;

	public ParntReleaseRecordAdapter(Context context, List<UpBringBean> list) {
		this.mContext = context;
		this.mList = list;
		if (context != null) {
			this.inflater = LayoutInflater.from(context);
		}
	}
	
	public void setData(List<UpBringBean> list){
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

			convertView = inflater.inflate(R.layout.item_listview_parents_release_record,
					null);
			viewHolder.createtimeTextView = (TextView) convertView.findViewById(R.id.collect_record_txt_listitem_createtime);
			viewHolder.salaryTextView = (TextView) convertView.findViewById(R.id.collect_record_txt_listitem_salary);
			viewHolder.courseTextView = (TextView) convertView.findViewById(R.id.collect_record_txt_listitem_course);
			viewHolder.gradeTextView = (TextView) convertView.findViewById(R.id.collect_record_txt_listitem_grade);
			viewHolder.statusTextView = (TextView) convertView.findViewById(R.id.collect_record_txt_listitem_status);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.createtimeTextView.setText(DateUtil.getLeaveNow(
				Long.parseLong(mList.get(position).getCreatetime()),
				new Date().getTime()/1000));
		viewHolder.salaryTextView.setText(mList.get(position).getSalary());
		viewHolder.courseTextView.setText(mList.get(position).getCourse());
		viewHolder.gradeTextView.setText(mList.get(position).getGrade());
		if("0".equals(mList.get(position).getStatus())){
			viewHolder.statusTextView.setText("未结束");
		}else {
			viewHolder.statusTextView.setText("结束");
		}
		
		if ("0".equals(AppContext.CHARACTER)) {
			//家长
			
		}else if("1".equals(AppContext.CHARACTER)){
			//教师
		}
		return convertView;
	}

	public class ViewHolder {
		private TextView createtimeTextView,salaryTextView,courseTextView,gradeTextView,statusTextView;
		
	}

}
