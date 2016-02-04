package com.dengdeng123.blowupplane.module.game;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.basv.gifmoviewview.widget.GifMovieView;
import com.dengdeng123.blowupplane.R;
import com.dengdeng123.blowupplane.base.Constant;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MainGameGridAdapter extends BaseAdapter {

	private class MainGridDataViewHolder {
		@ViewInject(R.id.gridview_textview)
		private TextView gridview_textview;

		@ViewInject(R.id.gridview_imageview)
		private ImageView gridview_imageview;

		@ViewInject(R.id.gridview_imageview_gif)
		private GifMovieView gridview_imageview_gif;

		// @ViewInject(R.id.gridview_item_bg)
		// private FrameLayout gridview_item_bg;
	}

	private Context mContext;

	private LayoutInflater mInflater;

	private List<? extends Map<String, ?>> mData;

	private int mResource;

	public MainGameGridAdapter(Context context, List<? extends Map<String, ?>> data, int resource) {
		mContext = context;
		mData = data;
		mResource = resource;
		// mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MainGridDataViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(mResource, parent, false);

			holder = new MainGridDataViewHolder();

			ViewUtils.inject(holder, convertView);

			convertView.setTag(holder);
		} else {
			holder = (MainGridDataViewHolder) convertView.getTag();
		}

		Map<String, ?> dataSet = mData.get(position);

		String itemtext = (String) dataSet.get(Constant.ITEMTEXT);
		holder.gridview_textview.setText(itemtext);
		int bgColor = 0;
		if (itemtext.equals(Constant.SHOT_BLOCK_OFFSET)) {
			bgColor = android.R.color.transparent;
		} else {
			bgColor = mContext.getResources().getColor(R.color.gridview_item_bg_color);
		}
		// holder.gridview_item_bg.setBackgroundColor(bgColor);
		holder.gridview_imageview.setBackgroundColor(bgColor);

		Object imageViewRes = dataSet.get(Constant.ITEMIMAGE);
		if (imageViewRes instanceof Integer) {
			holder.gridview_imageview.setImageResource((Integer) imageViewRes);
		} else if (imageViewRes instanceof Bitmap) {
			holder.gridview_imageview.setImageBitmap((Bitmap) imageViewRes);
		}

		int gifMovieViewResId = (Integer) dataSet.get(Constant.ITEMIMAGE_GIF);
		if (gifMovieViewResId != Constant.NOT_PLAY_GIF) {
			// holder.gridview_imageview.setVisibility(View.GONE);
			holder.gridview_imageview_gif.setVisibility(View.VISIBLE);
			holder.gridview_imageview_gif.setMovieResource(gifMovieViewResId);
		} else {
			holder.gridview_imageview_gif.setVisibility(View.GONE);
		}

		return convertView;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Map<String, ?> getItem(int position) {
		mData.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
