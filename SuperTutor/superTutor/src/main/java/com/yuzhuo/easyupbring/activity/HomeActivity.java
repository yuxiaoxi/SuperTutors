package com.yuzhuo.easyupbring.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.view.PullToRefreshListView;
import net.duohuo.dhroid.view.PullToRefreshBase.OnRefreshListener;

import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.adapter.UpListAdapter;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.bean.UpBringBean;
import com.yuzhuo.easyupbring.service.CusPerference;
import com.yuzhuo.easyupbring.service.LBService;
import com.yuzhuo.easyupbring.utils.DateUtil;
import com.yuzhuo.easyupbring.utils.ImageLoader;
import com.yuzhuo.easyupbring.utils.MyProperUtil;
import com.yuzhuo.easyupbring.widget.ViewHolder;

import android.R.id;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @ClassName: HomeActivity
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 余卓
 * @date 2014年12月25日 上午10:59:04
 * 
 */
public class HomeActivity extends BaseAct {

	@InjectView(id = R.id.easyup_list_home)
	private PullToRefreshListView pullToRefreshListView;
	@InjectView(id = R.id.home_img_add, click = "onClickCallBack")
	private ImageView home_img_add;
	@InjectView(id = R.id.home_edit_search)
	private EditText searchEditText;
	@InjectView(id = R.id.home_push_parent_linear, click = "onClickCallBack")
	private LinearLayout parentLayout;
	@InjectView(id = R.id.home_push_teacher_linear, click = "onClickCallBack")
	private LinearLayout teacherLayout;
	@InjectView(id = R.id.home_push_touser_txt, click = "onClickCallBack")
	private TextView pushTextView;
	// 推荐家长
	@InjectView(id = R.id.home_img_listitem_avatar)
	private ImageView parentImageView;
	@InjectView(id = R.id.home_txt_listitem_nickname)
	private TextView pnicknameTextView;
	@InjectView(id = R.id.home_txt_listitem_course)
	private TextView pcourseTextView;
	@InjectView(id = R.id.home_txt_listitem_salary)
	private TextView psalaryTextView;
	@InjectView(id = R.id.home_txt_listitem_level)
	private TextView plevelTextView;
	@InjectView(id = R.id.home_txt_listitem_status)
	private TextView pstatusTextView;
	@InjectView(id = R.id.home_txt_listitem_createtime)
	private TextView pcreatetimeTextView;
	@InjectView(id = R.id.home_txt_listitem_distance)
	private TextView pdistanceTextView;
	// 推荐老师
	@InjectView(id = R.id.home_teacher_img_listitem_avatar)
	private ImageView tarentImageView;
	@InjectView(id = R.id.home_teacher_txt_listitem_nickname)
	private TextView tnicknameTextView;
	@InjectView(id = R.id.home_teacher_txt_listitem_course)
	private TextView tcourseTextView;
	@InjectView(id = R.id.home_teacher_txt_listitem_salary)
	private TextView tsalaryTextView;
	@InjectView(id = R.id.home_teacher_txt_listitem_level)
	private TextView tlevelTextView;
	@InjectView(id = R.id.home_teacher_txt_listitem_professiona)
	private TextView tprofessionalTextView;
	@InjectView(id = R.id.home_teacher_txt_listitem_university)
	private TextView tschoolTextView;
	@InjectView(id = R.id.home_teacher_txt_listitem_createtime)
	private TextView tcreatetimeTextView;
	@InjectView(id = R.id.home_teacher_txt_listitem_distance)
	private TextView tdistanceTextView;

	private ListView listView;
	public static final int MODE_PULL_DOWN_TO_REFRESH = 0x1; // 下拉
	public static final int MODE_DEFAULT_LOAD = 0x2; // 上拉
	public boolean isRefresh = false;
	public int offsete;
	private UpListAdapter adapter;
	private List<UpBringBean> dataList;
	private UpBringBean pushBringBean;
	private boolean ispush = true;
	private ImageLoader imageLoader;
	@Inject
	CusPerference perference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_home);
		perference.load();
		imageLoader = ImageLoader.getIncetence(this);
		initView();
		loadData();
	}

	public void initView() {
		if ("0".equals(AppContext.CHARACTER)) {
			parentLayout.setVisibility(View.GONE);
			teacherLayout.setVisibility(View.VISIBLE);
		} else {
			parentLayout.setVisibility(View.VISIBLE);
			teacherLayout.setVisibility(View.GONE);
		}

		dataList = new ArrayList<UpBringBean>();
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(int pullDirection) {
				switch (pullDirection) {
				// 1下拉,2上拉
				case MODE_PULL_DOWN_TO_REFRESH:
					isRefresh = true;
					offsete = 0;
					loadData();
					break;
				case MODE_DEFAULT_LOAD:
					loadData();
					break;
				}
			}
		});

		listView = pullToRefreshListView.getRefreshableView();
		listView.setDividerHeight(0);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> view, View arg1, int arg2,
					long position) {
				Intent intent = new Intent();
				intent.putExtra("upbid", dataList.get(arg2).getUpbid());
				intent.putExtra("uid", dataList.get(arg2).getRid());
				intent.putExtra("name", dataList.get(arg2).getNickname());
				if ("1".equals(AppContext.CHARACTER)) {
					intent.setClass(HomeActivity.this,
							ParentReleaseInfoActivity.class);
				} else {
					intent.setClass(HomeActivity.this,
							UserInfoDetailActivity.class);
				}
				startActivity(intent);
			}
		});
		adapter = new UpListAdapter(HomeActivity.this, dataList);
		listView.setAdapter(adapter);

		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {

					// 先隐藏键盘
					((InputMethodManager) searchEditText.getContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(
									v.getApplicationWindowToken(), 0);
					// 触发点击搜索事件代码
					offsete = 0;
					if (perference.city.endsWith("市")) {
						getUpBringListBySearch(perference.city, searchEditText
								.getText().toString().trim(), getCharacter(),
								offsete + "", "10");
					} else {
						getUpBringListBySearch(perference.city + "市",
								searchEditText.getText().toString().trim(),
								getCharacter(), offsete + "", "10");
					}
				}
				return false;
			}
		});
	}

	public void initPushView() {
		//
		if ("1".equals(AppContext.CHARACTER)) {
			// 推荐家长
			pnicknameTextView.setText(pushBringBean.getNickname());
			psalaryTextView.setText(pushBringBean.getSalary());
			plevelTextView.setText(pushBringBean.getGrade());
			pcourseTextView.setText(pushBringBean.getCourse());
			pcreatetimeTextView.setText(DateUtil.getLeaveNow(
					Long.parseLong(pushBringBean.getCreatetime()),
					new Date().getTime() / 1000));
			if ("0".equals(pushBringBean.getStatus())) {
				pstatusTextView.setText("未结束");
			} else if ("1".equals(pushBringBean.getStatus())) {
				pstatusTextView.setText("已结束");
			} else if ("2".equals(pushBringBean.getStatus())) {
				pstatusTextView.setText("已过期");
			}
			pdistanceTextView.setText("2.12km");
			imageLoader
					.displayImage(pushBringBean.getAvatar(), parentImageView);
		} else {
			tnicknameTextView.setText(pushBringBean.getNickname());
			tsalaryTextView.setText(pushBringBean.getSalary());
			tlevelTextView.setText(pushBringBean.getGrade());
			tprofessionalTextView.setText(pushBringBean.getProfessional());
			tcourseTextView.setText(pushBringBean.getCourse());
			tschoolTextView.setText(pushBringBean.getSchool());
			tcreatetimeTextView.setText(DateUtil.getLeaveNow(
					Long.parseLong(pushBringBean.getCreatetime()),
					new Date().getTime() / 1000));
			tdistanceTextView.setText("2.12km");
			imageLoader
					.displayImage(pushBringBean.getAvatar(), tarentImageView);
		}
	}

	public void onClickCallBack(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {
		case R.id.home_img_add:
			intent.setClass(this, ReleaseJobActivity.class);
			startActivity(intent);
			break;

		case R.id.home_push_touser_txt:
			Toast.makeText(context, "数据还在继续完善中...", Toast.LENGTH_SHORT).show();
			break;

		case R.id.home_push_parent_linear:
			intent.setClass(HomeActivity.this, ParentReleaseInfoActivity.class);
			intent.putExtra("upbid", pushBringBean.getUpbid());
			intent.putExtra("uid", pushBringBean.getRid());
			intent.putExtra("name", pushBringBean.getNickname());
			startActivity(intent);
			break;
		case R.id.home_push_teacher_linear:

			intent.setClass(HomeActivity.this, UserInfoDetailActivity.class);

			intent.putExtra("upbid", pushBringBean.getUpbid());
			intent.putExtra("uid", pushBringBean.getRid());
			intent.putExtra("name", pushBringBean.getNickname());
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	public void loadData() {
		Log.e("city----->", perference.city);
		if (perference.city.endsWith("市")) {
			getUpBringList(perference.city, "", "", getCharacter(), offsete
					+ "", "10");
		} else {
			getUpBringList(perference.city + "市", "", "", getCharacter(),
					offsete + "", "10");
		}
		Log.e("offset", "" + offsete);

	}

	public String getCharacter() {
		if ("0".equals(AppContext.CHARACTER)) {
			return "1";
		} else {
			return "0";
		}
	}

	/**
	 * 获取家教信息列表方法
	 * @param area
	 * @param level
	 * @param course
	 * @param character
	 * @param offset
	 * @param num
	 */
	void getUpBringList(final String area, final String level,
			final String course, final String character, final String offset,
			final String num) {
		startProgressDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("area", area);//地址，可以为空
		map.put("course", course);//课程
		map.put("grade", level);//年级
		map.put("level", level);//学历
		map.put("characters", character);//角色
		map.put("offset", offset);//页数
		map.put("num", num);//每页条数
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty(
						"getUpBringList") + "&data=" + LBService.makeParam(map);
		DhNet net = new DhNet(url);
		Log.e("UrlString", "ulr = " + url);

		net.doGet(new NetTask(context) {
			@Override
			public void onErray(Response response) {
				super.onErray(response);
				// 错误处理,出错后会先关闭对话框然后调用这个方法,默认不处理
				Log.e("YZ", "onErray");
			}

			@Override
			public void doInUI(Response response, Integer transfer) {
				String message = JSONUtil.getString(response.jSON(),
						"data.message");
				Log.e("dataList", response.plain());
				if ("1".equals(JSONUtil.getString(response.jSON(), "data.code"))) {
					List<UpBringBean> list = response.listFrom(
							UpBringBean.class, "data.item");
					if (list.size() > 0) {
						offsete++;
					}
					if ("0".equals(offset)) {
						dataList.clear();
					}
					dataList.addAll(list);
					if (ispush) {
						pushBringBean = dataList.get(0);
						initPushView();
					}
					adapter.setData(dataList);
					adapter.notifyDataSetChanged();
				} else {
					Toast.makeText(HomeActivity.this, message,
							Toast.LENGTH_SHORT).show();
				}
				pullToRefreshListView.onRefreshComplete();
				stopProgressDialog();

			}
		});

	}

	/**
	 * 关键接检索方法
	 * @param area
	 * @param key
	 * @param character
	 * @param offset
	 * @param num
	 */
	void getUpBringListBySearch(final String area, final String key,
			final String character, final String offset, final String num) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("area", area);//城市
		map.put("key", key);//关键字
		map.put("characters", character);//角色
		map.put("offset", offset);//页数
		map.put("num", num);//每页条数
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty(
						"getUpBringListBySearch")
				+ "&data="
				+ LBService.makeParam(map);
		DhNet net = new DhNet(url);
		Log.e("UrlString", "ulr = " + url);

		net.doGet(new NetTask(context) {
			@Override
			public void onErray(Response response) {
				super.onErray(response);
				// 错误处理,出错后会先关闭对话框然后调用这个方法,默认不处理
				Log.e("YZ", "onErray");
			}

			@Override
			public void doInUI(Response response, Integer transfer) {
				String message = JSONUtil.getString(response.jSON(),
						"data.message");
				Log.e("dataList", response.plain());
				if ("1".equals(JSONUtil.getString(response.jSON(), "data.code"))) {
					List<UpBringBean> list = response.listFrom(
							UpBringBean.class, "data.item");
					if (list.size() > 0) {
						offsete++;
					}
					if ("0".equals(offset)) {
						dataList.clear();
					}
					dataList.addAll(list);
					adapter.setData(dataList);
					adapter.notifyDataSetChanged();
				} else {
					Toast.makeText(HomeActivity.this, message,
							Toast.LENGTH_SHORT).show();
				}
				pullToRefreshListView.onRefreshComplete();

			}
		});

	}
}
