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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoDetailActivity extends BaseAct {

	@InjectView(id =R.id.selfinfo_detail_linear_back,click="onClickCallBack")
	private LinearLayout backLayout;
	@InjectView(id = R.id.userinfo_detail_img_avatar)
	private ImageView avatarImageView;
	@InjectView(id =R.id.userinfo_detail_txt_nickname)
	private TextView nicknameTextView;
	@InjectView(id = R.id.userinfo_detail_txt_listitem_distance)
	private TextView distanceTextView;
	@InjectView(id =R.id.userinfo_detail_txt_listitem_createtime)
	private TextView createtimeTextView;
	@InjectView(id = R.id.userinfo_detail_txt_listitem_location)
	private TextView locationTextView;
	@InjectView(id = R.id.userinfo_detail_txt_listitem_school)
	private TextView schoolTextView;
	@InjectView(id = R.id.userinfo_detail_txt_listitem_professional)
	private TextView professionalTextView;
	@InjectView(id = R.id.userinfo_detail_txt_listitem_level)
	private TextView levelTextView;
	@InjectView(id = R.id.userinfo_detail_txt_listitem_course)
	private TextView courseTextView;
	@InjectView(id = R.id.userinfo_detail_txt_listitem_salary)
	private TextView salaryTextView;
	@InjectView(id = R.id.userinfo_detail_txt_listitem_other)
	private TextView otherTextView;
	@InjectView(id = R.id.userinfo_detail_linear_listitem_tomessage,click = "onClickCallBack")
	private LinearLayout tomessageLayout;
	@InjectView(id = R.id.userinfo_detail_linear_listitem_tofocus,click = "onClickCallBack")
	private LinearLayout tofocusLayout;
	@InjectView(id = R.id.userinfo_detail_linear_listitem_tocall,click="onClickCallBack")
	private LinearLayout tocallLayout;

	private ImageLoader imageLoader;
	private UpBringBean upBringBean;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_userinfo_detail);
		imageLoader = ImageLoader.getIncetence(this);
		getUpBringByUpbid(getIntent().getStringExtra("upbid"));
		initView();
	}
	
	void initView(){
//		selfinfo_detail_img_avatar.setLayoutParams(new LinearLayout.LayoutParams(height,height));
	}
	
	public void onClickCallBack(View view){
		Intent intent = new Intent();
		switch (view.getId()) {
		case R.id.selfinfo_detail_linear_back:
			finish();
			break;
		case R.id.userinfo_detail_linear_listitem_tomessage:
			intent.setClass(this, SingleChatAct.class);
			intent.putExtra("fid", "single@"+getIntent().getStringExtra("uid"));
			intent.putExtra("name", getIntent().getStringExtra("name"));
			startActivity(intent);
			break;
			
		case R.id.userinfo_detail_linear_listitem_tofocus:
			if (upBringBean!=null) {
				createAttention(upBringBean);
			}
			break;
			
		case R.id.userinfo_detail_linear_listitem_tocall:
			
			break;
			
		default:
			break;
		}
	}
	
	/**
	 * 
	* @Title: createAttention 
	* @Description: 关注
	* @param @param upBringBean    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	void createAttention(final UpBringBean upBringBean) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("mrid", AppContext.uid);
		map.put("frid", upBringBean.getRid());
		map.put("favatar", upBringBean.getAvatar());
		map.put("fnickname", upBringBean.getNickname());
		map.put("fsalary", upBringBean.getSalary());
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty(
						"createAttention") + "&data=" + LBService.makeParam(map);
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
				Log.e("rsstring", response.plain());
				if ("1".equals(JSONUtil.getString(response.jSON(), "data.code"))) {
					
				}
				Toast.makeText(UserInfoDetailActivity.this, message,
						Toast.LENGTH_SHORT).show();

			}
		});

	}
	
	/**
	 * 
	* @Title: getUpBringByUpbid 
	* @Description: 获取发布记录详情
	* @param @param upbid    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	void getUpBringByUpbid(final String upbid) {
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
				if ("1".equals(JSONUtil.getString(response.jSON(), "data.code"))) {
					upBringBean = response.modelFrom(UpBringBean.class, "data.item");
					nicknameTextView.setText(JSONUtil.getString(
							response.jSON(), "data.item.nickname"));
					locationTextView.setText(JSONUtil.getString(
							response.jSON(), "data.item.addr"));
					createtimeTextView.setText(DateUtil.getLeaveNow(Long
							.parseLong(JSONUtil.getString(response.jSON(),
									"data.item.createtime")), new Date()
							.getTime() / 1000));
					schoolTextView.setText(JSONUtil.getString(
							response.jSON(), "data.item.school"));
					levelTextView.setText(JSONUtil.getString(response.jSON(),
									"data.item.grade"));
					courseTextView.setText(JSONUtil.getString(response.jSON(),
									"data.item.course"));
					salaryTextView.setText(JSONUtil.getString(response.jSON(),
									"data.item.salary"));
					professionalTextView.setText(JSONUtil.getString(response.jSON(), "data.item.professional"));
					otherTextView.setText(JSONUtil.getString(response.jSON(), "data.item.other"));
					imageLoader.displayImage(JSONUtil.getString(response.jSON(),
									"data.item.avatar"), avatarImageView);
				} else {
					Toast.makeText(UserInfoDetailActivity.this, message,
							Toast.LENGTH_SHORT).show();
				}

			}
		});

	}
}
