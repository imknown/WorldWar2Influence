package com.dengdeng123.blowupplane.module.game.story;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.BaseActivity;
import com.dengdeng123.blowupplane.util.BitmapHelp;
import com.dengdeng123.blowupplane.util.DisplayUtil;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.activity_helper)
public class HelperActivity extends BaseActivity implements OnPageChangeListener {

	@ViewInject(R.id.viewpager)
	private ViewPager vp;

	@ViewInject(R.id.dots_lnrlyt)
	private LinearLayout dots_lnrlyt;

	@ViewInject(R.id.close_helper_txtv)
	private TextView close_helper_txtv;

	private ViewPagerAdapter vpAdapter;

	/** 现实的图片资源Id */
	private int[] imgResIds = new int[] { R.drawable.helper_1, R.drawable.helper_2, R.drawable.helper_3, R.drawable.helper_4, R.drawable.helper_5, R.drawable.helper_6 };

	/** 一共多少 */
	private int totalCount = imgResIds.length;

	/** 显示帮助页面的图片控件集合 */
	private List<ImageView> helperImgVs = new ArrayList<ImageView>(totalCount);

	/** 记录当前选中位置 */
	private int currentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initViews();

		initDots();
	}

	/** 动态加载控件 */
	private void initViews() {
		// LayoutInflater inflater = LayoutInflater.from(this);
		// views.add(inflater.inflate(R.layout.what_new_one, (ViewGroup) null, false));
		// views.add(inflater.inflate(R.layout.what_new_two, (ViewGroup) null, false));
		// views.add(inflater.inflate(R.layout.what_new_three, (ViewGroup) null, false));
		// views.add(inflater.inflate(R.layout.what_new_four, (ViewGroup) null, false));
		// views.add(inflater.inflate(R.layout.what_new_five, (ViewGroup) null, false));

		ViewGroup.LayoutParams helperImgVLp = new ViewGroup.LayoutParams(android.view.WindowManager.LayoutParams.MATCH_PARENT, android.view.WindowManager.LayoutParams.MATCH_PARENT);

		LinearLayout.LayoutParams dotsImgVLp = new LinearLayout.LayoutParams(android.view.WindowManager.LayoutParams.WRAP_CONTENT, android.view.WindowManager.LayoutParams.WRAP_CONTENT);
		int dip3 = (int) new DisplayUtil(this).dip2px_M1(3);// 3dip
		dotsImgVLp.setMargins(dip3, dip3, dip3, dip3);

		for (int i = 0; i < totalCount; i++) {
			ImageView helper_imgV = new ImageView(this);
			helper_imgV.setScaleType(ScaleType.FIT_XY);

			BitmapHelp.getBitmapUtils(this).display(helper_imgV, "drawable://" + imgResIds[i]);
			// helper_imgV.setImageBitmap(BitmapFactory.decodeResource(getResources(), imgResIds[i], opts));// 麻烦 懒得用
			// helper_imgV.setImageResource(imgResIds[i]);// 可能会 内存溢出
			helper_imgV.setLayoutParams(helperImgVLp);
			helperImgVs.add(helper_imgV);

			ImageView dot_iv = new ImageView(this);
			dot_iv.setImageResource(R.drawable.selector_dot);
			dot_iv.setLayoutParams(dotsImgVLp);
			dots_lnrlyt.addView(dot_iv);
		}

		// 初始化Adapter
		vpAdapter = new ViewPagerAdapter(helperImgVs/* , this */);

		vp.setAdapter(vpAdapter);

		// 绑定回调
		vp.setOnPageChangeListener(this);

		close_helper_txtv.setVisibility(View.GONE);
	}

	/** 初始化底部小点 */
	private void initDots() {

		for (int i = 0; i < totalCount; i++) {
			((ImageView) dots_lnrlyt.getChildAt(i)).setEnabled(true);// 都设为灰色
		}

		currentIndex = 0;
		dots_lnrlyt.getChildAt(currentIndex).setEnabled(false);// 设置为白色，即选中状态
	}

	/** 显示当前的点 */
	private void setCurrentDot(int position) {
		if (position < 0 || position > helperImgVs.size() - 1 || currentIndex == position) {
			return;
		}

		dots_lnrlyt.getChildAt(position).setEnabled(false);
		dots_lnrlyt.getChildAt(currentIndex).setEnabled(true);

		currentIndex = position;
	}

	@OnClick(R.id.close_helper_txtv)
	public void closeHelper(View view) {
		finish();
	}

	/** 当滑动状态改变时调用 */
	@Override
	public void onPageScrollStateChanged(int state) {
	}

	/** 当当前页面被滑动时调用 */
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

	/** 当新的页面被选中时调用 */
	@Override
	public void onPageSelected(int position) {
		setCurrentDot(position);

		if (position == helperImgVs.size() - 1) {
			close_helper_txtv.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
			close_helper_txtv.setVisibility(View.VISIBLE);
		} else {
			if (close_helper_txtv.getVisibility() == View.GONE) {
				return;
			}

			close_helper_txtv.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
			close_helper_txtv.setVisibility(View.GONE);
		}
	}

	class ViewPagerAdapter extends PagerAdapter {
		private List<ImageView> views;

		public ViewPagerAdapter(List<ImageView> views) {
			this.views = views;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(views.get(position));
		}

		@Override
		public int getCount() {
			if (views != null) {
				return views.size();
			}

			return 0;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(views.get(position), 0);

			// if (position == views.size() - 1) {
			// Button xxxx = (Button) container.findViewById(R.id.xxxx);
			// xxxx.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// }
			// });
			// }

			return views.get(position);
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return (view == obj);
		}
	}
}