package com.yuzhuo.easyupbring.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;

import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.adapter.InformationAdapter;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.bean.UserBean;
import com.yuzhuo.easyupbring.bean.UserInfoBean;
import com.yuzhuo.easyupbring.service.APIService;
import com.yuzhuo.easyupbring.service.LBService;
import com.yuzhuo.easyupbring.service.MessageReceiveService;
import com.yuzhuo.easyupbring.utils.MyProperUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetSelfInfoActivity extends BaseAct {

	@InjectView(id = R.id.set_info_linear_back, click = "onClickCallBack")
	private LinearLayout backLayout;
	@InjectView(id = R.id.seek_teacher_linear_layout)
	private LinearLayout seek_teacherLayout;
	@InjectView(id = R.id.seek_parent_linear_layout)
	private LinearLayout seek_parentLayout;
	private TextView submitTextView;
	private EditText locationEditText, courseEditText, salaryEditText,
			otherEditText, gradeEditText, professioalEditText, schoolEditText,
			educationEditText, usernameEditText;
	private ImageView avatarImageView;
	private String avatarString = null;
	LayoutInflater mInflater;
	public String path;
	public Uri imageUri;
	int ncount = 0;
	private String character;
	private APIService apiService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_set_selfinfo);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		character = this.getIntent().getStringExtra("character");
		apiService = new APIService(this);
		initView();
	}

	public void initView() {

		if (Integer.parseInt(character) == 0) {
			seek_teacherLayout.setVisibility(View.VISIBLE);
			seek_parentLayout.setVisibility(View.GONE);
			avatarImageView = (ImageView) findViewById(R.id.seek_teacher_img_avatar);
			usernameEditText = (EditText) findViewById(R.id.viewpager_edtxt_search_teacher_nickname);
			gradeEditText = (EditText) findViewById(R.id.viewpager_edtxt_search_teacher_grade);
			locationEditText = (EditText) findViewById(R.id.viewpager_edtxt_search_teacher_location);
			courseEditText = (EditText) findViewById(R.id.viewpager_edtxt_search_teacher_course);
			salaryEditText = (EditText) findViewById(R.id.viewpager_edtxt_search_teacher_salary);
			otherEditText = (EditText) findViewById(R.id.viewpager_edtxt_search_teacher_other);
			submitTextView = (TextView) findViewById(R.id.viewpager_parent_btn_submit);
		} else {
			seek_teacherLayout.setVisibility(View.GONE);
			seek_parentLayout.setVisibility(View.VISIBLE);
			avatarImageView = (ImageView) findViewById(R.id.seek_parent_img_avatar);
			usernameEditText = (EditText) findViewById(R.id.viewpager_edtxt_search_parent_nickname);
			professioalEditText = (EditText) findViewById(R.id.viewpager_edtxt_search_parent_professional);
			locationEditText = (EditText) findViewById(R.id.viewpager_edtxt_search_parent_location);
			courseEditText = (EditText) findViewById(R.id.viewpager_edtxt_search_parent_course);
			otherEditText = (EditText) findViewById(R.id.viewpager_edtxt_search_parent_other);
			salaryEditText = (EditText) findViewById(R.id.viewpager_edtxt_search_parent_salary);
			schoolEditText = (EditText) findViewById(R.id.viewpager_edtxt_search_parent_school);
			educationEditText = (EditText) findViewById(R.id.viewpager_edtxt_search_parent_education);
			submitTextView = (TextView) findViewById(R.id.viewpager_teacher_btn_submit);
		}
		locationEditText.setText(AppContext.address);
		avatarImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				doChangeBG();

			}
		});
		submitTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if ("".equals(usernameEditText.getText().toString().trim())) {
					Toast.makeText(context, "亲～用户昵还没有设置", Toast.LENGTH_SHORT)
							.show();
				} else {
					UserBean userBean = new UserBean();
					userBean.setAddr(locationEditText.getText().toString()
							.trim());
					userBean.setCourse(courseEditText.getText().toString()
							.trim());
					userBean.setSalary(salaryEditText.getText().toString()
							.trim()
							+ "元/小时");
					userBean.setOther(otherEditText.getText().toString().trim());
					userBean.setUsername(usernameEditText.getText().toString()
							.trim());
					userBean.setAvatar(avatarString);
					if (Integer.parseInt(character) == 0) {
						userBean.setGrade(gradeEditText.getText().toString()
								.trim());
					} else {
						userBean.setSchool(schoolEditText.getText().toString()
								.trim());
						userBean.setProfessional(professioalEditText.getText()
								.toString().trim());
						userBean.setLevel(educationEditText.getText()
								.toString().trim());
					}
					updateUserInfo(getIntent().getStringExtra("uid"), userBean);
				}
			}
		});
	}

	public void onClickCallBack(View view) {
		switch (view.getId()) {

		case R.id.set_info_linear_back:
			finish();
			break;

		default:
			break;
		}
	}

	public void updateUserInfo(String uid, UserBean userBean) {
		Map<String, String> map = new HashMap<String, String>();

		map.put("uid", uid);
		map.put("username", userBean.getUsername());
		map.put("avatar", userBean.getAvatar());
		map.put("addr", userBean.getAddr());
		map.put("course", userBean.getCourse());
		map.put("salary", userBean.getSalary());
		map.put("other", userBean.getOther());
		map.put("latitude", AppContext.latitude);
		map.put("longitude", AppContext.longtude);
		if (Integer.parseInt(character) == 0) {
			map.put("grade", userBean.getGrade());
		} else {
			map.put("school", userBean.getSchool());
			map.put("level", userBean.getLevel());
			map.put("professional", userBean.getProfessional());

		}

		String url = MyProperUtil.getProperties(this,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(this, "appConfigDebug.properties")
						.getProperty("updateUserInfo")
				+ "&data="
				+ LBService.makeParam(map);
		DhNet net = new DhNet(url);
		// 添加参数
		// net.addParam("data", makeParameters(uid, type, page,num));
		Log.e("XIaoxi", "notice ulr = " + url);
		net.doGet(new NetTask(this) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				if ("1".equals(JSONUtil.getString(response.jSON(), "data.code"))) {
					// Intent intent = new Intent();
					// intent.setClass(SetSelfInfoActivity.this,
					// MainActivity.class);
					// startActivity(intent);
					getUserInfoById(AppContext.uid);
					Toast.makeText(SetSelfInfoActivity.this, "设置成功！",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(SetSelfInfoActivity.this, "失败！",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	void getUserInfoById(final String rid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("rid", rid);
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty(
						"getUserInfoById")
				+ "&data="
				+ LBService.makeParam(map);
		DhNet net = new DhNet(url);
		Log.e("MJJ", "login ulr = " + url);

		net.doGet(new NetTask(context) {
			@Override
			public void onErray(Response response) {
				super.onErray(response);
				// 错误处理,出错后会先关闭对话框然后调用这个方法,默认不处理
				Log.e("MJJ", "onErray");
			}

			@Override
			public void doInBackground(Response response) {
				super.doInBackground(response);
				String message = JSONUtil.getString(response.jSON(),
						"data.message");
				String code = JSONUtil.getString(response.jSON(), "data.code");
				response.addBundle("code", code);
				response.addBundle("message", message);
				transfer(response, 150);
			}

			@Override
			public void doInUI(Response response, Integer transfer) {
				if (transfer == 150) {
					Log.e("responseString", response.jSON().toString());
					String message = response.getBundle("message");
					String code = response.getBundle("code");
					if ("1".equals(code)) {
						AppContext.userInfoBean = response.modelFrom(
								UserInfoBean.class, "data.item");
						// Log.e("USERNAME",
						// AppContext.userInfoBean.getCollectnum());
						Intent intent = new Intent(SetSelfInfoActivity.this,
								MainActivity.class);
						startActivity(intent);
						startMessageReceiveService();
						finish();
					}
					Toast.makeText(SetSelfInfoActivity.this, message,
							Toast.LENGTH_SHORT).show();

				}
			}
		});

	}

	public void startMessageReceiveService() {
		Intent serviceIntent = new Intent(this, MessageReceiveService.class);
		startService(serviceIntent);
	}

	private void doChangeBG() {
		String[] item = new String[] { "拍一张", "从相册中获取", "取消" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("更换头像");
		builder.setItems(item, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					photo();
				} else if (which == 1) {
					pickPhoto();
				} else if (which == 2) {

				}
			}
		});
		builder.create().show();

	}

	private void pickPhoto() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, 2000);
	}

	private void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String filePath = Environment.getExternalStorageDirectory()
				+ "/bianbiangou/";
		File files = new File(filePath);
		if (!files.exists()) {
			files.mkdir();
		}
		File file = new File(filePath, String.valueOf(System
				.currentTimeMillis()) + ".jpg");
		path = file.getPath();
		imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, 1000);
	}

	String photoFilePath;

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {

			case 1000:
				if (resultCode == Activity.RESULT_OK && null != path) {
					File f = new File(path);
					if (f.length() == 0) {
						f.deleteOnExit();
					} else {
						// degree = readPictureDegree(imagePath);
						startPhotoZoom(imageUri, 1001);
					}
				}
				break;

			case 2000:
				if (resultCode == Activity.RESULT_OK && null != data) {
					Uri originalUri = data.getData();
					if (originalUri != null) {
						String[] proj = { MediaColumns.DATA };
						Cursor cursor = managedQuery(originalUri, proj, null,
								null, null);
						// 按我个人理解 这个是获得用户选择的图片的索引值
						int column_index = cursor
								.getColumnIndexOrThrow(MediaColumns.DATA);
						// 将光标移至开头 ，这个很重要，不小心很容易引起越界
						cursor.moveToFirst();

						// 最后根据索引值获取图片路径
						photoFilePath = cursor.getString(column_index);

						if ("".equals(photoFilePath) || photoFilePath == null) {
							String wholeID = DocumentsContract
									.getDocumentId(originalUri);
							String id = wholeID.split(":")[1];
							String[] column = { MediaStore.Images.Media.DATA };
							String sel = MediaStore.Images.Media._ID + "=?";
							Cursor cursors = this
									.getContentResolver()
									.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
											column, sel, new String[] { id },
											null);
							int columnIndex = cursors.getColumnIndex(column[0]);
							if (cursors.moveToFirst()) {
								photoFilePath = cursors.getString(columnIndex);
							}
						}
					}
					Log.e("photoFilePath------>", photoFilePath);
					File file = new File(photoFilePath);
					imageUri = Uri.fromFile(file);
				}
				startPhotoZoom(imageUri, 1001);

				break;

			case 1001:
				if (data != null) {
					setPicToView(data);
				}
				break;

			}
		}
	}

	String pathRe, pats;

	@SuppressLint("SdCardPath")
	public void startPhotoZoom(Uri uri, int type) {
		String pathString = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/supertutors";
		File destDir = new File(pathString);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		pats = pathString + "/" + System.currentTimeMillis() + ".jpg";
		pathRe = "file:///sdcard/supertutors/" + System.currentTimeMillis()
				+ ".jpg";
		Log.e("url---->", pats);
		// imageUri = Uri.parse(pathRe);
		Uri imageUri = Uri.parse(pathRe);// The Uri to store the big bitmap
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra("return-data", false);
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, type);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			Uri imageUri = Uri.parse(pathRe);
			avatarImageView.setImageURI(imageUri);
			// pats上传图片的路径
			apiService.postFile(pats, handler);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			avatarString = "http://www.bianbiangou.com/" + "appimg/"
					+ (String) msg.obj;
		};
	};
}
