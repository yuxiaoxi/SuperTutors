package com.yuzhuo.easyupbring.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.duohuo.dhroid.view.megwidget.CircleImageView;

import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.activity.MapActivity;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.bean.IMMessage;
import com.yuzhuo.easyupbring.bean.MessageData;
import com.yuzhuo.easyupbring.service.APIService;
import com.yuzhuo.easyupbring.utils.FaceTextUtils;
import com.yuzhuo.easyupbring.utils.ImageLoader;
import com.yuzhuo.easyupbring.utils.PicturesUtil;
import com.yuzhuo.easyupbring.widget.ViewHolder;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @ClassName: MessageChatAdapter
 * @Description: 单聊适配
 * @author 余卓
 * @date 2014年10月21日 下午2:23:49
 * 
 */
public class MessageChatAdapter extends BaseListAdapter<IMMessage> {

	public final static int TYPE_RECEIVER_TXT = 0;
	public final static int TYPE_SEND_TXT = 1;
	public final static int TYPE_SEND_IMAGE = 2;
	public final static int TYPE_RECEIVER_IMAGE = 3;

	public final static int TYPE_SEND_LOCATION = 4;
	public final static int TYPE_RECEIVER_LOCATION = 5;

	public final static int TYPE_SEND_RECORD = 6;
	public final static int TYPE_RECEIVER_RECORD = 7;

	public final static int TYPE_SEND_INVITE = 8;
	public final static int TYPE_RECEIVER_INVITE = 9;

	public final static int TYPE_INFORMATION = 10;
	MediaPlayer mediaPlayer;
	String currentObjectId = "";
	private View popView;
	private PopupWindow popupWindow;
	private ImageView bigavatarImageView;
	private AnimationDrawable animationDrawable;

	Context mContext;

	private ImageLoader imageLoader;

	public MessageChatAdapter(Context context, List<IMMessage> msgList) {
		// TODO Auto-generated constructor stub
		super(context, msgList);
		this.mContext = context;
		imageLoader = ImageLoader.getIncetence(context);

	}

	@Override
	public int getItemViewType(int position) {
		IMMessage msg = list.get(position);
		Log.e("MJJ", msg.getType() + "            ====>");
		int type = msg.getType();
		if (type == MessageData.TYPE_TEXT) {
			return msg.getMsgType() == 1 ? TYPE_SEND_TXT : TYPE_RECEIVER_TXT;
		} else if (type == MessageData.TYPE_RECORD) {
			return msg.getMsgType() == 1 ? TYPE_SEND_RECORD
					: TYPE_RECEIVER_RECORD;
		} else if (type == MessageData.TYPE_IMAGE) {
			return msg.getMsgType() == 1 ? TYPE_SEND_IMAGE
					: TYPE_RECEIVER_IMAGE;
		} else if (type == MessageData.TYPE_LOCATION) {
			return msg.getMsgType() == 1 ? TYPE_SEND_LOCATION
					: TYPE_RECEIVER_LOCATION;
		} else if (type == MessageData.TYPE_INFOMATION) {
			return TYPE_INFORMATION;
		}
		return -1;

		// if(msg.getMsgType()==BmobConfig.TYPE_TEXT){
		// return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_TXT:
		// TYPE_RECEIVER_TXT;
		// }else if(msg.getMsgType()==BmobConfig.TYPE_IMAGE){
		// return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_IMAGE:
		// TYPE_RECEIVER_IMAGE;
		// }else if(msg.getMsgType()==BmobConfig.TYPE_LOCATION){
		// return msg.getBelongId().equals(currentObjectId) ?
		// TYPE_SEND_LOCATION: TYPE_RECEIVER_LOCATION;
		// }
		// return -1;
	}

	@Override
	public int getViewTypeCount() {
		return 9;
	}

	private View createViewByType(IMMessage message, int position) {

		int type = message.getType();
		// UIHelper.logDebug("MJJ", "createViewByType   "+type);
		if (type == MessageData.TYPE_TEXT) {
			return getItemViewType(position) == TYPE_RECEIVER_TXT ? mInflater
					.inflate(R.layout.item_chat_received_message, null)
					: mInflater.inflate(R.layout.item_chat_sent_message, null);
			// return mInflater.inflate(R.layout.item_chat_sent_message, null);
		} else if (type == MessageData.TYPE_RECORD) {
			return getItemViewType(position) == TYPE_RECEIVER_RECORD ? mInflater
					.inflate(R.layout.item_chat_received_record, null)
					: mInflater.inflate(R.layout.item_chat_sent_record, null);
			// return mInflater.inflate(R.layout.item_chat_sent_record, null);
		} else if (type == MessageData.TYPE_IMAGE) {
			return getItemViewType(position) == TYPE_RECEIVER_IMAGE ? mInflater
					.inflate(R.layout.item_chat_received_image, null)
					: mInflater.inflate(R.layout.item_chat_sent_image, null);
			// return mInflater.inflate(R.layout.item_chat_sent_image, null);
		} else if (type == MessageData.TYPE_LOCATION) {
			return getItemViewType(position) == TYPE_RECEIVER_LOCATION ? mInflater
					.inflate(R.layout.item_chat_received_location, null)
					: mInflater.inflate(R.layout.item_chat_sent_location, null);
			// return mInflater.inflate(R.layout.item_chat_sent_location, null);
		} else if (type == MessageData.TYPE_INFOMATION) {
//			return mInflater.inflate(R.layout.item_chat_information, null);
		}
		return null;

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public View bindView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final IMMessage item = list.get(position);
		// UIHelper.logDebug("MJJ", "item   "+item.getType()+"   "+list.size());
		if (convertView == null) {
			convertView = createViewByType(item, position);
			// AppContext.chatAvatar.put(position,item.getPicPath());

		}
		// 文本类型
		CircleImageView iv_avatar = (CircleImageView) convertView
				.findViewById(R.id.iv_avatar);
		TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
		TextView tv_message = ViewHolder.get(convertView, R.id.tv_message);
		// 语音
		ImageView iv_recode = ViewHolder.get(convertView, R.id.iv_record);
		TextView iv_soundTime = ViewHolder.get(convertView, R.id.sound_time);
		if (iv_recode != null) {
			animationDrawable = (AnimationDrawable) iv_recode.getDrawable();
			animationDrawable.stop();
		}
		// final ProgressBar progress_load = ViewHolder.get(convertView,
		// R.id.progress_load);//进度条
		// 图片
		ImageView iv_picture = ViewHolder.get(convertView, R.id.iv_picture);

		// 位置
		TextView tv_location = ViewHolder.get(convertView, R.id.tv_location);
		ImageView img_location = ViewHolder.get(convertView, R.id.img_location);
		RelativeLayout layout_location = ViewHolder.get(convertView,
				R.id.layout_location);

//		TextView iv_information = ViewHolder.get(convertView,
//				R.id.tv_information);
		TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
		final String messageTime = item.getTime();
		if (item.getType() != MessageData.TYPE_INFOMATION) {
			String name = item.getName();
			if (name != null && name.contains(",")) {
				name = name.split(",")[0];
			}
			if (name != null && !name.equals("")) {
				if (item.getMsgType() == 1
						&& !item.getFromSubJid().contains("@conference")) {
					tv_name.setVisibility(View.GONE);
				} else {
					tv_name.setVisibility(View.VISIBLE);
					tv_name.setText(name);
				}
			}
			String avatar = item.getPicPath();
			// Log.e("Avater", avatar);
			if (avatar != null && avatar.contains(",") && !avatar.equals(",")) {

				if (item.getMsgType() == 1) {
					avatar = AppContext.AVATAR;
				} else {
					avatar = avatar.split(",")[0];
				}
			}

			imageLoader.displayImage(avatar, iv_avatar);
			if (iv_avatar != null) {
				iv_avatar.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (item.getMsgType() == 2) {
							//点击图像查看个人信息
//							Intent intent = new Intent(mContext,
//									ParentInfoAct.class);
//							intent.putExtra("iUid", item.getFromUid());
//							intent.putExtra("iUserType", "3");
//							mContext.startActivity(intent);
						}

					}
				});
			}
			if (item.getTime() != null) {
				if (position != 0
						&& list.get(position - 1).getType() != MessageData.TYPE_INFOMATION) {
					String time = list.get(position - 1).getTime();
					String times = item.getTime();
					if (Long.parseLong(times) - Long.parseLong(time) < 600000) {
						tv_time.setVisibility(View.GONE);
					} else {
						tv_time.setVisibility(View.VISIBLE);
						tv_time.setText(getTime(item.getTime()));
					}
					// SimpleDateFormat sdf = new SimpleDateFormat(
					// AppContext.MS_FORMART);
					// try {
					// Date timeDate = sdf.parse(time);
					// Date timesDate = sdf.parse(times);
					// Log.e("MJJ",
					// "时间："
					// + (timeDate.getTime() - timesDate
					// .getTime()));
					// if (timesDate.getTime() - timeDate.getTime() < 600000) {
					// tv_time.setVisibility(View.GONE);
					// } else {
					// tv_time.setVisibility(View.VISIBLE);
					// tv_time.setText(item.getTime().split(",")[0]);
					// }
					// } catch (ParseException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
				} else {
					tv_time.setVisibility(View.VISIBLE);
					tv_time.setText(getTime(item.getTime()));
				}
			}
		}
		final String text = item.getContent();
		switch (item.getType()) {
		case MessageData.TYPE_TEXT:
			try {
				SpannableString spannableString = FaceTextUtils
						.toSpannableString(mContext, text);
				tv_message.setText(spannableString);
			} catch (Exception e) {
			}
			break;

		case MessageData.TYPE_RECORD:

			if (item.getMsgType() == 1) {
				if (text.contains(",")) {
					iv_soundTime.setText(text.split(",")[1] + "s");
				}

				iv_recode.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						try {
							byte[] bitmapArray;
							if (text.contains(",")) {
								bitmapArray = Base64.decode(text.split(",")[0],
										Base64.NO_WRAP);
							} else {
								bitmapArray = Base64.decode(text,
										Base64.NO_WRAP);
							}
							File tempMp3 = File.createTempFile("yuexsound",
									"amr", mContext.getCacheDir());
							tempMp3.deleteOnExit();
							FileOutputStream fos = new FileOutputStream(tempMp3);
							fos.write(bitmapArray);
							fos.close();
							// animationDrawable.start();
							// Tried reusing instance of media player
							// but that resulted in system crashes...
							if (mediaPlayer == null) {
								mediaPlayer = new MediaPlayer();
							} else {
								animationDrawable.stop();
								mediaPlayer.reset();
							}
							if (!mediaPlayer.isPlaying()) {
								animationDrawable = (AnimationDrawable) ((ImageView) v)
										.getDrawable();
								animationDrawable.start();
								// Tried passing path directly, but kept getting
								// "Prepare failed.: status=0x1"
								// so using file descriptor instead
								FileInputStream fis = new FileInputStream(
										tempMp3);
								mediaPlayer.setDataSource(fis.getFD());
								mediaPlayer.prepare();
								mediaPlayer.start();
								mediaPlayer
										.setOnCompletionListener(new OnCompletionListener() {

											@Override
											public void onCompletion(
													MediaPlayer arg0) {
												// TODO Auto-generated method
												// stub
												animationDrawable.stop();
											}
										});
							} else {
								Toast.makeText(mContext, "已经有语音在播放，请稍等...",
										Toast.LENGTH_SHORT).show();
							}
						} catch (IOException e) {
							Log.e("MJJ", "播放失败");
						}

					}

				});

			} else if (item.getMsgType() == 2) {
				if (text.contains(",")) {
					iv_soundTime.setText(text.split(",")[1] + "s");
				}

				iv_recode.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						if (mediaPlayer == null) {
							mediaPlayer = new MediaPlayer();
						} else {
							animationDrawable.stop();
							mediaPlayer.reset();
						}
						if (!mediaPlayer.isPlaying()) {
							animationDrawable = (AnimationDrawable) ((ImageView) v)
									.getDrawable();
							animationDrawable.start();
							// Tried passing path directly, but kept getting
							// "Prepare failed.: status=0x1"
							// so using file descriptor instead

							try {
								mediaPlayer.setDataSource(text.split(",")[0]);
								mediaPlayer.prepare();
								mediaPlayer.start();
								mediaPlayer
										.setOnCompletionListener(new OnCompletionListener() {

											@Override
											public void onCompletion(
													MediaPlayer arg0) {
												// TODO Auto-generated method
												// stub
												animationDrawable.stop();
											}
										});
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (SecurityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						} else {
							Toast.makeText(mContext, "已经有语音在播放，请稍等...",
									Toast.LENGTH_SHORT).show();
						}
					}
				});

			}

			break;

		case MessageData.TYPE_IMAGE:// 图片类
			try {
				if (text != null && !text.equals("")) {
					if (item.getMsgType() == 1) {
						Bitmap bitmap = PicturesUtil.stringtoBitmap(text);
						iv_picture.setImageBitmap(bitmap);
					} else if (item.getMsgType() == 2) {
						WindowManager wm = (WindowManager) mContext
								.getSystemService(Context.WINDOW_SERVICE);
						int width = wm.getDefaultDisplay().getWidth();
						Log.e("image_url", text);
						int x = width - APIService.dip2px(mContext, 70);
						if (Integer.parseInt(text.split(",")[1]) > x) {
							iv_picture
									.setLayoutParams(new RelativeLayout.LayoutParams(
											x, ((Integer.parseInt(text
													.split(",")[2])) * x)
													/ Integer.parseInt(text
															.split(",")[1])));
						} else {
							iv_picture
									.setLayoutParams(new RelativeLayout.LayoutParams(
											Integer.parseInt(text.split(",")[1]),
											Integer.parseInt(text.split(",")[2])));
						}

						// iv_picture.setPadding(APIService.dip2px(mContext,
						// 17), APIService.dip2px(mContext, 10),
						// APIService.dip2px(mContext, 10),
						// APIService.dip2px(mContext, 30));
						iv_picture.setX(APIService.dip2px(mContext, 60));
						Log.e("xiaoxi", APIService.dip2px(mContext, 60) + "");
						iv_picture.setY(APIService.dip2px(mContext, 25));
						imageLoader
								.displayImage(text.split(",")[0], iv_picture);
					}

				}
				popView = LayoutInflater.from(mContext).inflate(
						R.layout.popwin_big_avatar, null);
				if (item.getMsgType() == 2) {
					iv_picture.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// 点击放大图片事件

							bigavatarImageView = (ImageView) popView
									.findViewById(R.id.popwin_big_avatar);
							popupWindow = new PopupWindow(popView,
									LayoutParams.FILL_PARENT,
									LayoutParams.FILL_PARENT, true);
							popupWindow
									.setBackgroundDrawable(new BitmapDrawable());
							WindowManager wm = (WindowManager) mContext
									.getSystemService(Context.WINDOW_SERVICE);

							int width = wm.getDefaultDisplay().getWidth();
							int heigth = wm.getDefaultDisplay().getHeight();
							LayoutParams para = new LinearLayout.LayoutParams(
									android.view.ViewGroup.LayoutParams.FILL_PARENT,
									android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
							para.height = ((Integer.parseInt(text.split(",")[2])) * width)
									/ Integer.parseInt(text.split(",")[1]);
							para.width = width;
							bigavatarImageView.setLayoutParams(para);
							bigavatarImageView.setY((heigth - para.height) / 2);
							imageLoader.displayImage(text.split(",")[0],
									bigavatarImageView);
							// 设置点击窗口外边窗口消失
							popupWindow.setOutsideTouchable(true);
							// 设置此参数获得焦点，否则无法点击
							popupWindow.setFocusable(true);
							if (popupWindow.isShowing()) {
								// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
								popupWindow.dismiss();
							} else {
								// 显示窗口
								popupWindow.showAtLocation(popView,
										Gravity.CENTER, 0, 0);
								popupWindow.showAsDropDown(v);

							}
						}
					});
				}
				LinearLayout lin = (LinearLayout) popView
						.findViewById(R.id.popwin_linear_avatar);
				lin.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						if (popupWindow.isShowing()) {
							// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
							popupWindow.dismiss();
						}
					}
				});
			} catch (Exception e) {
			}
			break;

		case MessageData.TYPE_LOCATION:// 位置信息
			try {
				if (text != null && !text.equals("")) {
					final String address = text.split(",")[0];
					final String latitude = text.split(",")[2];
					final String longtitude = text.split(",")[1];
					final String bitmapStr = text.split(",")[3];
					if (item.getMsgType() == 1) {
						Bitmap bitmap = PicturesUtil.stringtoBitmap(bitmapStr);
						img_location.setImageBitmap(bitmap);
					} else if (item.getMsgType() == 2) {
						Log.e("image_url", text);
						imageLoader.displayImage(bitmapStr, img_location);
					}
					// img_location.setBackgroundDrawable(new BitmapDrawable(
					// PicturesUtil.stringtoBitmap(bitmapStr)));
					// img_location.setImageBitmap(PersonalService.stringtoBitmap(bitmapStr));
					tv_location.setText(address);
					layout_location.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							Intent intent = new Intent(mContext,
									MapActivity.class);
							intent.putExtra("map_x", latitude);
							intent.putExtra("map_y", longtitude);
							intent.putExtra("address", address);
							mContext.startActivity(intent);
						}
					});
				}
			} catch (Exception e) {

			}
			break;

//		case MessageData.TYPE_INFOMATION:
//			iv_information.setText(text);
//			break;
		default:
			break;
		}

		return convertView;
	}

	private String getTime(String time) {
		String re_StrTime = null;
		SimpleDateFormat sdf = null;
		if (time.equals("")) {
			return "";
		}
		sdf = new SimpleDateFormat(AppContext.MS_FORMART);
		long loc_time = Long.valueOf(time);
		re_StrTime = sdf.format(new Date(loc_time));
		return re_StrTime;
	}


}
