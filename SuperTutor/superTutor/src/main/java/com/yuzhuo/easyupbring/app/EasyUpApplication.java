package com.yuzhuo.easyupbring.app;

import net.duohuo.dhroid.Dhroid;
import net.duohuo.dhroid.dialog.DialogImpl;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.Instance.InstanceScope;
import net.duohuo.dhroid.ioc.IocContainer;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.yuzhuo.easyupbring.R;

/**
 * 
 * @ClassName: EasyUpApplication
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 余卓
 * @date 2014年12月24日 上午11:44:12
 * 
 */
public class EasyUpApplication extends Application {

	public LocationClient mLocationClient = null;
	public GeofenceClient mGeofenceClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	public TextView addTv, laTv, lonTv;
	private String address;
	private String latitude;
	private String lontitude;
	public NotifyLister mNotifyer = null;
	public Vibrator mVibrator01;
	public static String TAG = "YueX";
	public boolean m_bKeyRight = true;
	private static EasyUpApplication mInstance = null;
	// "1Vd5bwU82SXHGaVj6O3WZYaR";//打包用的AppKey
	public static final String strKey = "cjVh4B0WyEHmE0bAYeP0Ok4d";
	public BMapManager mBMapManager = null;
	private String province;
	private String city;
	public static DisplayImageOptions defaultOptions;

	@Override
	public void onCreate() {
		super.onCreate();
		Dhroid.init(this);

		// 对话框对象
		IocContainer.getShare().bind(DialogImpl.class).to(IDialog.class)
		// 这是单例
				.scope(InstanceScope.SCOPE_SINGLETON);
		ShareSDK.initSDK(this, "55624bee13ed"); // init shareSDK
		mLocationClient = new LocationClient(this);
		mInstance = this;
		mLocationClient.setAK(strKey);
		mLocationClient.registerLocationListener(myListener);
		mGeofenceClient = new GeofenceClient(this);
		// //位置提醒相关代码
		// mNotifyer = new NotifyLister();
		// mNotifyer.SetNotifyLocation(40.047883,116.312564,3000,"gps");//4个参数代表要位置提醒的点的坐标，具体含义依次为：纬度，经度，距离范围，坐标系类型(gcj02,gps,bd09,bd09ll)
		// mLocationClient.registerNotify(mNotifyer);
		// 这个基本不需要,主要用于被注入的对象的属性也是注入时,可以注入的包
		// String[] pcks={"net.duohuo.xxxx"};
		// Const.ioc_instal_pkg=pcks;

		/***** 下面是测试的对象相互依赖的注入问题与配置无关 ******/

		defaultOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher) // 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.ic_launcher)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.ic_launcher) // 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.build();// 构建完成
		/*
		 * ImageLoaderConfiguration config = new
		 * ImageLoaderConfiguration.Builder( getApplicationContext())
		 * .threadPoolSize(3) // 线程池内加载的数量 .threadPriority(Thread.NORM_PRIORITY
		 * - 2) .denyCacheImageMultipleSizesInMemory()
		 * .defaultDisplayImageOptions(defaultOptions) .memoryCacheSize(3 * 1024
		 * * 1024) .discCacheSize(100 * 1024 * 1024)//
		 * .discCacheFileCount(100)// 缓存一百张图片 .writeDebugLogs().build();
		 * ImageLoader.getInstance().init(config);
		 */

	}

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(strKey, new MyGeneralListener())) {
			Toast.makeText(
					EasyUpApplication.getInstance().getApplicationContext(),
					"BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 监听函数，有更新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			StringBuffer sb = new StringBuffer(256);
			sb.append(location.getLatitude());
			setLatitude(sb.toString());
			StringBuffer sb1 = new StringBuffer(256);
			sb1.append(location.getLongitude());
			setLontitude(sb1.toString());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {

			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				/**
				 * 格式化显示地址信息
				 */
				setAddress(location.getAddrStr());
				setCity(location.getCity());
				setProvince(location.getProvince());
			}

		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append(poiLocation.getLatitude());
			setLatitude(String
					.valueOf((int) (Double.valueOf(sb.toString()) * 1e6 + 5640) / 1e6));
			StringBuffer sb1 = new StringBuffer(256);
			sb1.append(poiLocation.getLongitude());
			setLontitude(String
					.valueOf((int) (Double.valueOf(sb1.toString()) * 1e6 + 6640) / 1e6));

			if (poiLocation.hasPoi()) {
				StringBuffer sb2 = new StringBuffer(256);
				sb2.append(poiLocation.getAddrStr());
				setAddress(sb2.toString() + poiLocation.getPoi());
			} else {
				StringBuffer sb2 = new StringBuffer(256);
				sb2.append(poiLocation.getAddrStr());
				setAddress(sb2.toString());
			}
		}
	}

	public class NotifyLister extends BDNotifyListener {
		public void onNotify(BDLocation mlocation, float distance) {
			mVibrator01.vibrate(1000);
		}
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLontitude() {
		return lontitude;
	}

	public void setLontitude(String lontitude) {
		this.lontitude = lontitude;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public static EasyUpApplication getInstance() {
		return mInstance;
	}

	public static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Toast.makeText(
						EasyUpApplication.getInstance().getApplicationContext(),
						"您的网络出错啦！", Toast.LENGTH_LONG).show();
			} else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Toast.makeText(
						EasyUpApplication.getInstance().getApplicationContext(),
						"输入的地址不正确，请输入正确地址！", Toast.LENGTH_LONG).show();
			}
			// ...
		}

		@Override
		public void onGetPermissionState(int iError) {
			// 非零值表示key验证未通过
			if (iError != 0) {
				// 授权Key错误：
				Toast.makeText(
						EasyUpApplication.getInstance().getApplicationContext(),
						"请在相关文件中输入正确的授权Key,并检查您的网络连接是否正常！error: " + iError,
						Toast.LENGTH_LONG).show();
				EasyUpApplication.getInstance().m_bKeyRight = false;
			} else {
				EasyUpApplication.getInstance().m_bKeyRight = true;
			}
		}
	}

}
