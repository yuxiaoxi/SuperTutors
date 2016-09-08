package com.yuzhuo.easyupbring.activity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;

import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.bean.UpBringBean;
import com.yuzhuo.easyupbring.service.LBService;
import com.yuzhuo.easyupbring.utils.DateUtil;
import com.yuzhuo.easyupbring.utils.ImageLoader;
import com.yuzhuo.easyupbring.utils.MyProperUtil;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ParentReleaseInfoActivity extends BaseAct {

	@InjectView(id = R.id.parent_release_info_linear_back, click = "onClickCallBack")
	private LinearLayout backLayout;
	@InjectView(id = R.id.parent_release_record_linear_torecord, click = "onClickCallBack")
	private LinearLayout parent_release_record_linear_torecord;
	@InjectView(id = R.id.parent_release_record_img_imgAvatar)
	private ImageView avatarImageView;
	@InjectView(id = R.id.parent_release_record_txt_nickname)
	private TextView nicknameTextView;
	@InjectView(id = R.id.parent_release_record_txt_distance)
	private TextView distanceTextView;
	@InjectView(id = R.id.parent_release_record_txt_createtime)
	private TextView createtimeTextView;
	@InjectView(id = R.id.parent_release_record_txt_location)
	private TextView locationTextView;
	@InjectView(id = R.id.parent_release_record_txt_level)
	private TextView levelTextView;
	@InjectView(id = R.id.parent_release_record_txt_course)
	private TextView courseTextView;
	@InjectView(id = R.id.parent_release_record_txt_salary)
	private TextView salaryTextView;
	@InjectView(id = R.id.parent_release_record_txt_other)
	private TextView otherTextView;
	@InjectView(id = R.id.parent_release_record_txt_status)
	private TextView statusTextView;
	@InjectView(id = R.id.parent_release_record_linear_message, click = "onClickCallBack")
	private LinearLayout tomessageLayout;
	@InjectView(id = R.id.parent_release_record_linear_call, click = "onClickCallBack")
	private LinearLayout callLayout;
	@InjectView(id = R.id.parent_release_record_linear_collect, click = "onClickCallBack")
	private LinearLayout collectLayout;
	
	@InjectView(id = R.id.parent_release_bottom_linear)
	private LinearLayout bottomLayout;

	private ImageLoader imageLoader;
	private UpBringBean upBringBean;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_parent_release_info);
		imageLoader = ImageLoader.getIncetence(this);
		getUpBringByUpbid(getIntent().getStringExtra("upbid"));
		initView();
	}
	
	public void initView(){
		if(AppContext.uid.equals(this.getIntent().getStringExtra("uid"))){
			parent_release_record_linear_torecord.setVisibility(View.GONE);
			bottomLayout.setVisibility(View.GONE);
		}
	}

	public void onClickCallBack(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {
		case R.id.parent_release_info_linear_back:
			finish();
			break;
		case R.id.parent_release_record_linear_torecord:
			intent.setClass(this, ParentReleaseRecordActivity.class);
			intent.putExtra("rid", upBringBean.getRid());
			intent.putExtra("name", upBringBean.getNickname());
			intent.putExtra("avatar", upBringBean.getAvatar());
			startActivity(intent);
			break;

		case R.id.parent_release_record_linear_call:

			break;

		case R.id.parent_release_record_linear_collect:
			if (upBringBean!=null) {
				createCollect(upBringBean);
			}
			break;

		case R.id.parent_release_record_linear_message:
			intent.setClass(this, SingleChatAct.class);
			intent.putExtra("fid", "single@"
					+ getIntent().getStringExtra("uid"));
			intent.putExtra("name", getIntent().getStringExtra("name"));
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	/**
	 * 
	* @Title: createCollect 
	* @Description: 收藏发布记录
	* @param @param rid
	* @param @param upbid
	* @param @param grade
	* @param @param nickname
	* @param @param addr
	* @param @param course
	* @param @param salary
	* @param @param other
	* @param @param status
	* @param @param latitude
	* @param @param longitude    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	void createCollect(final UpBringBean upBringBean) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("rid", upBringBean.getRid());
		map.put("mrid", AppContext.uid);
		map.put("upbid", upBringBean.getUpbid());
		map.put("grade", upBringBean.getGrade());
		map.put("nickname", upBringBean.getNickname());
		map.put("addr", upBringBean.getAddr());
		map.put("course", upBringBean.getCourse());
		map.put("salary", upBringBean.getSalary());
		map.put("other", upBringBean.getOther());
		map.put("status", upBringBean.getStatus());
		map.put("latitude", upBringBean.getLatitude());
		map.put("longitude", upBringBean.getLongitude());
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty(
						"createCollect") + "&data=" + LBService.makeParam(map);
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

				}
				Toast.makeText(ParentReleaseInfoActivity.this, message,
						Toast.LENGTH_SHORT).show();

			}
		});

	}

	/**
	 * 
	* @Title: getUpBringByUpbid 
	* @Description: 获取单条发布记录详情 
	* @param @param upbid    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	void getUpBringByUpbid(final String upbid) {
		startProgressDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("upbid", upbid);
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty(
						"getUpBringByUpbid")
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
				stopProgressDialog();
				if ("1".equals(JSONUtil.getString(response.jSON(), "data.code"))) {
					upBringBean = response.modelFrom(UpBringBean.class, "data.item");
					nicknameTextView.setText(JSONUtil.getString(
							response.jSON(), "data.item.nickname"));
					String status = JSONUtil.getString(response.jSON(),
							"data.item.status");
					if ("0".equals(status)) {
						statusTextView.setText("未结束");
					} else if ("1".equals(status)) {
						statusTextView.setText("已结束");
					} else if ("2".equals(status)) {
						statusTextView.setText("已过期");
					}
					locationTextView.setText(JSONUtil.getString(
							response.jSON(), "data.item.addr"));
					createtimeTextView.setText(DateUtil.getLeaveNow(Long
							.parseLong(JSONUtil.getString(response.jSON(),
									"data.item.createtime")), new Date()
							.getTime() / 1000));
					levelTextView.setText(JSONUtil.getString(response.jSON(),
							"data.item.grade"));
					courseTextView.setText(JSONUtil.getString(response.jSON(),
							"data.item.course"));
					salaryTextView.setText(JSONUtil.getString(response.jSON(),
							"data.item.salary"));
					otherTextView.setText(JSONUtil.getString(response.jSON(),
							"data.item.other"));
					imageLoader.displayImage(JSONUtil.getString(
							response.jSON(), "data.item.avatar"),
							avatarImageView);
				} else {
					Toast.makeText(ParentReleaseInfoActivity.this, message,
							Toast.LENGTH_SHORT).show();
				}

			}
		});

	}
}
