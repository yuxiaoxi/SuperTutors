package com.yuzhuo.easyupbring.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.service.LBService;
import com.yuzhuo.easyupbring.utils.DateUtil;
import com.yuzhuo.easyupbring.utils.MyProperUtil;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ReleaseJobActivity extends BaseAct {

	@InjectView(id = R.id.release_job_linear_back, click = "onClickCallBack")
	private LinearLayout backLayout;
	@InjectView(id = R.id.release_job_txt_add, click = "onClickCallBack")
	private TextView addTextView;
	@InjectView(id = R.id.release_job_txt_reduction, click = "onClickCallBack")
	private TextView reductionTextView;
	@InjectView(id = R.id.get_job_txt_add, click = "onClickCallBack")
	private TextView addTextView1;
	@InjectView(id = R.id.get_job_txt_reduction, click = "onClickCallBack")
	private TextView reductionTextView2;
	@InjectView(id = R.id.release_job_txt_submit, click = "onClickCallBack")
	private TextView submiTextView;
	// 发布招聘信息
	@InjectView(id = R.id.get_job_edit_loctaion, click = "onClickCallBack")
	private EditText locationTextView;
	@InjectView(id = R.id.get_job_edit_course)
	private EditText courseEditText;
	@InjectView(id = R.id.get_job_edit_grade)
	private EditText gradeEditText;
	@InjectView(id = R.id.get_job_edit_other)
	private EditText otherEditText;
	@InjectView(id = R.id.get_job_edit_salary)
	private EditText salaryEditText;
	// 发布求职信息
	@InjectView(id = R.id.release_job_edit_location)
	private EditText locationEditText;
	@InjectView(id = R.id.release_job_edit_school)
	private EditText schoolEditText;
	@InjectView(id = R.id.release_job_edit_course)
	private EditText courseEditText2;
	@InjectView(id = R.id.release_job_edit_grade)
	private EditText gradeEditText2;
	@InjectView(id = R.id.release_job_edit_professional)
	private EditText professionalEditText;
	@InjectView(id = R.id.release_job_edit_salary)
	private EditText salaryEditText2;
	@InjectView(id = R.id.release_job_edit_other)
	private EditText otherEditText2;
	Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if ("0".equals(AppContext.CHARACTER)) {
			setContentView(R.layout.act_get_job);
			locationTextView.setText(AppContext.address);
		} else {
			setContentView(R.layout.act_release_job);
			locationEditText.setText(AppContext.address);
		}

	}

	public void onClickCallBack(View view) {
		switch (view.getId()) {
		case R.id.release_job_linear_back:
			finish();
			break;

		case R.id.release_job_txt_submit:

			if ("0".equals(AppContext.CHARACTER)) {
				addUpBringInfo(AppContext.username, AppContext.uid, "",
						gradeEditText.getText().toString().trim(),
						AppContext.USERNAME, salaryEditText.getText()
								.toString().trim()
								+ "元/小时", courseEditText.getText().toString()
								.trim(), AppContext.city, AppContext.AVATAR,
						otherEditText.getText().toString().trim(), "",
						locationTextView.getText().toString().trim(),
						AppContext.latitude, AppContext.longtude);

			} else {
				addUpBringInfo(AppContext.username, AppContext.uid,
						schoolEditText.getText().toString().trim(),
						gradeEditText2.getText().toString().trim(),
						AppContext.USERNAME, salaryEditText2.getText()
								.toString().trim()
								+ "元/小时", courseEditText2.getText().toString()
								.trim(), AppContext.city, AppContext.AVATAR,
						otherEditText2.getText().toString().trim(),
						professionalEditText.getText().toString().trim(),
						locationEditText.getText().toString().trim(),
						AppContext.latitude, AppContext.longtude);
			}
			break;
		case R.id.release_job_txt_add:
			setEditText(salaryEditText2, 1);
			break;

		case R.id.release_job_txt_reduction:
			setEditText(salaryEditText2, 0);
			break;

		case R.id.get_job_txt_add:
			setEditText(salaryEditText, 1);
			break;

		case R.id.get_job_txt_reduction:
			setEditText(salaryEditText, 0);
			break;

		case R.id.get_job_edit_loctaion:

			break;
		default:
			break;
		}
	}

	public void setEditText(EditText editText, int type) {
		int n = Integer.parseInt(editText.getText().toString().trim());
		if (type == 0) {
			if (n >= 1) {
				n = n - 1;
			} else {
				Toast.makeText(ReleaseJobActivity.this, "亲，不能再减了！",
						Toast.LENGTH_SHORT).show();
			}
		} else if (type == 1) {
			n = n + 1;
		}

		editText.setText(n + "");
	}

	/**
	 * 发布家教信息
	 * @param uid
	 * @param rid
	 * @param school
	 * @param grade
	 * @param nickname
	 * @param salary
	 * @param course
	 * @param area
	 * @param avatar
	 * @param other
	 * @param professional
	 * @param addr
	 * @param latitude
	 * @param longitude
	 */
	void addUpBringInfo(final String uid, final String rid,
			final String school, final String grade, final String nickname,
			final String salary, final String course, final String area,
			final String avatar, final String other, final String professional,
			final String addr, final String latitude, final String longitude) {
		startProgressDialog();
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty(
						"addUpBringInfo")
				+ "&data="
				+ makeParameters(uid, rid, school, grade, nickname, salary,
						course, area, avatar, other, professional, addr,
						latitude, longitude);
		DhNet net = new DhNet(url);
		Log.e("UrlString", "ulr = " + url);

		net.doGet(new NetTask(this) {
			@Override
			public void onErray(Response response) {
				super.onErray(response);
				// 错误处理,出错后会先关闭对话框然后调用这个方法,默认不处理
				Log.e("JoJo", "onErray");
			}

			@Override
			public void doInUI(Response response, Integer transfer) {
				Log.e("restring", response.plain());
				String message = JSONUtil.getString(response.jSON(),
						"data.message");
				if ("1".equals(JSONUtil.getString(response.jSON(), "data.code"))) {
					finish();
				}
				Toast.makeText(ReleaseJobActivity.this, message,
						Toast.LENGTH_SHORT).show();
				stopProgressDialog();

			}
		});

	}

	private String makeParameters(String uid, String rid, String school,
			String grade, String nickname, String salary, String course,
			String area, String avatar, String other, String professional,
			String addr, String latitude, String longitude) {
		String parameters = "";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uid", uid);
		map.put("rid", rid);
		map.put("school", school);
		map.put("grade", grade);
		map.put("nickname", nickname);
		map.put("salary", salary);
		map.put("course", course);
		map.put("area", area);
		map.put("avatar", avatar);
		map.put("other", other);
		map.put("professional", professional);
		map.put("characters", AppContext.CHARACTER);
		map.put("addr", addr);
		map.put("latitude", latitude);
		map.put("longitude", longitude);
		try {
			parameters = new String(Base64.encode(gson.toJson(map).getBytes(),
					Base64.NO_WRAP));
			return URLEncoder.encode(parameters, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
}
