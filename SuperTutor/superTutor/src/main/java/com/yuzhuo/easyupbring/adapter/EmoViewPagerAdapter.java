package com.yuzhuo.easyupbring.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 
* @ClassName: EmoViewPagerAdapter 
* @Description: 表情分页适配 
* @author 余卓 
* @date 2014年10月21日 上午11:17:47 
*
 */
public class EmoViewPagerAdapter extends PagerAdapter{

	private List<View> views;

	public EmoViewPagerAdapter(List<View> views){
		this.views = views;
	}
	
	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(views.get(arg1));
		return views.get(arg1);
	}
	
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(views.get(arg1));
		
	}

}
