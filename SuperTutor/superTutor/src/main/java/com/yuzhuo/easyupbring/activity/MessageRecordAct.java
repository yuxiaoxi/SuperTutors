package com.yuzhuo.easyupbring.activity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.view.PullToRefreshBase.OnRefreshListener;
import net.duohuo.dhroid.view.PullToRefreshListView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout.LayoutParams;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.adapter.MessageUserAdapter;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.app.MessageManager;
import com.yuzhuo.easyupbring.bean.ChartHisBean;
import com.yuzhuo.easyupbring.bean.IMMessage;
import com.yuzhuo.easyupbring.service.CusPerference;
import com.yuzhuo.easyupbring.utils.ImageLoader;

public class MessageRecordAct extends BaseAct {
	private PullToRefreshListView messagePullListView;
	private ListView messageListView = null;
	public static final int MODE_PULL_DOWN_TO_REFRESH = 0x1; // 下拉
	public static final int MODE_DEFAULT_LOAD = 0x2; // 上拉
	public boolean isRefresh = false;
	private MessageUserAdapter messageAdapter;
	private ArrayList<ChartHisBean> inviteNotices = new ArrayList<ChartHisBean>();
	private int messageTimes = 1;
	private int position;
	private Dialog alertDialog;
	private Receiver receiver;
	String fuid;
	ImageLoader imageLoader;
	CusPerference cusPerference;
	Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		receiver = new Receiver();
		cusPerference = new CusPerference();
		cusPerference.load();
		IntentFilter filter = new IntentFilter(
				AppContext.REFRESH_MESSAGE_RECORD);
		registerReceiver(receiver, filter);
		setContentView(R.layout.act_message_record);
		imageLoader = ImageLoader.getIncetence(context);
		initView();
	}

	void initView() {
		messagePullListView = (PullToRefreshListView) findViewById(R.id.ztgame_list_friends_activity_message);

		messagePullListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh(int pullDirection) {
				switch (pullDirection) {
				// 1下拉,2上拉
				case MODE_PULL_DOWN_TO_REFRESH:
					isRefresh = true;
					messageTimes = 1;
					getData2();
					break;
				}
			}
		});

		messageListView = messagePullListView.getRefreshableView();
		messageListView.setDividerHeight(0);

		messageListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> adapter,
							View view, int arg2, long arg3) {
						position = (int) arg3;

						deleteDialog();

						return true;
					}
				});

		messageListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int pstion = (int) arg3;

				Intent intent = new Intent();
				// Intent intent = new Intent ();
				// messageManager.updateStatus(id, status);
				String fid = inviteNotices.get(pstion).getFrom();
				// String name = inviteNotices.get(pstion).getName();
				// Log.d("MJJ", name);
				String picPath = inviteNotices.get(pstion).getPicPath();
				Log.d("MJJ", "picPath" + inviteNotices.get(pstion).getPicPath());
				String loaclName = "";
				String titleName = inviteNotices.get(pstion).getName();
				if (inviteNotices.get(pstion).getType() == 1) {
					loaclName = titleName.split(",")[1];
				} else {
					if (titleName.contains(",")) {
						loaclName = titleName.split(",")[0];
					}
				}

				// if (picPath != null && picPath.contains(",")) {
				// picPath = picPath.split(",")[1];
				// }
				// if(picPath!=null && !picPath.equals("")){
				// if(picPath.startsWith("/upload")){
				// picPath=MyProperUtil.getProperties(context).getProperty("photoSrc")+picPath;
				// }
				// }
				intent.putExtra("fid", fid);
				if (fid.contains("@conference")) {
					// 群聊天
					if (inviteNotices.get(pstion).getType() == 1) {
						intent.putExtra("name", titleName.split(",")[1]);
					} else {
						intent.putExtra("name", titleName.split(",")[2]);
					}

					// intent.setClass(MessageRecordActivity.this,
					// ChatRoomActivity.class);
					// intent.putExtra("picPath", picPath);

				} else {
					// 聊天
					Log.e("msg_type", inviteNotices.get(pstion).getMsg_type()
							+ "");
					if ("single".equals(inviteNotices.get(pstion).getFrom()
							.split("@")[0])) {
						intent.setClass(MessageRecordAct.this,
								SingleChatAct.class);
						// intent.putExtra("picPath", picPath);
						intent.putExtra("name", loaclName);
						clearNotification(10000 + Integer
								.parseInt(inviteNotices.get(pstion).getFrom()
										.split("@")[1]));
					}

				}

				MessageRecordAct.this.startActivity(intent);

			}
		});
	}

	/**
	 * 获取消息用户数据方法
	 */
	private void getData2() {

		new Thread() {
			public void run() {
				List<ChartHisBean> chartHisBeans = MessageManager.getInstance(
						MessageRecordAct.this).getRecentContactsWithLastMsg();
				List<ChartHisBean> data = removeDuplicate(chartHisBeans);
				Message msg = new Message();
				msg.obj = data;
				messagehandler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 
	 * @Title: removeDuplicate
	 * @Description: 去掉list中重复的数据
	 * @param @param list
	 * @param @return 设定文件
	 * @return List 返回类型
	 * @throws
	 */
	public static List<ChartHisBean> removeDuplicate(List<ChartHisBean> list) {

		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = list.size() - 1; j > i; j--) {
				if (list.get(j).equals(list.get(i))) {
					list.remove(j);
				}
			}
		}
		return list;
	}

	Handler messagehandler = new Handler() {

		public void handleMessage(Message msg) {
			@SuppressWarnings("unchecked")
			List<ChartHisBean> list = (List<ChartHisBean>) msg.obj;
			if (null != list && list.size() > 0)
				Collections.sort(list);
			// Log.e("fid", fuid);
			inviteNotices.clear();
			inviteNotices.addAll(list);
			// Log.e("invite.size", inviteNotices.size()+"");
			if (inviteNotices != null) {
				messageAdapter = new MessageUserAdapter(inviteNotices,
						MessageRecordAct.this);
				messageListView.setAdapter(messageAdapter);
			}

			messagePullListView.onRefreshComplete();

		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		receiver = new Receiver();
		IntentFilter filter = new IntentFilter(
				AppContext.REFRESH_MESSAGE_RECORD);
		registerReceiver(receiver, filter);
		getData2();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}

	public void deleteDialog() {
		alertDialog = new AlertDialog.Builder(this)
				.setTitle("提示")
				.setMessage("您确定删除该条信息吗？")
				.setIcon(R.drawable.search_clear_pressed)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						MessageManager.getInstance(MessageRecordAct.this)
								.delChatHisWithSb(
										inviteNotices.get(position).getFrom());
						inviteNotices.remove(position);
						messageAdapter.notifyDataSetChanged();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
					}
				}).create();
		alertDialog.show();
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	class WarnReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (AppContext.SINGLE_CHAT_OPEN_NEW_MESSAGE.equals(action)) {

				IMMessage message = intent.getParcelableExtra("IMMessage");
				fuid = message.getFromSubJid();
			}
		}
	}

	// 删除通知
	private void clearNotification(int type) {
		// 启动后删除之前我们定义的通知
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(type);

	}

	class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (AppContext.REFRESH_MESSAGE_RECORD.equals(action)) {
				getData2();
			}
		}
	}

	@Override
	public void onBackPressed() {
		getParent().onBackPressed();
	}
	
	
	void doChangeBG()
    {

        String[] item = new String[] { "拍一张", "从相册中获取", "取消" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("更换头像");

        /**
         * 
         * 1、public Builder setItems(int itemsId, final OnClickListener
         * 
         * listener) itemsId表示字符串数组的资源ID，该资源指定的数组会显示在列表中。 2、public Builder
         * 
         * setItems(CharSequence[] items, final OnClickListener listener)
         * 
         * items表示用于显示在列表中的字符串数组
         */

        builder.setItems(item, new DialogInterface.OnClickListener()

        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == 0)
                {
                    photo();
                }
                else if (which == 1)
                {
                    pickPhoto();
                }
                else if (which == 2)
                {

                }
            }

        });
        builder.create().show();
    }
 
    public String path;

    public Uri imageUri;

    public void photo()
    {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String filePath = Environment.getExternalStorageDirectory()
                + "/weixiaoyuan/";
        File files = new File(filePath);
        if (!files.exists())
        {
            files.mkdir();
        }
        File file = new File(filePath, String.valueOf(System
                .currentTimeMillis()) + ".jpg");
        path = file.getPath();
        imageUri = Uri.fromFile(file);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(openCameraIntent, 1000);
    }

    private void pickPhoto()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 2000);
    }
    
    String photoFilePath;

    int degree;

    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
            case 1000:
                if (resultCode == Activity.RESULT_OK && null != path)
                {
                    File f = new File(path);
                    if (f.length() == 0)
                    {
                        f.deleteOnExit();
                    }
                    else
                    {
                        // degree = readPictureDegree(imagePath);
                        startPhotoZoom(imageUri);
                    }
                }
                break;
            case 2000:
                if (resultCode == Activity.RESULT_OK && null != data)
                {
                    Uri originalUri = data.getData();
                    if (originalUri != null)
                    {
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

                        if ("".equals(photoFilePath) || photoFilePath == null)
                        {
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
                            if (cursors.moveToFirst())
                            {
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
                if (data != null)
                {
                    setPicToView(data);
                }
                break;

            }
        }
    }
    String pathRe,pats;
    String pathString = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/supertutor";
   
    public void startPhotoZoom(Uri uri)
    {
        /*
         * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
         * yourself_sdk_path/docs/reference/android/content/Intent.html
         * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能, 是直接调本地库的，小马不懂C C++
         * 这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么 制做的了...吼吼
         */
      
       String pathString = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/weixiaoyuan";
        File destDir = new File(pathString);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        pats = pathString+"/"+System.currentTimeMillis()+".jpg";
        pathRe ="file:///sdcard/supertuto/"+System.currentTimeMillis()+".jpg";
       // imageUri = Uri.parse(pathRe);
        Uri imageUri = Uri.parse(pathRe);//The Uri to store the big bitmap
        Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 100);
            intent.putExtra("outputY", 100);
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
    private void setPicToView(Intent picdata)
    {
        Bundle extras = picdata.getExtras();
        if (extras != null)
        {
            //bitmap = extras.getParcelable("data");
//            postFile(MessageRecieveService.APPID, pats);
        }
    }

}
