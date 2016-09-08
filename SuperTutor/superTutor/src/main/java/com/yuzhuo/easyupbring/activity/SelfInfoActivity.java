package com.yuzhuo.easyupbring.activity;

import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.app.AppManager;
import com.yuzhuo.easyupbring.app.MessageManager;
import com.yuzhuo.easyupbring.service.CusPerference;
import com.yuzhuo.easyupbring.utils.ImageLoader;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelfInfoActivity extends BaseAct {

	// @InjectView(id = R.id.self_info_btn_exit, click = "onClickCallBack")
	// private TextView exiTextView;
	@InjectView(id = R.id.selfinfo_linear_myfocus, click = "onClickCallBack")
	private LinearLayout selfinfo_linear_myfocus;
	@InjectView(id = R.id.selfinfo_linear_myrelease, click = "onClickCallBack")
	private LinearLayout selfinfo_linear_myrelease;
	@InjectView(id = R.id.selfinfo_linear_selfinfo_detail, click = "onClickCallBack")
	private LinearLayout selfinfo_linear_selfinfo_detail;
	@InjectView(id = R.id.selfinfo_linear_selfinfo_advice, click = "onClickCallBack")
	private LinearLayout selfinfo_linear_selfinfo_advice;
	@InjectView(id = R.id.selfinfo_linear_selfinfo_aboutus, click = "onClickCallBack")
	private LinearLayout selfinfo_linear_selfinfo_aboutus;
	@InjectView(id = R.id.selfinfo_linear_selfinfo_tocomment, click = "onClickCallBack")
	private LinearLayout tocommentLayout;
	@InjectView(id = R.id.selfinfo_img_avatar)
	private ImageView avatarImageView;
	@InjectView(id = R.id.selfinfo_txt_username)
	private TextView usernameTextView;
	@InjectView(id = R.id.selfinfo_txt_titelName)
	private TextView selfinfo_txt_titelName;
	@InjectView(id = R.id.selfinfo_txt_focus)
	private TextView selfinfo_txt_focus;
	@InjectView(id = R.id.release_num)
	private TextView releaseNumTextView;
	@InjectView(id = R.id.collect_num)
	private TextView collectNumTextView;
	@Inject
	CusPerference cusPerference;
	ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_selfinfo);
		cusPerference.load();
		imageLoader = ImageLoader.getIncetence(this);
		initView();
	}

	public void initView() {
		imageLoader.displayImage(AppContext.userInfoBean.getAvatar(),
				avatarImageView);
		usernameTextView.setText(AppContext.userInfoBean.getUsername());
		if ("1".equals(AppContext.CHARACTER)) {
			selfinfo_txt_titelName.setText("收藏的招聘");
			releaseNumTextView.setText(AppContext.userInfoBean.getCollectnum()
					+ "条");
			collectNumTextView.setText(AppContext.userInfoBean.getReleasenum()
					+ "条");
			selfinfo_txt_focus.setText("发布的求职");
		} else {
			// releaseNumTextView.setText(AppContext.userInfoBean.get);
			collectNumTextView.setText(AppContext.userInfoBean
					.getAttentionnum() + "个");
			selfinfo_txt_titelName.setText("发布的招聘");
			releaseNumTextView.setText(AppContext.userInfoBean.getReleasenum()
					+ "条");
			selfinfo_txt_focus.setText("关注的老师");
		}
	}

	public void onClickCallBack(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {

		case R.id.selfinfo_linear_myfocus:
			if ("1".equals(AppContext.CHARACTER)) {
				// 1为教师
				intent.setClass(this, MyCollectOrReleaseActivity.class);
				intent.putExtra("type", "1");
				intent.putExtra("rid", AppContext.uid);
			} else if ("0".equals(AppContext.CHARACTER)) {
				intent.setClass(this, MyFocusOrFansActivity.class);
			}
			startActivity(intent);
			break;

		case R.id.selfinfo_linear_myrelease:
			intent.setClass(this, MyCollectOrReleaseActivity.class);
			if ("1".equals(AppContext.CHARACTER)) {

				intent.putExtra("type", "0");
			} else {
				intent.putExtra("type", "1");
				intent.putExtra("rid", AppContext.uid);
			}
			startActivity(intent);
			break;
		case R.id.selfinfo_linear_selfinfo_detail:
			intent.setClass(this, SelfInfoDetailActivity.class);
			startActivity(intent);
			break;
		case R.id.selfinfo_linear_selfinfo_aboutus:
			intent.setClass(this, SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.selfinfo_linear_selfinfo_advice:
			intent.setClass(this, AdviceActivity.class);
			startActivity(intent);
			break;

		case R.id.selfinfo_linear_selfinfo_tocomment:
			Uri uri = Uri.parse("market://details?id=" + getPackageName());
			Intent mintent = new Intent(Intent.ACTION_VIEW, uri);
			mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(mintent);
			break;

		default:
			break;
		}
	}
}
