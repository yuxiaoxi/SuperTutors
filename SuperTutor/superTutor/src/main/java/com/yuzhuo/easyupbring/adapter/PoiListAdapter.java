package com.yuzhuo.easyupbring.adapter;

import java.util.List;
import com.baidu.mapapi.search.MKPoiInfo;
import com.yuzhuo.easyupbring.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
* @ClassName: PoiListAdapter 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author 余卓 
* @date 2014年12月24日 下午12:52:06 
*
 */
public class PoiListAdapter extends BaseAdapter {

	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private List<MKPoiInfo> datalist = null;
	private Handler handler;
	public PoiListAdapter(Context context,List<MKPoiInfo> dataList,Handler handler){
		this.mContext = context;
		this.datalist = dataList;
		if (context != null) {
			this.mInflater = LayoutInflater.from(context);
		}
		this.handler = handler;
	}
	
	@Override
	public int getCount() {
		if (datalist==null) {
			return 0;
		}else {
			return datalist.size();
		}
		
	}
	
	public void setData(List<MKPoiInfo> list) {
		this.datalist = list;
		
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
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater
					.inflate(R.layout.listitem_popview_poplist, null);
			viewHolder = new ViewHolder();
			viewHolder.poinameTextView = (TextView) convertView.findViewById(R.id.poiname_txt);		
			viewHolder.clickLayout = (LinearLayout) convertView.findViewById(R.id.linear_click);
			viewHolder.poinameTextView.setText(datalist.get(position1).name);
			viewHolder.clickLayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					geoPoint(datalist.get(position1));
				}
			});
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		return convertView;
	}
	
	private class ViewHolder{
		private TextView poinameTextView;
		private LinearLayout clickLayout;
	}
	
	public void geoPoint(MKPoiInfo poiInfo){
		Message msg = new Message();
		if (poiInfo!=null) {
			
			msg.obj = poiInfo;
			msg.what = 0 ;
		}else {
			msg.what = 1;
		}
		handler.sendMessage(msg);
	}

}
