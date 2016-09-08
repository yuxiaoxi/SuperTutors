package com.yuzhuo.easyupbring.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TextView;

import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.app.AppManager;
import com.yuzhuo.easyupbring.service.MessageReceiveService;


public class MainActivity extends TabActivity {

	public static TabHost tabHost;
	public static TextView main_tab_new_message;
	private RadioButton homeButton, selfButton,nearButton;
	private Button createButton;
	private PopupWindow popupWindow;
	private RelativeLayout main;
	private FrameLayout framButton;
	private Receiver receiver;
	int width,height;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_main);
		receiver = new Receiver();
		WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		width= wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
		AppManager.getAppManager().addActivity(this);
		initView();
	}

	public void initView() {
		framButton = (FrameLayout)findViewById(R.id.framButton);
		main = (RelativeLayout)findViewById(R.id.main);
		main_tab_new_message = (TextView) findViewById(R.id.main_tab_new_message);
		homeButton = (RadioButton) findViewById(R.id.rad_home);
		selfButton = (RadioButton) findViewById(R.id.rad_personal);
		nearButton = (RadioButton) findViewById(R.id.rad_near);
		tabHost = this.getTabHost();
		TabHost.TabSpec spec;
		Intent intent;
		intent = new Intent().setClass(this, HomeActivity.class);
		spec = tabHost.newTabSpec("home").setIndicator("home")
				.setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, MessageRecordAct.class);
		spec = tabHost.newTabSpec("near").setIndicator("near")
				.setContent(intent);
		tabHost.addTab(spec);
		intent = new Intent().setClass(this, SelfInfoActivity.class);
		spec = tabHost.newTabSpec("personal").setIndicator("personal")
				.setContent(intent);
		tabHost.addTab(spec);
		RadioGroup radioGroup = (RadioGroup) this.findViewById(R.id.main_tab_group);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				int gray = getResources().getColor(R.color.main_bottom_gray);
				int green = getResources().getColor(R.color.index_top_bg);
				switch (checkedId) {
				case R.id.rad_home:
					tabHost.setCurrentTabByTag("home");					
					homeButton.setTextColor(green);
					selfButton.setTextColor(gray);
					nearButton.setTextColor(gray);
					break;
				case R.id.rad_near:
					main_tab_new_message.setVisibility(View.GONE);
					tabHost.setCurrentTabByTag("near");
					homeButton.setTextColor(gray);
					selfButton.setTextColor(gray);
					nearButton.setTextColor(green);
					break;
				case R.id.rad_personal:
					tabHost.setCurrentTabByTag("personal");
					homeButton.setTextColor(gray);
					selfButton.setTextColor(green);
					nearButton.setTextColor(gray);
					break;
				}
			}
		});
	}


	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(AppContext.REFRESH_WARN_RECORD);
		filter.addAction(AppContext.MESSAGE_WARN_RECORD);
		filter.addAction(AppContext.REMOVE_WARN_RECORD);
		registerReceiver(receiver, filter);
	}
	
	class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (AppContext.REFRESH_WARN_RECORD.equals(action)) {
				main_tab_new_message.setVisibility(View.VISIBLE);
				if (MessageReceiveService.messagecounts > 0) {
					main_tab_new_message
							.setText(MessageReceiveService.messagecounts + "");
				}
				removeStickyBroadcast(intent);
			}
			
			if (AppContext.MESSAGE_WARN_RECORD.equals(action)) {
				main_tab_new_message.setText("");
				main_tab_new_message.setVisibility(View.VISIBLE);
				removeStickyBroadcast(intent);
			}
			if (AppContext.REMOVE_WARN_RECORD.equals(action)) {
				main_tab_new_message.setText("");
				main_tab_new_message.setVisibility(View.GONE);
				removeStickyBroadcast(intent);
			}
		}
	}

}
