package com.wen.security.adapter;

 
import java.util.List;

import android.content.Context;  
import android.support.v4.view.PagerAdapter; 
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams; 

public class MyPagerAdapter extends PagerAdapter {
	private Context context;
	private List<View> views;

	private View mCurrentView;

	public MyPagerAdapter(Context mContext, List<View> mViews) {
		context = mContext;
		views = mViews;

	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public View instantiateItem(ViewGroup container, int position) {
		View convertView = views.get(position);
		container.addView(convertView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);

		return convertView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		mCurrentView = (View) object;

	}

	public View getPrimaryItem() {
		return mCurrentView;
	}

}
