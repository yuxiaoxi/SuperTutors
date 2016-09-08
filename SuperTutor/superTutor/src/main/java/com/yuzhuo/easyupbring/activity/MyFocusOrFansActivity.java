package com.yuzhuo.easyupbring.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.view.PullToRefreshListView;
import net.duohuo.dhroid.view.PullToRefreshBase.OnRefreshListener;

import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.adapter.FansOrFocusAdapter;
import com.yuzhuo.easyupbring.adapter.UpListAdapter;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.bean.AttentionBean;
import com.yuzhuo.easyupbring.bean.UpBringBean;
import com.yuzhuo.easyupbring.bean.UserBean;
import com.yuzhuo.easyupbring.service.LBService;
import com.yuzhuo.easyupbring.utils.MyProperUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyFocusOrFansActivity extends BaseAct {

	@InjectView(id = R.id.myfocus_fans_linear_back, click = "onClickCallBack")
	private LinearLayout backLayout;
	private ListView listView;
	@InjectView(id = R.id.myfocus_fans_list_body)
	private PullToRefreshListView myfocus_fans_list_body;

	public static final int MODE_PULL_DOWN_TO_REFRESH = 0x1; // 下拉
	public static final int MODE_DEFAULT_LOAD = 0x2; // 上拉
	public boolean isRefresh = false;
	public int offsete;

	private FansOrFocusAdapter adapter;
	private List<AttentionBean> dataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_myfocus_fans);
		initView();
		loadData();
	}

	public void initView() {
		dataList = new ArrayList<AttentionBean>();
		myfocus_fans_list_body.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(int pullDirection) {
				switch (pullDirection) {
				// 1下拉,2上拉
				case MODE_PULL_DOWN_TO_REFRESH:
					isRefresh = true;
					offsete = 0;
					dataList.clear();
					loadData();
					break;
				case MODE_DEFAULT_LOAD:
					loadData();
					break;
				}
			}
		});

		listView = myfocus_fans_list_body.getRefreshableView();
		listView.setDividerHeight(0);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> view, View arg1, int position,
					long id) {
				Intent intent = new Intent();

				intent.setClass(MyFocusOrFansActivity.this,
						ParentReleaseRecordActivity.class);
				intent.putExtra("rid", dataList.get(position).getFrid());
				intent.putExtra("name", dataList.get(position).getMnickname());
				intent.putExtra("avatar", dataList.get(position).getMavatar());
				startActivity(intent);
			}
		});
		adapter = new FansOrFocusAdapter(MyFocusOrFansActivity.this, dataList);
		listView.setAdapter(adapter);
	}

	public void onClickCallBack(View view) {
		switch (view.getId()) {
		case R.id.myfocus_fans_linear_back:
			finish();
			break;

		default:
			break;
		}
	}
	
	public void loadData(){
		getAttentionListByRid(AppContext.uid, offsete+"", "20");
	}
	
	void getAttentionListByRid(final String rid,final String offset,final String num) {
		startProgressDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("rid", rid);
		map.put("offset", offset);
		map.put("num", num);
		
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty(
						"getAttentionListByRid") + "&data=" + LBService.makeParam(map);
		DhNet net = new DhNet(url);
		Log.e("UrlString", "ulr = " + url);

		net.doGet(new NetTask(context) {
			@Override
			public void onErray(Response response) {
				super.onErray(response);
				// 错误处理,出错后会先关闭对话框然后调用这个方法,默认不处理
				Log.e("JoJo", "onErray");
			}

			@Override
			public void doInUI(Response response, Integer transfer) {
				String message = JSONUtil.getString(response.jSON(),
						"data.message");
				if ("1".equals(JSONUtil.getString(response.jSON(), "data.code"))) {
					List<AttentionBean> list = response.listFrom(AttentionBean.class, "data.item");
					if (list.size()>0) {
						offsete++;
					}
					dataList.addAll(list);
					adapter.setData(dataList);
					adapter.notifyDataSetChanged();
					
				}else{
					Toast.makeText(MyFocusOrFansActivity.this, message,
							Toast.LENGTH_SHORT).show();
				}
				stopProgressDialog();	
				myfocus_fans_list_body.onRefreshComplete();

			}
		});

	}
}
