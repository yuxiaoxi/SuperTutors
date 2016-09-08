package com.yuzhuo.easyupbring.activity;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.map.TransitOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionInfo;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.yuzhuo.easyupbring.R;
import com.yuzhuo.easyupbring.adapter.PoiListAdapter;
import com.yuzhuo.easyupbring.app.AppContext;
import com.yuzhuo.easyupbring.app.EasyUpApplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
* @ClassName: MapActivity 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author 余卓 
* @date 2014年12月24日 下午5:26:55 
*
 */
public class MapActivity extends BaseAct {
	private Toast mToast;
	final static String TAG = "Activity";
	// 弹出泡泡图层，浏览节点时使用
	public static PopupOverlay mPopupOverlay = null;
	// MapView 是地图主控件
	public static MapView mMapView = null;
	public static TextView addrestextview = null;
	// 定義兩個textview用來存放經偉度
	public static TextView latitudeteTextView, lontitudeteTextView;
	private boolean isRequest = false;// 是否手动触发请求定位
	private boolean isFirstLoc = true;// 是否首次定位
	// 用MapController完成地图控制
	public static EasyUpApplication app = null;
	private MapController mMapController = null;
	// 定义一个linearlayout布局
	private LinearLayout returnLayout = null;
	// MKMapViewListener 用于处理地图事件回调
	private LocationClient mLocClient;
	private LocationData mLocData;
	public static TextView popText;
	MKMapViewListener mMapListener = null;
	// 定义两个字符串来存放经纬度
	private String latitude, lontitude, address;
	public static BDLocation location;
	// 定义一个editext
	public static EditText searchEditText;
	//
	MKSearch mSearch = null;
	//
	private String title = "";
	//
	private EditText searEditText = null;
	private LocationOverlay myLocationOverlay = null;
	public String rsaddress = "";
	public String lat = "";
	public String lon = "";
	public String city = "上海";
	public String province = "上海市";
	private List<String> dataList = new ArrayList<String>();
	private PopupWindow pop1;
	private View popview;
	private LayoutInflater inflater = null;
	private EditText keyEditText;
	private LinearLayout reLayout;
	private PoiListAdapter adapter = null;
	private ListView poiListView = null;
	private List<MKPoiInfo> data = new ArrayList<MKPoiInfo>();
	private TextView addressTextView, poiTextView;
	public static Bitmap saveBitmap = null;
	RouteOverlay routeOverlay = null;
	int nodeIndex = -2;// 节点索引,供浏览节点时使用
	MKRoute route = null;// 保存驾车/步行路线数据的变量，供浏览节点时使用
	TransitOverlay transitOverlay = null;// 保存公交路线图层数据的变量，供浏览节点时使用
	private ImageView naviImageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 使用地图sdk前需先初始化BMapManager. BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
		 * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
		 */
		Bundle bundle = this.getIntent().getExtras();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		latitude = AppContext.latitude;
		lontitude = AppContext.longtude;
		address = AppContext.address;
		lat = latitude;
		lon = lontitude;
		new GetAdressTask().execute();
		app = (EasyUpApplication) this.getApplication();
		if (app.mBMapManager == null) {
			app.mBMapManager = new BMapManager(getApplicationContext());
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			app.mBMapManager.init(EasyUpApplication.strKey,
					new EasyUpApplication.MyGeneralListener());
		}
		/**
		 * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
		 */
		setContentView(R.layout.act_resetlocation);
		inflater = LayoutInflater.from(this);
		// 引入窗口配置文件
		popview = inflater.inflate(R.layout.poilist_popview, null);
		addressTextView = (TextView) findViewById(R.id.pop_txt_address);
		poiTextView = (TextView) findViewById(R.id.pop_txt_poi);
		keyEditText = (EditText) popview.findViewById(R.id.ztgame_edit_popview);
		reLayout = (LinearLayout) popview
				.findViewById(R.id.ztgame_return_popview);
		poiListView = (ListView) popview.findViewById(R.id.ztgame_list_popview);
		latitudeteTextView = (TextView) findViewById(R.id.resetlocation_latitude_textview);
		lontitudeteTextView = (TextView) findViewById(R.id.resetlocation_lontitude_textview);
		addrestextview = (TextView) findViewById(R.id.resetlocation_address_textview);
		mMapView = (MapView) findViewById(R.id.resetlocation_mapview);
		naviImageView = (ImageView) findViewById(R.id.resetlocation_img_navi);
		searEditText = (EditText) findViewById(R.id.resetlocation_address_edittext);
		pop1 = new PopupWindow(popview, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		pop1.setBackgroundDrawable(new BitmapDrawable());
		// 设置点击窗口外边窗口消失
		pop1.setOutsideTouchable(true);
		// 设置此参数获得焦点，否则无法点击
		pop1.setFocusable(true);
		addressTextView.setText(address);
		searEditText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (pop1.isShowing()) {
					// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
					pop1.dismiss();
				} else {
					// 显示窗口
					pop1.showAtLocation(v, Gravity.TOP, 0, 0);
					// pop1.showAsDropDown(v);

				}
			}
		});

		reLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				pop1.dismiss();

			}
		});

		returnLayout = (LinearLayout) findViewById(R.id.resetlocation_returnimg_linearlayout);
		returnLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MapActivity.this.finish();

			}
		});
		((TextView) findViewById(R.id.resetlocation__txt_confirm))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						startProgressDialog();
						mMapView.getCurrentMap();

					}
				});

		// 初始化搜索模块，注册搜索事件监听
		mSearch = new MKSearch();
		mSearch.init(app.mBMapManager, new MKSearchListener() {
			// 在此处理详情页结果
			@Override
			public void onGetPoiDetailSearchResult(int type, int error) {
				if (error != 0) {
					Toast.makeText(MapActivity.this, "抱歉，未找到结果",
							Toast.LENGTH_SHORT).show();
				} else {

					Toast.makeText(MapActivity.this, "成功，查看详情页面",
							Toast.LENGTH_SHORT).show();
				}
			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
				if (error != 0) {

				}

				if (res.type == MKAddrInfo.MK_REVERSEGEOCODE) {
					// 反地理编码：通过坐标点检索详细地址及周边poi
					addressTextView.setText(res.strAddr.substring(3));
					rsaddress = addressTextView.getText().toString() + title;
				}
			}

			/**
			 * 在此处理poi搜索结果
			 */
			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				// 错误号可参考MKEvent中的定义
				if (error != 0 || res == null) {
					Toast.makeText(MapActivity.this, "抱歉，未找到结果",
							Toast.LENGTH_LONG).show();
					return;
				}
				// 将地图移动到第一个POI中心点
				if (res.getCurrentNumPois() > 0) {
					// 将poi结果显示到地图上
					// Toast.makeText(MapActivity.this,
					// "找到结果" + res.getAllPoi().size(), Toast.LENGTH_LONG)
					// .show();
					data = res.getAllPoi();
					adapter = new PoiListAdapter(MapActivity.this, data,
							handler);
					poiListView.setAdapter(adapter);

				} else if (res.getCityListNum() > 0) {
					// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表

				}
			}

			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {

				// 起点或终点有歧义，需要选择具体的城市列表或地址列表
				if (error == MKEvent.ERROR_ROUTE_ADDR) {
					// 遍历所有地址
					// ArrayList<MKPoiInfo> stPois =
					// res.getAddrResult().mStartPoiList;
					// ArrayList<MKPoiInfo> enPois =
					// res.getAddrResult().mEndPoiList;
					// ArrayList<MKCityListInfo> stCities =
					// res.getAddrResult().mStartCityList;
					// ArrayList<MKCityListInfo> enCities =
					// res.getAddrResult().mEndCityList;
					Toast.makeText(MapActivity.this, "抱歉，未找到路线",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (error != 0 || res == null) {
					Toast.makeText(MapActivity.this, "抱歉，未找到结果",
							Toast.LENGTH_SHORT).show();
					return;
				}

				transitOverlay = new TransitOverlay(MapActivity.this, mMapView);
				// 此处仅展示一个方案作为示例
				transitOverlay.setData(res.getPlan(0));
				// 清除其他图层
				mMapView.getOverlays().clear();
				// 添加路线图层
				mMapView.getOverlays().add(transitOverlay);
				// 执行刷新使生效
				mMapView.refresh();
				// 使用zoomToSpan()绽放地图，使路线能完全显示在地图上
				mMapView.getController().zoomToSpan(
						transitOverlay.getLatSpanE6(),
						transitOverlay.getLonSpanE6());
				// 移动地图到起点
				mMapView.getController().animateTo(res.getStart().pt);
				// 重置路线节点索引，节点浏览时使用
				nodeIndex = 0;
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
			}

			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}

			/**
			 * 更新建议列表
			 */
			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
				if (res == null || res.getAllSuggestions() == null) {
					return;
				}
				dataList.clear();
				for (MKSuggestionInfo info : res.getAllSuggestions()) {
					if (info.key != null)
						dataList.add(info.key);
				}
				// sugAdapter.notifyDataSetChanged();

			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult result, int type,
					int error) {
				// TODO Auto-generated method stub

			}
		});

		keyEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				if (cs.length() <= 0) {
					return;
				}
				/**
				 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
				 */
				mSearch.suggestionSearch(cs.toString(), city);
				mSearch.poiSearchInCity(city, keyEditText.getText().toString());
			}
		});
		// 按键点击事件
		OnClickListener clickListener = new OnClickListener() {
			public void onClick(View v) {
				// 发起搜索
				// searchButtonProcess();
			}
		};

		naviImageView.setOnClickListener(clickListener);
	}

	@Override
	protected void onPause() {
		/**
		 * MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		 */
		mMapView.onPause();
		super.onPause();

	}

	@Override
	protected void onResume() {
		/**
		 * MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		 */
		mMapView.onResume();
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		/**
		 * MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		 */
		mMapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	private class LocationOverlay extends MyLocationOverlay {

		public LocationOverlay(MapView arg0) {
			super(arg0);
		}

		@Override
		protected boolean dispatchTap() {

			return super.dispatchTap();
		}

		@Override
		public void setMarker(Drawable arg0) {
			super.setMarker(arg0);
		}

	}

	private Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				// stopProgressDialog();
				MapActivity.this.finish();
			}
		}
	};

	/**
	 * 定位接口，需要实现两个方法
	 * 
	 * @author yuzhuo
	 * 
	 */
	public class BDLocationListenerImpl implements BDLocationListener {
		private String lat;
		private String lon;

		public BDLocationListenerImpl(String lat, String lon) {
			this.lat = lat;
			this.lon = lon;
		}

		/**
		 * 接收异步返回的定位结果，参数是BDLocation类型参数
		 */
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}

			MapActivity.this.location = location;

			mLocData.latitude = (int) (Double.valueOf(lat) * 1e6 + 6600) / 1e6;
			mLocData.longitude = (int) (Double.valueOf(lon) * 1e6 + 6700) / 1e6;
			// 如果不显示定位精度圈，将accuracy赋值为0即可
			mLocData.accuracy = 0;
			// mLocData.direction = location.getDerect();
			// 定位图层初始化
			myLocationOverlay = new LocationOverlay(mMapView);
			// 将定位数据设置到定位图层里
			myLocationOverlay.setData(mLocData);
			// myLocationOverlay.setMarker(getResources().getDrawable(
			// R.drawable.map_point_blue));
			// 添加定位图层
			// mMapView.getOverlays().add(myLocationOverlay);
			myLocationOverlay.enableCompass();
			// 更新图层数据执行刷新后生效
			mMapView.refresh();

			if (isFirstLoc || isRequest) {
				mMapController.animateTo(new GeoPoint((int) (Double
						.valueOf(lat) * 1E6) + 6600,
						(int) (Double.valueOf(lon) * 1E6) + 6700));

				// showPopupOverlay(location);

				isRequest = false;
			}

			isFirstLoc = false;
		}

		/**
		 * 接收异步返回的POI查询结果，参数是BDLocation类型参数
		 */
		@Override
		public void onReceivePoi(BDLocation poiLocation) {

		}

	}

	/**
	 * 手动请求定位的方法
	 */
	public void requestLocation() {
		isRequest = true;

		if (mLocClient != null && mLocClient.isStarted()) {
			showToast("正在定位......");
			mLocClient.requestLocation();
		} else {
			Log.d("LocSDK3", "locClient is null or not started");
		}
	}

	/**
	 * 显示Toast消息
	 * 
	 * @param msg
	 */
	private void showToast(String msg) {
		if (mToast == null) {
			mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(msg);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	private void showPopup(String address, int latitude, int lontitude) {
		MapActivity.popText.setText(address);
		MapActivity.mPopupOverlay.showPopup(
				getBitmapFromView(MapActivity.popText), new GeoPoint(latitude,
						lontitude), 10);
	}

	/**
	 * 
	 * @param view
	 * @return
	 */
	public static Bitmap getBitmapFromView(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}

	private class GetAdressTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			/**
			 * 获取地图控制器
			 */
			mMapController = mMapView.getController();
			/**
			 * 设置地图是否响应点击事件 .
			 */
			mMapController.enableClick(true);
			/**
			 * 设置地图缩放级别
			 */
			mMapController.setZoom(17);
			// viewCache = LayoutInflater.from(MapActivity.this).inflate(
			// R.layout.showlocation_pop, null);
			mPopupOverlay = new PopupOverlay(mMapView,
					new PopupClickListener() {

						@Override
						public void onClickedPopup(int arg0) {
							mPopupOverlay.hidePop();

						}
					});
			mLocData = new LocationData();
			// 实例化定位服务，LocationClient类必须在主线程中声明
			mLocClient = new LocationClient(getApplicationContext());
			mLocClient
					.registerLocationListener(new BDLocationListenerImpl(
							String.valueOf((int) (Double.valueOf(latitude) * 1e6) / 1e6),
							String.valueOf((int) (Double.valueOf(lontitude) * 1e6) / 1e6)));// 注册定位监听接口

			/**
			 * 设置定位参数
			 */
			LocationClientOption option = new LocationClientOption();
			// 打开GPRS
			option.setOpenGps(true);
			// 返回的定位结果包含地址信息
			option.setAddrType("all");
			// 返回的定位结果是百度经纬度,默认值gcj02
			option.setCoorType("bd09ll");
			// 设置发起定位请求的间隔时间为5000ms
			option.setScanSpan(5000);
			// 禁止启用缓存定位
			option.disableCache(false);
			option.setPoiNumber(100); // 最多返回POI个数
			// option.setPoiDistance(1000); //poi查询距离
			// option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息

			mLocClient.setLocOption(option);
			mLocClient.start(); // 调用此方法开始定位
			mLocData.latitude = (int) (Double.valueOf(lat) * 1e6 + 6600) / 1e6;
			mLocData.longitude = (int) (Double.valueOf(lon) * 1e6 + 6700) / 1e6;
			// 定位图层初始化
			mLocData.accuracy = 0;
			// mLocData.direction = MapActivity.this.location.getDerect();
			myLocationOverlay = new LocationOverlay(mMapView);
			// // 设置定位数据
			myLocationOverlay.setData(mLocData);

			myLocationOverlay.setMarker(getResources().getDrawable(
					R.drawable.location_arrows));
			// 添加定位图层-
			mMapView.getOverlays().add(myLocationOverlay);
			myLocationOverlay.enableCompass();

			// 修改定位数据后刷新图层生效
			mMapView.refresh();
			/**
			 * 将地图移动至指定点
			 * 使用百度经纬度坐标，可以通过http://api.map.baidu.com/lbsapi/getpoint/index
			 * .html查询地理坐标 如果需要在百度地图上显示使用其他坐标系统的位置，请发邮件至mapapi@baidu.com申请坐标转换接口
			 */
			GeoPoint p;
			double cLat = (int) (Double.valueOf(latitude) * 1e6) / 1e6;
			double cLon = (int) (Double.valueOf(latitude) * 1e6) / 1e6;
			Intent intent = getIntent();
			if (intent.hasExtra("x") && intent.hasExtra("y")) {
				// 当用intent参数时，设置中心点为指定点
				Bundle b = intent.getExtras();
				p = new GeoPoint(b.getInt("y"), b.getInt("x"));
			} else {
				// 设置中心点
				p = new GeoPoint((int) (cLat * 1E6), (int) (cLon * 1E6));
			}

			mMapController.setCenter(p);

			/**
			 * MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
			 */
			mMapListener = new MKMapViewListener() {
				@Override
				public void onMapMoveFinish() {
					/**
					 * 在此处理地图移动完成回调 缩放，平移等操作完成后，此回调被触发
					 */
				}

				@Override
				public void onClickMapPoi(MapPoi mapPoiInfo) {
					/**
					 * 在此处理底图poi点击事件 显示底图poi名称并移动至该点 设置过：
					 * mMapController.enableClick(true); 时，此回调才能被触发
					 * 
					 */

					if (mapPoiInfo != null) {
						title = "";
						rsaddress = "";
						title = mapPoiInfo.strText;
						lat = mapPoiInfo.geoPt.getLatitudeE6() / 1e6 + "";
						lon = mapPoiInfo.geoPt.getLongitudeE6() / 1e6 + "";
						GeoPoint geoPoint = new GeoPoint(
								mapPoiInfo.geoPt.getLatitudeE6(),
								mapPoiInfo.geoPt.getLongitudeE6());
						// search(geoPoint);
						mSearch.reverseGeocode(geoPoint);
						rsaddress = title;

						poiTextView.setText(title);

						mLocData.latitude = Double.valueOf(mapPoiInfo.geoPt
								.getLatitudeE6() / 1e6 + "");
						mLocData.longitude = Double.valueOf(mapPoiInfo.geoPt
								.getLongitudeE6() / 1e6 + "");
						// 如果不显示定位精度圈，将accuracy赋值为0即可
						mLocData.accuracy = 0;
						mLocData.direction = location.getDerect();
						mMapView.getOverlays().clear();
						// 定位图层初始化
						myLocationOverlay = new LocationOverlay(mMapView);
						// 将定位数据设置到定位图层里
						myLocationOverlay.setData(mLocData);
						myLocationOverlay.setMarker(getResources().getDrawable(
								R.drawable.location_arrows));
						// 添加定位图层
						mMapView.getOverlays().add(myLocationOverlay);
						myLocationOverlay.enableCompass();
						// 更新图层数据执行刷新后生效
						mMapView.refresh();
						mMapController.animateTo(mapPoiInfo.geoPt);
					}
				}

				@Override
				public void onGetCurrentMap(Bitmap b) {
					/**
					 * 当调用过 mMapView.getCurrentMap()后，此回调会被触发 可在此保存截图至存储设备
					 */
					saveBitmap = b;
					// ChatActivity.SAVEBITMAP = b;
					if (saveBitmap != null) {
						Message msgMessage = new Message();
						msgMessage.what = 0;
						handler2.sendMessage(msgMessage);
					}
					stopProgressDialog();
				}

				@Override
				public void onMapAnimationFinish() {
					/**
					 * 地图完成带动画的操作（如: animationTo()）后，此回调被触发
					 */
				}

				/**
				 * 在此处理地图载完成事件
				 */
				@Override
				public void onMapLoadFinish() {
					Toast.makeText(MapActivity.this, "地图加载完成",
							Toast.LENGTH_SHORT).show();

				}
			};
			mMapView.regMapViewListener(
					EasyUpApplication.getInstance().mBMapManager, mMapListener);

			super.onPostExecute(result);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				pop1.dismiss();

				keyEditText.setText("");

				MKPoiInfo mkPoiInfo = (MKPoiInfo) msg.obj;
				poiTextView.setText(mkPoiInfo.name);
				addressTextView.setText(mkPoiInfo.address);
				Log.e("latitude", "latitude" + mkPoiInfo.pt.getLatitudeE6());
				Log.e("lontitude", "lontitude" + mkPoiInfo.pt.getLongitudeE6()
						+ "");
				lat = mkPoiInfo.pt.getLatitudeE6() / 1e6 + "";
				lon = mkPoiInfo.pt.getLongitudeE6() / 1e6 + "";
				rsaddress = mkPoiInfo.name;
				city = mkPoiInfo.city;
				mMapController = mMapView.getController();
				// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
				GeoPoint point = new GeoPoint(mkPoiInfo.pt.getLatitudeE6(),
						mkPoiInfo.pt.getLongitudeE6());
				// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
				mMapController.setCenter(point);// 设置地图中心点
				mMapController.setZoom(17);// 设置地图zoom级别
				mLocData = new LocationData();
				// 实例化定位服务，LocationClient类必须在主线程中声明
				mLocClient = new LocationClient(getApplicationContext());
				mLocClient.registerLocationListener(new BDLocationListenerImpl(
						mkPoiInfo.pt.getLatitudeE6() / 1e6 + "", mkPoiInfo.pt
								.getLongitudeE6() / 1e6 + ""));// 注册定位监听接口

				/**
				 * 设置定位参数
				 */
				LocationClientOption option = new LocationClientOption();
				// 打开GPRS
				option.setOpenGps(true);
				// 返回的定位结果包含地址信息
				option.setAddrType("all");
				// 返回的定位结果是百度经纬度,默认值gcj02
				option.setCoorType("bd09ll");
				// 设置发起定位请求的间隔时间为5000ms
				option.setScanSpan(5000);
				// 禁止启用缓存定位
				option.disableCache(false);

				mLocClient.setLocOption(option);
				mLocClient.start(); // 调用此方法开始定位
				mLocData.latitude = (int) (mkPoiInfo.pt.getLatitudeE6()) / 1e6;
				mLocData.longitude = (int) (mkPoiInfo.pt.getLongitudeE6()) / 1e6;
				// 如果不显示定位精度圈，将accuracy赋值为0即可
				mLocData.accuracy = 0;
				mLocData.direction = location.getDerect();
				// 定位图层初始化
				myLocationOverlay = new LocationOverlay(mMapView);
				// 将定位数据设置到定位图层里
				myLocationOverlay.setData(mLocData);
				myLocationOverlay.setMarker(getResources().getDrawable(
						R.drawable.location_arrows));
				// 添加定位图层
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(myLocationOverlay);
				myLocationOverlay.enableCompass();
				// 更新图层数据执行刷新后生效
				mMapView.refresh();

				if (isFirstLoc || isRequest) {
					mMapController.setCenter(new GeoPoint(mkPoiInfo.pt
							.getLatitudeE6(), mkPoiInfo.pt.getLongitudeE6()));

					isRequest = false;
				}

				isFirstLoc = false;
			}
		}
	};

	/**
	 * 发起路线规划搜索示例
	 * 
	 * @param v
	 */
	// void searchButtonProcess() {
	// // 重置浏览节点的路线数据
	// route = null;
	// routeOverlay = null;
	// transitOverlay = null;
	// // 对起点终点的name进行赋值，也可以直接对坐标赋值，赋值坐标则将根据坐标进行搜索
	// MKPlanNode stNode = new MKPlanNode();
	// stNode.name = service.getPreference().get("address");
	// Log.e("stNode.name", stNode.name);
	// MKPlanNode enNode = new MKPlanNode();
	// enNode.name = poiTextView.getText().toString();
	// Log.e("enNode", enNode.name);
	// mSearch.transitSearch(service.getPreference().get("city"), stNode,
	// enNode);
	// // 实际使用中请对起点终点城市进行正确的设定
	// // if (mBtnDrive.equals(v)) {
	// // mSearch.drivingSearch("北京", stNode, "北京", enNode);
	// // } else if (mBtnTransit.equals(v)) {
	// // mSearch.transitSearch("北京", stNode, enNode);
	// // } else if (mBtnWalk.equals(v)) {
	// // mSearch.walkingSearch("北京", stNode, "北京", enNode);
	// // }
	// }

}
