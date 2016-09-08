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
import com.yuzhuo.easyupbring.adapter.ParntReleaseRecordAdapter;
import com.yuzhuo.easyupbring.app.AppContext;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyCollectOrReleaseActivity extends BaseAct {

	@InjectView(id = R.id.mycollect_release_record_linear_back, click = "onClickCallBack")
	private LinearLayout backLayout;
	@InjectView(id = R.id.mycollect_release_record_txt_title)
	private TextView titleTextView;
	@InjectView(id = R.id.mycollect_release_record_txt_name)
	private TextView titlenameTextView;

	@InjectView(id = R.id.mycollect_release_record_list_body)
	private PullToRefreshListView parent_release_record_list_body;
	private ListView listView;

	public static final int MODE_PULL_DOWN_TO_REFRESH = 0x1; // 下拉
	public static final int MODE_DEFAULT_LOAD = 0x2; // 上拉
	public boolean isRefresh = false;
	public int offsete;

	private ParntReleaseRecordAdapter adapter;
	private List<UpBringBean> dataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_mycollect_release);
		initView();
		loadData();
	}

	public void initView() {
		dataList = new ArrayList<UpBringBean>();

		parent_release_record_list_body
				.setOnRefreshListener(new OnRefreshListener() {

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

		listView = parent_release_record_list_body.getRefreshableView();
		listView.setDividerHeight(0);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> view, View arg1, int arg2,
					long position) {
				Intent intent = new Intent();

				intent.setClass(MyCollectOrReleaseActivity.this,
						ParentReleaseInfoActivity.class);
				intent.putExtra("upbid", dataList.get(arg2).getUpbid());
				intent.putExtra("uid", dataList.get(arg2).getRid());
				intent.putExtra("name", dataList.get(arg2).getNickname());
				startActivity(intent);
			}
		});
		adapter = new ParntReleaseRecordAdapter(
				MyCollectOrReleaseActivity.this, dataList);
		listView.setAdapter(adapter);
	}

	public void onClickCallBack(View view) {

		switch (view.getId()) {
		case R.id.mycollect_release_record_linear_back:
			finish();
			break;

		default:
			break;
		}
	}

	public void loadData() {
		if ("0".equals(this.getIntent().getStringExtra("type"))) {
			// 1为教师
			titlenameTextView.setText("收藏栏");
			titleTextView.setText("收藏过的招聘");
			getCollectListByRid(AppContext.uid, offsete + "", "20");
		} else if ("1".equals(this.getIntent().getStringExtra("type"))) {
			// 0为家长
			titlenameTextView.setText("发布栏");
			titleTextView.setText("发布过的招聘");
			getUpBringByUid(this.getIntent().getStringExtra("rid"), offsete
					+ "", "20");
		}

	}

	/**
	 * 
	 * @Title: getCollectListByRid
	 * @Description: 获取收藏列表
	 * @param @param rid
	 * @param @param offset
	 * @param @param num 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	void getCollectListByRid(final String rid, final String offset,
			final String num) {
		startProgressDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("rid", rid);
		map.put("offset", offset);
		map.put("num", num);

		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty(
						"getCollectListByRid")
				+ "&data="
				+ LBService.makeParam(map);
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

					List<UpBringBean> list = response.listFrom(
							UpBringBean.class, "data.item");
					if (list.size() > 0) {
						offsete++;
					}
					dataList.addAll(list);
					adapter.setData(dataList);
					adapter.notifyDataSetChanged();

				} else {
					Toast.makeText(MyCollectOrReleaseActivity.this, message,
							Toast.LENGTH_SHORT).show();
				}
				parent_release_record_list_body.onRefreshComplete();
				stopProgressDialog();

			}
		});

	}

	/**
	 * 
	 * @Title: getUpBringByUid
	 * @Description: 获取自己发布的招聘
	 * @param @param rid
	 * @param @param offset
	 * @param @param num 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	void getUpBringByUid(final String rid, final String offset, final String num) {
		startProgressDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("rid", rid);
		map.put("offset", offset);
		map.put("num", num);

		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty(
						"getUpBringByUid")
				+ "&data="
				+ LBService.makeParam(map);
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
					List<UpBringBean> list = response.listFrom(
							UpBringBean.class, "data.item");
					if (list.size() > 0) {
						offsete++;
					}
					dataList.addAll(list);
					adapter.setData(dataList);
					adapter.notifyDataSetChanged();

				} else {
					Toast.makeText(MyCollectOrReleaseActivity.this, message,
							Toast.LENGTH_SHORT).show();
				}

				parent_release_record_list_body.onRefreshComplete();
				stopProgressDialog();

			}
		});

	}
}
