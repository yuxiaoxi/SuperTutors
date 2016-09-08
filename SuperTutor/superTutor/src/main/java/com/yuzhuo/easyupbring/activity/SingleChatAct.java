package com.yuzhuo.easyupbring.activity;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.adapter.EmoViewPagerAdapter;
import com.yuzhuo.easyupbring.adapter.EmoteAdapter;
import com.yuzhuo.easyupbring.adapter.MessageChatAdapter;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.app.MessageManager;
import com.yuzhuo.easyupbring.app.NoticeManager;
import com.yuzhuo.easyupbring.bean.FaceText;
import com.yuzhuo.easyupbring.bean.IMMessage;
import com.yuzhuo.easyupbring.bean.MessageData;
import com.yuzhuo.easyupbring.bean.Notice;
import com.yuzhuo.easyupbring.service.APIService;
import com.yuzhuo.easyupbring.service.LBService;
import com.yuzhuo.easyupbring.utils.DateUtil;
import com.yuzhuo.easyupbring.utils.FaceTextUtils;
import com.yuzhuo.easyupbring.utils.MyProperUtil;
import com.yuzhuo.easyupbring.widget.EmoticonsEditText;
import com.yuzhuo.easyupbring.widget.RecordButton;
import com.yuzhuo.easyupbring.widget.RecordButton.OnFinishedRecordListener;
import com.yuzhuo.easyupbring.widget.XListView;
import com.yuzhuo.easyupbring.widget.XListView.IXListViewListener;

/**
 * 
 * @ClassName: SingleChatAct
 * @Description: 单聊界面
 * @author 余卓
 * @date 2014年10月21日 上午11:15:43
 * 
 */
public class SingleChatAct extends BaseAct implements OnClickListener,
		IXListViewListener {

	private Button chat_emoButton, btn_chat_send, btn_chat_add,
			btn_chat_keyboard, btn_chat_voice;
	private RecordButton btn_speak;
	private LinearLayout layout_more, layout_emo, layout_add;
	EmoticonsEditText edit_user_comment;
	private ViewPager pager_emo;
	private LinearLayout returnLayout;
	private ImageView moreImageView;
	public XListView mListView;
	private List<IMMessage> message_pool = new ArrayList<IMMessage>();
	private String path = "";
	private TextView tv_picture, tv_camera, tv_location;
	private int pageIndex = 1;
	private static int pageSize = 5;
	private String fid;
	private String name = "";
	Receiver receiver;
	private APIService apiService;
	public static TextView titelTextView;
	String picPath;
	public static String ADDRESS = "";
	public static String LONTITUDE = "0";
	public static String LATITUDE = "0";
	public static Bitmap SAVEBITMAP = null;
	private String locPath;
	private String pathString;
	MessageChatAdapter mAdapter;
	private String useravater = "aaaaa";
	private static int width;
	private static int height;
	private String locType;
	private boolean isFresh;
	Handler picHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			final Message message = new Message();
			String time = DateUtil.date2Str(Calendar.getInstance(),
					AppContext.MS_FORMART);
			String msgtime = String.valueOf(new Date().getTime());
			byte[] data = (byte[]) msg.obj;
			IMMessage newMessages = new IMMessage();
			newMessages.setType(MessageData.TYPE_IMAGE);
			newMessages.setMsgType(1);
			newMessages.setFromSubJid(fid);
			newMessages.setContent(Base64.encodeToString(data, 0, data.length,
					Base64.NO_WRAP));
			newMessages.setTime(msgtime);
			newMessages.setName("小昔" + "," + name);
			newMessages.setPicPath(AppContext.userInfoBean.getAvatar() + ","
					+ useravater);
			// message_pool.add(newMessages);
//			MessageManager.getInstance(SingleChatAct.this).saveIMMessage(
//					newMessages);
			refreshMessage(newMessages);
			// apiService.postImage("3", fid.split("@")[1], localCameraPath,
			// "2");
			if (newMessages.getName().endsWith(",")
					|| newMessages.getPicPath().endsWith(",")
					|| newMessages.getFromSubJid().endsWith("@")) {
				Toast.makeText(SingleChatAct.this, "发送失败！请检查网络是否正常连接！然后刷新界面！",
						Toast.LENGTH_SHORT).show();
			} else {
				apiService.postFile("1001", localCameraPath, AppContext.uid,
						fid.split("@")[1], "2", newMessages, 1, "", "", "",
						width + "", height + "");
			}
		};
	};

	Handler soundHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String time = DateUtil.date2Str(Calendar.getInstance(),
					AppContext.MS_FORMART);
			String msgtime = String.valueOf(new Date().getTime());
			String data = (String) msg.obj;
			IMMessage newMessages = new IMMessage();
			newMessages.setType(MessageData.TYPE_RECORD);
			newMessages.setMsgType(1);
			newMessages.setFromSubJid(fid);
			newMessages.setContent(data + "," + btn_speak.intervalTime);
			newMessages.setTime(msgtime);
			newMessages.setName("小昔" + "," + name);
			newMessages.setPicPath(AppContext.userInfoBean.getAvatar() + ","
					+ useravater);
			// message_pool.add(newMessages);
//			MessageManager.getInstance(SingleChatAct.this).saveIMMessage(
//					newMessages);
			refreshMessage(newMessages);
			if (newMessages.getName().endsWith(",")
					|| newMessages.getPicPath().endsWith(",")
					|| newMessages.getFromSubJid().endsWith("@")) {
				Toast.makeText(SingleChatAct.this, "发送失败！请检查网络是否正常连接！然后刷新界面！",
						Toast.LENGTH_SHORT).show();
			} else {
//				apiService.postRecord("1001", path, AppContext.uid,
//						fid.split("@")[1], "3", newMessages);
			}
		};
	};

	Handler locationHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			final Message message = new Message();
			String time = DateUtil.date2Str(Calendar.getInstance(),
					AppContext.MS_FORMART);
			String msgtime = String.valueOf(new Date().getTime());
			byte[] data = (byte[]) msg.obj;
			IMMessage newMessages = new IMMessage();
			newMessages.setType(MessageData.TYPE_LOCATION);
			newMessages.setMsgType(1);
			newMessages.setFromSubJid(fid);
			String detail = ADDRESS
					+ ","
					+ LONTITUDE
					+ ","
					+ LATITUDE
					+ ","
					+ Base64.encodeToString(data, 0, data.length,
							Base64.NO_WRAP);
			Log.e("xiaoxi", detail);
			newMessages.setContent(detail);
			newMessages.setTime(msgtime);
			newMessages.setName("小昔" + "," + name);
			newMessages.setPicPath(AppContext.AVATAR + "," + useravater);
			MessageManager.getInstance(SingleChatAct.this).saveIMMessage(
					newMessages);
			refreshMessage(newMessages);
			if (newMessages.getName().endsWith(",")
					|| newMessages.getPicPath().endsWith(",")
					|| newMessages.getFromSubJid().endsWith("@")) {
				Toast.makeText(SingleChatAct.this, "发送失败！请检查网络是否正常连接！然后刷新界面！",
						Toast.LENGTH_SHORT).show();
			} else {
				// apiService.postFile(LBService.APPID, locPath,
				// AppContext.UID, fid.split("@")[1], "4", newMessages, 2,
				// ADDRESS, LATITUDE, LONTITUDE, width + "", height + "");
			}
		};
	};

	Handler IMUIHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 111) {
				mListView.stopRefresh();
				List<IMMessage> messages = (List<IMMessage>) msg.obj;
				if (messages != null) {
					message_pool.addAll(messages);
					if (null != message_pool && message_pool.size() > 0)
						Collections.sort(message_pool);
					mAdapter.notifyDataSetChanged();
				}
			} else if (msg.what == 222) {
				initView();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.act_singlechat);
		// getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		pathString = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/supertutor";
		File destDir = new File(pathString);

		if (!destDir.exists()) {
			destDir.mkdirs();
		}

		path = pathString + "/" + System.currentTimeMillis() + ".amr";
		locPath = pathString + "/" + System.currentTimeMillis() + ".jpg";
		fid = getIntent().getExtras().getString("fid", "s@1010");
		name = getIntent().getExtras().getString("name");
		Log.e("fid", fid);
		apiService = new APIService(this);
		initView();
		getUserI(fid.split("@")[1]);
	}

	void initView() {
		titelTextView = (TextView) findViewById(R.id.chat_information);
		titelTextView.setText(name);
		chat_emoButton = (Button) findViewById(R.id.btn_chat_emo);
		// 最下面
		layout_more = (LinearLayout) findViewById(R.id.layout_more);
		layout_emo = (LinearLayout) findViewById(R.id.layout_emo);
		layout_add = (LinearLayout) findViewById(R.id.layout_add);
		mListView = (XListView) findViewById(R.id.mListView);
		// 输入框
		edit_user_comment = (EmoticonsEditText) findViewById(R.id.edit_user_comment);
		btn_chat_add = (Button) findViewById(R.id.btn_chat_add);
		btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
		// 最右边
		btn_chat_keyboard = (Button) findViewById(R.id.btn_chat_keyboard);
		btn_chat_voice = (Button) findViewById(R.id.btn_chat_voice);
		returnLayout = (LinearLayout) findViewById(R.id.singlechat_linear_back);
		moreImageView = (ImageView) findViewById(R.id.singlechat_imgMore);
		// 最中间
		// 语音框
		btn_speak = (RecordButton) findViewById(R.id.btn_speak);

		btn_speak.setSavePath(path);
		btn_speak.setOnFinishedRecordListener(new OnFinishedRecordListener() {

			@Override
			public void onFinishedRecord(String audioPath) {
				Message messgae = new Message();
				// ImSendMsgData data = new ImSendMsgData();
				// data.duDuId = 298374980;
				final String data = audioPath;
				new Thread() {
					public void run() {
						String str = soundToString(data);
						android.os.Message msg = new android.os.Message();
						msg.obj = str;
						soundHandler.sendMessage(msg);
					};
				}.start();
			}
		});

		edit_user_comment.setOnClickListener(this);
		edit_user_comment.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (!TextUtils.isEmpty(s)) {
					btn_chat_send
							.setBackgroundResource(R.drawable.talk_activity_btn_send);
					btn_chat_send.setVisibility(View.VISIBLE);
					btn_chat_keyboard.setVisibility(View.GONE);
					btn_chat_voice.setVisibility(View.GONE);
				} else {
					if (btn_chat_voice.getVisibility() != View.VISIBLE) {
						btn_chat_voice.setVisibility(View.GONE);
						btn_chat_send
								.setBackgroundResource(R.drawable.talk_activity_btn_voice);
						btn_chat_send.setVisibility(View.VISIBLE);
						btn_chat_keyboard.setVisibility(View.GONE);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		btn_chat_keyboard.setOnClickListener(this);
		returnLayout.setOnClickListener(this);
		moreImageView.setOnClickListener(this);
		chat_emoButton.setOnClickListener(this);
		btn_chat_add.setOnClickListener(this);
		btn_chat_send.setOnClickListener(this);
		initEmoView();
		initXListView();
		initAddView();

	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.btn_chat_emo:// 点击笑脸图标
			if (layout_more.getVisibility() == View.GONE) {
				showEditState(true);
			} else {
				if (layout_add.getVisibility() == View.VISIBLE) {
					layout_add.setVisibility(View.GONE);
					layout_emo.setVisibility(View.VISIBLE);
				} else {
					layout_more.setVisibility(View.GONE);
				}
			}
			if (btn_chat_voice.getVisibility() == View.VISIBLE
					&& btn_chat_send.getVisibility() == View.VISIBLE) {
				btn_chat_voice.setVisibility(View.GONE);
			}
			break;

		case R.id.singlechat_linear_back:
			hideSoftInputView();
			finish();
			break;
		case R.id.btn_chat_add:// 添加按钮-显示图片、拍照、位置
			if (layout_more.getVisibility() == View.GONE) {
				layout_more.setVisibility(View.VISIBLE);
				layout_add.setVisibility(View.VISIBLE);
				layout_emo.setVisibility(View.GONE);
				hideSoftInputView();
			} else {
				if (layout_emo.getVisibility() == View.VISIBLE) {
					layout_emo.setVisibility(View.GONE);
					layout_add.setVisibility(View.VISIBLE);
				} else {
					layout_more.setVisibility(View.GONE);
				}
			}

			break;

		case R.id.edit_user_comment:// 点击文本输入框
			mListView.setSelection(mListView.getCount() - 1);
			if (layout_more.getVisibility() == View.VISIBLE) {
				layout_add.setVisibility(View.GONE);
				layout_emo.setVisibility(View.GONE);
				layout_more.setVisibility(View.GONE);
			}
			break;

		case R.id.btn_chat_keyboard:
			btn_chat_voice.setVisibility(View.GONE);
			btn_chat_send.setVisibility(View.VISIBLE);
			btn_speak.setVisibility(View.GONE);
			btn_chat_keyboard.setVisibility(View.GONE);
			edit_user_comment.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_chat_send:// 发送文本

			final String msg = edit_user_comment.getText().toString().trim();
			String time = DateUtil.date2Str(Calendar.getInstance(),
					AppContext.MS_FORMART);
			String msgtime = String.valueOf(new Date().getTime());
			if (msg.equals("")) {
				btn_chat_keyboard.setVisibility(View.VISIBLE);
				btn_speak.setVisibility(View.VISIBLE);
				btn_chat_send.setVisibility(View.GONE);
				edit_user_comment.setVisibility(View.GONE);
				return;
			}
			if (edit_user_comment.getText().toString().trim().length() > 0) {
				IMMessage newMessages = new IMMessage();
				newMessages.setType(MessageData.TYPE_TEXT);
				newMessages.setMsgType(1);
				newMessages.setFromSubJid(fid);
				newMessages.setContent(msg);
				newMessages.setTime(msgtime);
				newMessages.setPicPath(AppContext.AVATAR + "," + useravater);
				newMessages.setName("小昔" + "," + name); //
				refreshMessage(newMessages);
				if (newMessages.getName().endsWith(",")
						|| newMessages.getPicPath().endsWith(",")
						|| newMessages.getFromSubJid().endsWith("@")) {
					Toast.makeText(this, "发送失败！请检查网络是否正常连接！然后刷新界面！",
							Toast.LENGTH_SHORT).show();
				} else {
					Log.e("messageString", newMessages.toString());
					
					MessageManager.getInstance(this).saveIMMessage(newMessages);
					apiService.postMsg(AppContext.uid, fid.split("@")[1], msg,
							"1", "0", "0", "0");					

				}
			} else {
				Toast.makeText(SingleChatAct.this, "亲，发送内容不能为空，请输入...",
						Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.tv_camera:// 拍照
			selectImageFromCamera();
			break;
		case R.id.tv_picture:// 图片
			selectImageFromLocal();
			break;
		// case R.id.tv_location:// 位置
		// selectLocationFromMap();
		// break;
		default:
			break;
		}
	}

	List<FaceText> emos;

	/**
	 * 初始化表情布局
	 * 
	 * @Title: initEmoView
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initEmoView() {
		pager_emo = (ViewPager) findViewById(R.id.pager_emo);
		emos = FaceTextUtils.faceTexts;

		List<View> views = new ArrayList<View>();
		for (int i = 0; i < 5; ++i) {
			views.add(getGridView(i));
		}
		pager_emo.setAdapter(new EmoViewPagerAdapter(views));
	}

	private View getGridView(final int i) {
		View view = View.inflate(this, R.layout.include_emo_gridview, null);
		GridView gridview = (GridView) view.findViewById(R.id.gridview);
		List<FaceText> list = new ArrayList<FaceText>();
		if (i == 0) {
			list.addAll(emos.subList(0, 21));
		} else if (i == 1) {
			list.addAll(emos.subList(21, 42));
		} else if (i == 2) {
			list.addAll(emos.subList(42, 63));
		} else if (i == 3) {
			list.addAll(emos.subList(63, 84));
		} else if (i == 4) {
			list.addAll(emos.subList(84, emos.size()));
		}
		final EmoteAdapter gridAdapter = new EmoteAdapter(SingleChatAct.this,
				list);
		gridview.setAdapter(gridAdapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				FaceText name = (FaceText) gridAdapter.getItem(position);
				String key = name.text.toString();
				try {
					if (edit_user_comment != null && !TextUtils.isEmpty(key)) {
						int start = edit_user_comment.getSelectionStart();
						CharSequence content = edit_user_comment.getText()
								.insert(start, key);
						edit_user_comment.setText(content);
						// 定位光标位置
						CharSequence info = edit_user_comment.getText();
						if (info instanceof Spannable) {
							Spannable spanText = (Spannable) info;
							Selection.setSelection(spanText,
									start + key.length());
						}
					}
					// }
				} catch (Exception e) {

				}

			}
		});
		return view;
	}

	/**
	 * 根据是否点击笑脸来显示文本输入框的状态
	 * 
	 * @Title: showEditState
	 * @Description: TODO
	 * @param @param isEmo: 用于区分文字和表情
	 * @return void
	 * @throws
	 */
	private void showEditState(boolean isEmo) {
		edit_user_comment.setVisibility(View.VISIBLE);
		btn_chat_keyboard.setVisibility(View.GONE);
		btn_chat_voice.setVisibility(View.VISIBLE);
		btn_speak.setVisibility(View.GONE);
		edit_user_comment.requestFocus();
		if (isEmo) {
			layout_more.setVisibility(View.VISIBLE);
			layout_more.setVisibility(View.VISIBLE);
			layout_emo.setVisibility(View.VISIBLE);
			layout_add.setVisibility(View.GONE);
			hideSoftInputView();
		} else {
			layout_more.setVisibility(View.GONE);
			showSoftInputView();
		}
	}

	private String localCameraPath = "";// 拍照后得到的图片地址

	/**
	 * 启动相机拍照 startCamera
	 * 
	 * @Title: startCamera
	 * @throws
	 */
	public void selectImageFromCamera() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File dir = new File(AppContext.BMOB_PICTURE_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, String.valueOf(System.currentTimeMillis())
				+ ".jpg");
		localCameraPath = file.getPath();
		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent,
				AppContext.REQUESTCODE_TAKE_CAMERA);
		// pageIndex = 1;
		// message_pool.clear();
	}

	/**
	 * 选择图片
	 * 
	 * @Title: selectImage
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void selectImageFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, AppContext.REQUESTCODE_TAKE_LOCAL);
		// pageIndex = 1;
		// message_pool.clear();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case AppContext.REQUESTCODE_TAKE_CAMERA:// 当取到值的时候才上传path路径下的图片到服务器
				Toast.makeText(SingleChatAct.this,
						"本地图片的地址：" + localCameraPath, Toast.LENGTH_SHORT)
						.show();
				sendImageMessage(localCameraPath);
				break;
			case AppContext.REQUESTCODE_TAKE_LOCAL:
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						Cursor cursor = getContentResolver().query(
								selectedImage, null, null, null, null);
						cursor.moveToFirst();
						int columnIndex = cursor.getColumnIndex("_data");
						localCameraPath = cursor.getString(columnIndex);
						cursor.close();
						if (localCameraPath == null
								|| localCameraPath.equals("null")) {

							return;
						}
						sendImageMessage(localCameraPath);
					}
				}
				break;
			}
		} else if (requestCode == AppContext.REQUESTCODE_TAKE_LOCATION) {
			double latitude = Double.parseDouble(LATITUDE);// 维度
			double longtitude = Double.parseDouble(LONTITUDE);// 经度
			String address = ADDRESS;
			if (address == null || address.equals("")) {
				Log.e("loction", "无法获取到您的位置信息!");
			} else {

				sendLocationMessage();
			}

		}
	}

	private String getTime(String time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = null;
		if (time.equals("")) {
			return "";
		}
		sdf = new SimpleDateFormat("MM月dd日");
		long loc_time = Long.valueOf(time);
		re_StrTime = sdf.format(new Date(loc_time * 1000L));
		return re_StrTime;
	}

	private Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 50) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	// 显示软键盘
	public void showSoftInputView() {
		if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.showSoftInput(edit_user_comment, 0);
		}
	}

	/**
	 * 隐藏软键盘 hideSoftInputView
	 * 
	 * @Title: hideSoftInputView
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	public void hideSoftInputView() {
		InputMethodManager manager = ((InputMethodManager) this
				.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public String soundToString(String path) {

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		BufferedInputStream bis = new BufferedInputStream(fis);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int c;
		try {
			c = bis.read();
			while (c != -1) {
				baos.write(c);
				c = bis.read();
			}
			bis.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}// 读取bis流中的下一个字节
		byte retArr[] = baos.toByteArray();
		return Base64.encodeToString(retArr, 0, retArr.length, Base64.NO_WRAP);
	}

	/**
	 * 刷新界面 refreshMessage
	 * 
	 * @Title: refreshMessage
	 * @Description: TODO
	 * @param @param message
	 * @return void
	 * @throws
	 */
	private void refreshMessage(IMMessage msg) {
		// 更新界面
		mAdapter.add(msg);
		mListView.setSelection(mAdapter.getCount() - 1);
		edit_user_comment.setText("");
	}

	private void initXListView() {
		// 首先不允许加载更多
		mListView.setPullLoadEnable(false);
		// 允许下拉
		mListView.setPullRefreshEnable(true);
		// 设置监听器
		mListView.setXListViewListener(this);
		mListView.pullRefreshing();
		// 加载数据
		mAdapter = new MessageChatAdapter(this, message_pool);
		mListView.setAdapter(mAdapter);
		// 初始化定位到最后一条
		mListView.setSelection(mAdapter.getCount() - 1);
		mListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				hideSoftInputView();
				layout_more.setVisibility(View.GONE);
				layout_add.setVisibility(View.GONE);
				return false;
			}
		});

		// 重发按钮的点击事件
		mAdapter.setOnInViewClickListener(R.id.iv_fail_resend,
				new MessageChatAdapter.onInternalClickListener() {

					@Override
					public void OnClickListener(View parentV, View v,
							Integer position, Object values) {
						// 重发消息
						// ShowLog("点击事件");
						// showResendDialog(parentV, v, values);
					}
				});
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		loadData();
		mListView.stopRefresh();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	private void initAddView() {
		tv_picture = (TextView) findViewById(R.id.tv_picture);
		tv_camera = (TextView) findViewById(R.id.tv_camera);
		tv_location = (TextView) findViewById(R.id.tv_location);
		// tv_active = (TextView) findViewById(R.id.tv_active);
		tv_picture.setOnClickListener(this);
		tv_location.setOnClickListener(this);
		tv_camera.setOnClickListener(this);
		// tv_active.setOnClickListener(this);
	}

	/**
	 * 默认先上传本地图片，之后才显示出来 sendImageMessage
	 * 
	 * @Title: sendImageMessage
	 * @Description: TODO
	 * @param @param localPath
	 * @return void
	 * @throws
	 */
	private void sendImageMessage(final String local) {
		if (layout_more.getVisibility() == View.VISIBLE) {
			layout_more.setVisibility(View.GONE);
			layout_add.setVisibility(View.GONE);
			layout_emo.setVisibility(View.GONE);
		}
		/*
		 * try { bitmap = Bimp.revitionImageSize(local); } catch (IOException
		 * e1) { // TODO Auto-generated catch block e1.printStackTrace(); }
		 */
		new Thread() {
			public void run() {
				try {
					byte[] data = bitmapToStr(getimage(local));
					android.os.Message msg = new android.os.Message();
					msg.obj = data;
					picHandler.sendMessage(msg);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
	}

	public static byte[] bitmapToStr(Bitmap bitmap) {
		byte[] imageurlStr = null;
		if (bitmap != null) {
			width = bitmap.getWidth();
			height = bitmap.getHeight();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// 将bitmap一字节流输出 Bitmap.CompressFormat.PNG 压缩格式，100：压缩率，baos：字节流
			bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
			// FileOutputStream fos = null;
			try {
				// fos = new FileOutputStream(new
				// File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/xiaoxi.txt"));
				// baos.writeTo(fos);
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			byte[] buffer = baos.toByteArray();
			// 将图片的字节流数据加密成base64字符输出
			// imageurlStr = Base64.encodeToString(buffer, 0, buffer.length,
			// Base64.NO_WRAP);
			imageurlStr = buffer;
			// Log.e("imageurlStr", new String(imageurlStr));
		}
		return imageurlStr;
	}

	public static String byte2hex(byte[] b) { // 一个字节的数，
		// 转成16进制字符串
		String hs = "";
		String tmp = "";
		for (int n = 0; n < b.length; n++) {
			// 整数转成十六进制表示
			tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (tmp.length() == 1) {
				hs = hs + "0" + tmp;
			} else {
				hs = hs + tmp;
			}
		}
		tmp = null;
		return hs.toUpperCase(); // 转成大写
	}

	private void loadData() {

		List<IMMessage> listData = MessageManager.getInstance(this)
				.getMessageListByFrom(getIntent().getExtras().getString("fid"),
						pageIndex, pageSize);
		// Log.e("MJJ",
		// "loadData我是第"+pageIndex+"次来加载数据，加载完了我有+"+listData.size()+"个数据");
		android.os.Message msg = new android.os.Message();
		msg.what = 111;
		msg.obj = listData;
		IMUIHandler.sendMessage(msg);
		pageIndex = pageIndex + 1;
	}

	@Override
	protected void onResume() {
		if (!isFresh) {
			loadData();
			isFresh = true;
		}
		receiver = new Receiver();
		IntentFilter filter = new IntentFilter(
				AppContext.SINGLE_CHAT_NEW_MESSAGE);
		registerReceiver(receiver, filter);
		NoticeManager.getInstance(this).updateStatusByFrom(fid, Notice.READ);
		super.onResume();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}

	class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (AppContext.SINGLE_CHAT_NEW_MESSAGE.equals(action)) {
				IMMessage message = intent.getParcelableExtra("IMMessage");
				Log.e("MJJ", "接收" + message.toString());
				// NotificationManager notificationManager =
				// (NotificationManager) context
				// .getSystemService(android.content.Context.NOTIFICATION_SERVICE);
				// notificationManager.cancel(321);
				// NoticeManager.getInstance(context).updateStatusByFrom(toJID,
				// Notice.READ);
				String ttString = edit_user_comment.getText().toString();
				refreshMessage(message);
				edit_user_comment.setText(ttString);
			}
		}
	}

	/**
	 * 
	 * @Title: sendLocationMessage
	 * @Description: 发送位置信息
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void sendLocationMessage() {
		if (layout_more.getVisibility() == View.VISIBLE) {
			layout_more.setVisibility(View.GONE);
			layout_add.setVisibility(View.GONE);
			layout_emo.setVisibility(View.GONE);
		}
		// 组装BmobMessage对象
		// Message message = new Message();
		// ImSendMsgData data = new ImSendMsgData();
		// data.duDuId = 24789493;
		if (MapActivity.saveBitmap != null) {
			int w = MapActivity.saveBitmap.getWidth();
			int h = MapActivity.saveBitmap.getHeight();
			final Bitmap bitmap = Bitmap.createBitmap(MapActivity.saveBitmap,
					0, h / 3, w, h / 3);
			try {
				saveMyBitmap(locPath, bitmap);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			new Thread() {
				public void run() {
					byte[] str = bitmapToStr(bitmap);
					android.os.Message msg = new android.os.Message();
					msg.obj = str;
					locationHandler.sendMessage(msg);
				};
			}.start();
		}
	}

	/**
	 * 
	 * @Title: saveMyBitmap
	 * @Description: 将bitmap输出为文件
	 * @param @param locpath
	 * @param @param mBitmap
	 * @param @throws IOException 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void saveMyBitmap(String locpath, Bitmap mBitmap) throws IOException {
		File f = new File(locpath);
		f.createNewFile();
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 20, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 启动地图
	 * 
	 * @Title: selectLocationFromMap
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void selectLocationFromMap() {
		Intent intent = new Intent(this, MapActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("type", "1");
		intent.putExtras(bundle);
		startActivityForResult(intent, AppContext.REQUESTCODE_TAKE_LOCATION);
		// pageIndex = 1;
		// message_pool.clear();
	}

	/**
	 * 
	 * @Title: getUserI
	 * @Description: 获取用户姓名头像
	 * @param @param uid 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void getUserI(String uid) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("rid", uid);
		String url = MyProperUtil.getProperties(this,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(this, "appConfigDebug.properties")
						.getProperty("getUserInfoById")
				+ "&data="
				+ LBService.makeParam(map);
		DhNet net = new DhNet(url);
		// 添加参数
		// net.addParam("data", makeParameters(uid, type, page,num));
		Log.e("getUserI", "notice ulr = " + url);
		net.doGet(new NetTask(this) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				titelTextView.setText(JSONUtil.getString(response.jSON(),
						"data.item.username"));
				name = JSONUtil.getString(response.jSON(), "data.item.username");
				useravater = JSONUtil.getString(response.jSON(),
						"data.item.avatar");
				if ("".equals(useravater)) {
					useravater = "bbb";
				}
				Log.e("useravater", useravater);
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		hideSoftInputView();
	}

}
