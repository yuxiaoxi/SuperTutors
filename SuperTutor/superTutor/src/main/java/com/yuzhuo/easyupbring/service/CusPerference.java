package com.yuzhuo.easyupbring.service;
import net.duohuo.dhroid.util.Perference;

/**
 * 
* @ClassName: CusPerference 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author 余卓 
* @date 2014年12月24日 下午2:23:26 
*
 */
public class CusPerference extends Perference {
	// 必须是public的属性不然不会赋值的
	
	//perference 保存用户帐号和密码的account的属性
	public String userName = ""; // 用户名
	public String password = ""; // 密码
	public String uid = ""; //uid
	public String nickName = "";
	public String lontitude="";  //经度
	public String latitude ="";  //纬度
	public String address =""; //地址
	public boolean isFirst = true;//是否是第一次启动app
	public String city="西安市";//城市
	public int logintype = 0;//帐号类型
	public int threelogintype = 0;//第三方帐号类型
	public String msg_time = "1447753069856";
}
