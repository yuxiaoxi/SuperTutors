package com.yuzhuo.easyupbring.activity;

import java.util.HashMap;
import java.util.Map;

import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.service.CusPerference;
import com.yuzhuo.easyupbring.utils.MyProperUtil;

public class ResetPswActivity extends BaseAct {

	@InjectView(id = R.id.reset_edite_new_psw)
	private EditText reset_edite_new_psw;
	@InjectView(id = R.id.reset_edite_new_repsw)
	private EditText reset_edite_new_repsw;
	@InjectView(id = R.id.reset_edite_old_psw)
	private TextView oldTextView;
	@InjectView(id = R.id.reset_btn_finish, click = "onClickCallBack")
	private TextView reset_btn_finish;

	@InjectView(id = R.id.reset_psw_linear_back, click = "onClickCallBack")
	private LinearLayout backLayout;
	@Inject
	CusPerference cusPerference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.act_reset_psw);
		cusPerference.load();
	}

	public void onClickCallBack(View view) {
		switch (view.getId()) {
		case R.id.reset_psw_linear_back:
			finish();
			break;

		case R.id.reset_btn_finish:
			if (isEmpty(reset_edite_new_psw.getText().toString().trim())
					|| isEmpty(reset_edite_new_repsw.getText().toString()
							.trim())
					|| isEmpty(oldTextView.getText().toString().trim())) {
				Toast.makeText(ResetPswActivity.this, "新密码不能为空！",
						Toast.LENGTH_SHORT).show();
			} else {
				if (reset_edite_new_psw
						.getText()
						.toString()
						.trim()
						.equals(reset_edite_new_repsw.getText().toString()
								.trim())) {
					startProgressDialog();
					modifyUserPasswd(AppContext.username, oldTextView
							.getText().toString().trim(), reset_edite_new_psw
							.getText().toString().trim());
				} else {
					reset_edite_new_psw.setText("");
					reset_edite_new_repsw.setText("");
					Toast.makeText(ResetPswActivity.this, "两次输入的新密码不一致，请重新输入！",
							Toast.LENGTH_SHORT).show();
				}

			}
			break;

		default:
			break;
		}
	}

	/**
	 * 密码修改
	 * 
	 * @Title: modifyUserPasswd
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param uuid
	 * @param @param oldpwd
	 * @param @param newpwd 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void modifyUserPasswd(final String uuid, final String oldpwd,
			final String newpwd) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("uuid", uuid);
		map.put("oldpwd", oldpwd);
		map.put("newpwd", newpwd);
		String url = MyProperUtil.getProperties(this,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(this, "appConfigDebug.properties")
						.getProperty("modifyUserPasswd");
		DhNet net = new DhNet(url);
		net.addParams(map).doPost(new NetTask(this) {

			@Override
			public void onErray(Response response) {

				super.onErray(response);
				stopProgressDialog();
			}

			@Override
			public void doInUI(Response response, Integer transfer) {

				String code = JSONUtil.getString(response.jSON(), "code");
				if ("10".equals(code)) {
					Toast.makeText(ResetPswActivity.this, "密码修改成功！",
							Toast.LENGTH_SHORT).show();
					cusPerference.password = newpwd;
					cusPerference.commit();
					finish();
				} else {
					Toast.makeText(ResetPswActivity.this, "密码修改失败！",
							Toast.LENGTH_SHORT).show();
				}
				stopProgressDialog();
			}
		});

	}

	/**
	 * 
	 * @Title: isEmpty
	 * @Description: 判断字符串是否为空
	 * @param @param str
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public boolean isEmpty(String str) {
		if (str == null || "".equals(str)) {
			return true;
		}
		return false;
	}

}
