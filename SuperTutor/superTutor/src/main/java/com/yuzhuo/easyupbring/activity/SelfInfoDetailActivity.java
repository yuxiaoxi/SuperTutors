package com.yuzhuo.easyupbring.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;

import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.bean.UserBean;
import com.yuzhuo.easyupbring.service.APIService;
import com.yuzhuo.easyupbring.service.LBService;
import com.yuzhuo.easyupbring.utils.ImageLoader;
import com.yuzhuo.easyupbring.utils.MyProperUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SelfInfoDetailActivity extends BaseAct {

	@InjectView(id =R.id.selfinfo_detail_linear_back,click="onClickCallBack")
	private LinearLayout backLayout;
	@InjectView(id = R.id.selfinfo_detail_img_avatar,click="onClickCallBack")
	private ImageView selfinfo_detail_img_avatar;
	@InjectView(id = R.id.selfinfo_detail_txt_location)
	private TextView locationTextView;
	@InjectView(id = R.id.selfinfo_detail_txt_education)
	private TextView educationTextView;
	@InjectView(id = R.id.selfinfo_detail_txt_professional)
	private TextView professionalTextView;
	@InjectView(id = R.id.selfinfo_detail_txt_course)
	private TextView courseTextView;
	@InjectView(id =R.id.selfinfo_detail_txt_salary)
	private TextView salaryTextView;
	@InjectView(id = R.id.selfinfo_detail_txt_school)
	private TextView schoolTextView;
	@InjectView(id = R.id.selfinfo_detail_txt_other)
	private TextView otherTextView;
	@InjectView(id = R.id.selfinfo_detail_txt_nickname)
	private TextView nicknameTextView;
	@InjectView(id = R.id.selfinfo_detail_linear_stu)
	private LinearLayout stuLayout;
	@InjectView(id = R.id.selfinfo_detail_linear_tea)
	private LinearLayout teaLayout;
	@InjectView(id = R.id.selfinfo_detail_txt_salary_stu)
	private TextView salaryTextView2;
	@InjectView(id = R.id.selfinfo_detail_txt_course_stu)
	private TextView courseTextView2;
	@InjectView(id = R.id.selfinfo_detail_txt_grade_stu)
	private TextView gradeTextView;
	@InjectView(id = R.id.selfinfo_detail_txt_location_stu)
	private TextView locationTextView2;
	private UserBean userBean;
	private ImageLoader imageLoader;
	
	public String path;

	public Uri imageUri;
	String photoFilePath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_selfinfo_detail);
		imageLoader = ImageLoader.getIncetence(this);
		initView();
		loadData();
	}
	
	void initView(){
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int height = wm.getDefaultDisplay().getHeight();
		int width = wm.getDefaultDisplay().getWidth();
		int xheith = (int)(height*2.2)/8 - APIService.dip2px(context, 74);
		int ywidth = (width - xheith)/2;
		selfinfo_detail_img_avatar.setLayoutParams(new LinearLayout.LayoutParams(xheith,xheith));
		selfinfo_detail_img_avatar.setY(APIService.dip2px(context, 10));
		selfinfo_detail_img_avatar.setX(ywidth);
		if ("1".equals(AppContext.CHARACTER)) {
			teaLayout.setVisibility(View.VISIBLE);
			stuLayout.setVisibility(View.GONE);
		}else {
			teaLayout.setVisibility(View.GONE);
			stuLayout.setVisibility(View.VISIBLE);
		}
	}
	
	public void onClickCallBack(View view){
		switch (view.getId()) {
		case R.id.selfinfo_detail_linear_back:
			finish();
			break;
			
		case R.id.selfinfo_detail_img_avatar:
			doChangeBG();
			break;

		default:
			break;
		}
	}
	
	public void loadData(){
		getUserInfoById(AppContext.uid);
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
				+ "/weixiaoyuan/";
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
						startPhotoZoom(imageUri);
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
				startPhotoZoom(imageUri);

				break;
			case 3000:
				if (data != null) {
					setPicToView(data);
				}
				break;

			}
		}
	}

	String pathRe, pats;

	String pathString = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/weixiaoyuan";

	public void startPhotoZoom(Uri uri) {
		String pathString = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/weixiaoyuan";
		File destDir = new File(pathString);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		pats = pathString + "/" + System.currentTimeMillis() + ".jpg";
		pathRe = "file:///sdcard/weixiaoyuan/" + System.currentTimeMillis()
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
		startActivityForResult(intent, 3000);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			// bitmap = extras.getParcelable("data");
			startProgressDialog();
			postFile("1001", pats);
		}
	}
	
	public void postFile(String appid, String path) {
		File file = new File(path);
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("FileHost")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty("fileserver");
		DhNet net = new DhNet(url);
		Log.e("posturl", url);
		// 添加参数
		net.addParam("appid", appid).addFile("content", new File(path))
				.upload(new NetTask(this) {
					@Override
					public void doInUI(Response response, Integer transfer) {
						if (response.isSuccess()) {
							Boolean uploading = response.getBundle("uploading");
							if (!uploading) {
								String photoUrl = JSONUtil.getString(
										response.jSON(), "data.info.self.url");
								updateUserInfo(AppContext.username, photoUrl);
							} else {

							}
						}
					}

					@Override
					public void onErray(Response response) {
						stopProgressDialog();
						super.onErray(response);
					}
				});
	}
	
	public void updateUserInfo(final String uid, final String avatar) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("uid", uid);
		map.put("avatar", avatar);
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
					AppContext.AVATAR = avatar;
					imageLoader.displayImage(avatar, selfinfo_detail_img_avatar, 0, false);
					Toast.makeText(
							SelfInfoDetailActivity.this,
							"设置成功！",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(SelfInfoDetailActivity.this, "失败！",
							Toast.LENGTH_SHORT).show();
				}
				stopProgressDialog();
			}
		});

	}
	
	
	void getUserInfoById(final String rid) {
		startProgressDialog();
		Map<String, String> map = new HashMap<String, String>();
		map.put("rid", rid);
		
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty(
						"getUserInfoById") + "&data=" + LBService.makeParam(map);
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
					userBean = response.modelFrom(UserBean.class, "data.item");
					imageLoader.displayImage(userBean.getAvatar(), selfinfo_detail_img_avatar);
					nicknameTextView.setText(userBean.getUsername());
					if ("1".equals(AppContext.CHARACTER)) {
						locationTextView.setText(userBean.getAddr());
						schoolTextView.setText(userBean.getSchool());
						educationTextView.setText(userBean.getLevel());
						professionalTextView.setText(userBean.getProfessional());
						courseTextView.setText(userBean.getCourse());
						salaryTextView.setText(userBean.getSalary());
						
					}else {
						locationTextView2.setText(userBean.getAddr());
						courseTextView2.setText(userBean.getCourse());
						salaryTextView2.setText(userBean.getSalary());
						gradeTextView.setText(userBean.getGrade());
					}
					otherTextView.setText(userBean.getOther());
				}else{
					Toast.makeText(SelfInfoDetailActivity.this, message,
							Toast.LENGTH_SHORT).show();
				}
				stopProgressDialog();
			}
		});

	}
}
