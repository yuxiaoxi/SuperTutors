package com.yuzhuo.easyupbring.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.yuzhuo.easyupbring.app.MessageManager;
import com.yuzhuo.easyupbring.bean.IMMessage;
import com.yuzhuo.easyupbring.utils.MyProperUtil;

import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.JSONUtil;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

/**
 * 
 * @ClassName: APIService
 * @Description: 接口处理服务类
 * @author 余卓
 * @date 2014年11月3日 下午3:59:06
 * 
 */
public class APIService {

	private Context context;
	public static String msg;
	public static String imagePath;
	public static Bitmap mBitmap;
	public static int type = 0;
	public static String height;
	public static String width;

	public APIService(Context context) {
		this.context = context;
	}

	/**
	 * 
	 * @Title: postMsg
	 * @Description: 发送纯文本
	 * @param @param iUid
	 * @param @param iTuid
	 * @param @param sContent
	 * @param @param iType 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void postMsg(String iUid, String iTuid, String sContent,
			String iType, String width, String heigth, String seconds) {
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty(
						"createmsginfo")
				+ "&data="
				+ makeSingleParam(iUid, iTuid, sContent, iType, seconds, "",
						"0", "0", width, heigth);
		DhNet net = new DhNet(url);
		// 添加参数
		// net.addParam("data", makeParameters(uid, type, page,num));
		Log.e("XIaoxi", "notice ulr = " + url);
		net.doGet(new NetTask(context) {

			@Override
			public void doInUI(Response response, Integer transfer) {

			}
		});

	}

	public void postGMsg(String iUid, String iGtype, String iTgid,
			String sContent, String iType) {
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host1")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty(
						"androidGroupChat")
				+ "&data="
				+ makeGroupParameters(iUid, iTgid, sContent, iType, iGtype,
						"0", "", "0", "0", "0", "0");
		DhNet net = new DhNet(url);
		// 添加参数
		// net.addParam("data", makeParameters(uid, type, page,num));
		Log.e("XIaoxi", "notice ulr = " + url);
		net.doGet(new NetTask(context) {

			@Override
			public void doInUI(Response response, Integer transfer) {

			}
		});

	}

	/**
	 * 
	 * @Title: postMsgR
	 * @Description: 发送语音文本
	 * @param @param iUid
	 * @param @param iTuid
	 * @param @param sContent
	 * @param @param iType
	 * @param @param iSeconds 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void postMsgR(String iUid, String iTuid, String sContent,
			String iType, String iSeconds) {
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host1")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty("singleChat")
				+ "&data="
				+ makeSingleParam(iUid, iTuid, sContent, iType, iSeconds, "",
						"0", "0", "0", "0");
		DhNet net = new DhNet(url);
		// 添加参数
		// net.addParam("data", makeParameters(uid, type, page,num));
		Log.e("XIaoxi", "notice ulr = " + url);
		net.doGet(new NetTask(context) {

			@Override
			public void doInUI(Response response, Integer transfer) {

			}
		});
	}

	public void postGMsgR(String iUid, String iGtype, String iTgid,
			String sContent, String iType, String iSeconds) {
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host1")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty(
						"androidGroupChat")
				+ "&data="
				+ makeGroupParameters(iUid, iTgid, sContent, iType, iGtype,
						iSeconds, "", "0", "0", "0", "0");
		DhNet net = new DhNet(url);
		// 添加参数
		// net.addParam("data", makeParameters(uid, type, page,num));
		Log.e("XIaoxi", "notice ulr = " + url);
		net.doGet(new NetTask(context) {

			@Override
			public void doInUI(Response response, Integer transfer) {

			}
		});

	}

	/**
	 * 
	 * @Title: postMsgI
	 * @Description: 发送图片文本
	 * @param @param iUid
	 * @param @param iTuid
	 * @param @param sContent
	 * @param @param iType
	 * @param @param width
	 * @param @param height 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void postMsgI(String iUid, String iTuid, String sContent,
			String iType, String width, String height) {
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host1")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty("singleChat")
				+ "&data="
				+ makeSingleParam(iUid, iTuid, sContent, iType, "0", "", "0",
						"0", width, height);
		DhNet net = new DhNet(url);
		// 添加参数
		// net.addParam("data", makeParameters(uid, type, page,num));
		Log.e("XIaoxi", "notice ulr = " + url);
		net.doGet(new NetTask(context) {

			@Override
			public void doInUI(Response response, Integer transfer) {

			}
		});

	}

	public void postGMsgI(String iUid, String iGtype, String iTgid,
			String sContent, String iType, String width, String height) {
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host1")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty(
						"androidGroupChat")
				+ "&data="
				+ makeGroupParameters(iUid, iTgid, sContent, iType, iGtype,
						"0", "", "0", "0", width, height);
		DhNet net = new DhNet(url);
		// 添加参数
		// net.addParam("data", makeParameters(uid, type, page,num));
		Log.e("XIaoxi", "notice ulr = " + url);
		net.doGet(new NetTask(context) {

			@Override
			public void doInUI(Response response, Integer transfer) {

			}
		});

	}

	/**
	 * 
	 * @Title: postMsgL
	 * @Description: 发送位置文本
	 * @param @param iUid
	 * @param @param iTuid
	 * @param @param sContent
	 * @param @param iType
	 * @param @param addr
	 * @param @param latitude
	 * @param @param longitude 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void postMsgL(String iUid, String iTuid, String sContent,
			String iType, String addr, String latitude, String longitude) {
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host1")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty("singleChat")
				+ "&data="
				+ makeSingleParam(iUid, iTuid, sContent, iType, "0", addr,
						latitude, longitude, "0", "0");
		DhNet net = new DhNet(url);
		// 添加参数
		// net.addParam("data", makeParameters(uid, type, page,num));
		Log.e("XIaoxi", "notice ulr = " + url);
		net.doGet(new NetTask(context) {

			@Override
			public void doInUI(Response response, Integer transfer) {

			}
		});

	}

	public void postGMsgL(String iUid, String iGtype, String iTgid,
			String sContent, String iType, String addr, String latitude,
			String longitude) {
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("Host1")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty(
						"androidGroupChat")
				+ "&data="
				+ makeGroupParameters(iUid, iTgid, sContent, iType, iGtype,
						"0", addr, latitude, longitude, "0", "0");
		DhNet net = new DhNet(url);
		// 添加参数
		// net.addParam("data", makeParameters(uid, type, page,num));
		Log.e("XIaoxi", "notice ulr = " + url);
		net.doGet(new NetTask(context) {

			@Override
			public void doInUI(Response response, Integer transfer) {

			}
		});

	}

	/**
	 * 
	 * @Title: postRecord
	 * @Description: 推送语音文件
	 * @param @param appid
	 * @param @param pathurl
	 * @param @param iUid
	 * @param @param iTuid
	 * @param @param iType
	 * @param @param newImMessage 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void postRecord(String appid, String pathurl, final String iUid,
			final String iTuid, final String iType, final IMMessage newImMessage) {
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("FileHost")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty("uploadFile");
		DhNet net = new DhNet(url);
		Log.e("url", url);
		// 添加参数
		// net.addParam("data", makeParameters(uid, type, page,num));
		File file = new File(pathurl);
		net.addFile("fileTest", file).upload(new NetTask(context) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				if ("12".equals(JSONUtil.getString(response.jSON(), "code"))) {
					Log.e("returnstring", response.plain());
					String pathString = "http://www.bianbiangou.com/" + "appimg/"
							+JSONUtil.getString(response.jSON(),
							"data.picture");
					postMsg(iUid, iTuid, pathString, iType, "0", "0",
							newImMessage.getContent().split(",")[1]);
					MessageManager.getInstance(context).saveIMMessage(
							newImMessage);

				}

			}
		});

	}

	public void postGRecord(String appid, String pathurl, final String iUid,
			final String iGtype, final String iTgid, final String iType,
			final IMMessage newImMessage) {
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("FileHost")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty("fileserver");
		DhNet net = new DhNet(url);
		Log.e("url", url);
		// 添加参数
		// net.addParam("data", makeParameters(uid, type, page,num));
		File file = new File(pathurl);
		net.addParam("appid", appid).addFile("content", file)
				.upload(new NetTask(context) {

					@Override
					public void doInUI(Response response, Integer transfer) {
						if (response.isSuccess()) {
							Boolean uploading = response.getBundle("uploading");
							if (!uploading) {
								Log.e("上传完成", "上传完成");
								Log.e("responseString--->", response.plain());
								JSONObject json;

								try {
									json = new JSONObject(JSONUtil.getString(
											response.jSON(), "data.info.self"));
									postGMsgR(iUid, iGtype, iTgid, json
											.getString("url"), iType,
											newImMessage.getContent()
													.split(",")[1]);
									MessageManager.getInstance(context)
											.saveIMMessage(newImMessage);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							} else {
								// 已上传大小

								// 文件总大小

							}
						}

					}
				});

	}

	/**
	 * 
	 * @Title: postFile
	 * @Description: 往服务器推送文件
	 * @param @param appid
	 * @param @param pathurl
	 * @param @param iUid
	 * @param @param iTuid
	 * @param @param iType
	 * @param @param newImMessage
	 * @param @param type
	 * @param @param addr
	 * @param @param latitude
	 * @param @param longitude
	 * @param @param width
	 * @param @param height 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void postFile(String appid, String pathurl, final String iUid,
			final String iTuid, final String iType,
			final IMMessage newImMessage, final int type, final String addr,
			final String latitude, final String longitude, final String width,
			final String height) {
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("FileHost")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty("uploadFile");
		DhNet net = new DhNet(url);
		Log.e("posturl", url);
		// 添加参数
		// net.addParam("data", makeParameters(uid, type, page,num));
		File file = new File(pathurl);
		net.addFile("fileTest", getimage(pathurl, file)).upload(
				new NetTask(context) {

					@Override
					public void doInUI(Response response, Integer transfer) {
						if ("12".equals(JSONUtil.getString(response.jSON(),
								"code"))) {
							Log.e("returnstring", response.plain());
							String pathString = "http://www.bianbiangou.com/" + "appimg/"
									+JSONUtil.getString(
									response.jSON(), "data.picture");

							postMsg(iUid, iTuid, pathString, iType, width,
									height, "0");

							MessageManager.getInstance(context).saveIMMessage(
									newImMessage);

						}

					}
				});

	}

	public void postGFile(String appid, String pathurl, final String iUid,
			final String iGtype, final String iTgid, final String iType,
			final IMMessage newImMessage, final int type, final String addr,
			final String latitude, final String longitude, final String width,
			final String height) {
		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("FileHost")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty("fileserver");
		DhNet net = new DhNet(url);
		Log.e("posturl", url);
		// 添加参数
		// net.addParam("data", makeParameters(uid, type, page,num));
		File file = new File(pathurl);
		net.addParam("appid", appid)
				.addFile("content", getimage(pathurl, file))
				.upload(new NetTask(context) {

					@Override
					public void doInUI(Response response, Integer transfer) {
						if (response.isSuccess()) {
							Boolean uploading = response.getBundle("uploading");
							if (!uploading) {
								Log.e("上传完成", "上传完成");
								Log.e("responseString--->", response.plain());
								JSONObject json;

								try {
									json = new JSONObject(JSONUtil.getString(
											response.jSON(), "data.info.self"));
									if (type == 1) {
										postGMsgI(iUid, iGtype, iTgid,
												json.getString("url"), iType,
												width, height);
									} else {
										postGMsgL(iUid, iGtype, iTgid,
												json.getString("url"), iType,
												addr, latitude, longitude);
									}

									MessageManager.getInstance(context)
											.saveIMMessage(newImMessage);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							} else {
								// 已上传大小

								// 文件总大小

							}
						}

					}
				});

	}

	/**
	 * 
	 * @Title: getimage
	 * @Description: 图片等比便缩放
	 * @param @param srcPath
	 * @param @param file
	 * @param @return 设定文件
	 * @return File 返回类型
	 * @throws
	 */

	private File getimage(String srcPath, File file) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 640f;// 这里设置高度为800f
		float ww = 360f;// 这里设置宽度为480f
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
		return compressImage(bitmap, file);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 
	 * @Title: compressImage
	 * @Description: 图片质量压缩
	 * @param @param image
	 * @param @param file
	 * @param @return 设定文件
	 * @return File 返回类型
	 * @throws
	 */
	private File compressImage(Bitmap image, File file) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 50) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		// ByteArrayInputStream isBm = new
		// ByteArrayInputStream(baos.toByteArray());//
		// 把压缩后的数据baos存放到ByteArrayInputStream中
		// Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//
		// 把ByteArrayInputStream数据生成图片

		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baos.toByteArray());
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 
	 * @Title: makeSingleParam
	 * @Description: 单聊文本转换成base64
	 * @param @param iUid
	 * @param @param iTuid
	 * @param @param sContent
	 * @param @param iType
	 * @param @param iSeconds
	 * @param @param sAddr
	 * @param @param latitude
	 * @param @param longitude
	 * @param @param iWidth
	 * @param @param iHeight
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public String makeSingleParam(String iUid, String iTuid, String sContent,
			String iType, String iSeconds, String sAddr, String latitude,
			String longitude, String iWidth, String iHeight) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("msg_from", iUid);
		map.put("msg_to", iTuid);
		map.put("msg_content", sContent);
		map.put("content_type", iType);
		map.put("msg_seconds", iSeconds);
		map.put("msg_width", iWidth);
		map.put("msg_height", iHeight);
		JSONObject arr = null;
		String parameters;
		try {
			arr = new JSONObject(map);
			Log.e("json-->", arr.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			parameters = new String(Base64.encode(arr.toString().getBytes(),
					Base64.NO_WRAP));
			return URLEncoder.encode(parameters, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;

	}

	public String makeGroupParameters(String iUid, String iTgid,
			String sContent, String iType, String sGtype, String iSeconds,
			String sAddr, String latitude, String longitude, String iWidth,
			String iHeight) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("iUid", iUid);

		map.put("iTgid", iTgid);
		map.put("sContent", sContent);
		map.put("iType", iType);
		map.put("sGtype", sGtype);
		map.put("iSeconds", iSeconds);
		map.put("sAddr", sAddr);
		map.put("iLatitude", latitude);
		map.put("iLongitude", longitude);
		map.put("iWidth", iWidth);
		map.put("iHeight", iHeight);
		JSONObject arr = null;
		String parameters;
		try {
			arr = new JSONObject(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			parameters = new String(Base64.encode(arr.toString().getBytes(),
					Base64.NO_WRAP));
			return URLEncoder.encode(parameters, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;

	}

	public void postFile(String path, final Handler handle) {

		String url = MyProperUtil.getProperties(context,
				"appConfigDebugHost.properties").getProperty("FileHost")
				+ MyProperUtil.getProperties(context,
						"appConfigDebug.properties").getProperty("uploadFile");
		DhNet net = new DhNet(url);
		Log.e("url", url);
		// 添加参数
		net.addFile("fileTest", new File(path)).upload(new NetTask(context) {

			@Override
			public void doInUI(Response response, Integer transfer) {
				if ("12".equals(JSONUtil.getString(response.jSON(), "code"))) {
					Log.e("returnstring", response.plain());
					String pathString = JSONUtil.getString(response.jSON(),
							"data.picture");
					Message msg = new Message();
					msg.obj = pathString;
					handle.sendMessage(msg);
				}
			}
		});
	}

	/**
	 * 将dp转成像素
	 * 
	 * @Title: dip2px
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param context
	 * @param @param dpValue
	 * @param @return 设定文件
	 * @return int 返回类型
	 * @throws
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

}
