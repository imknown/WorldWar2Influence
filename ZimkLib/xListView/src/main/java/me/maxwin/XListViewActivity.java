package me.maxwin;

import java.util.ArrayList;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;

public class XListViewActivity extends Activity implements IXListViewListener
{
	private XListView mListView;
	private ArrayAdapter<String> mAdapter;
	private ArrayList<String> items = new ArrayList<String>();
	private Handler mHandler;
	private int start = 0;

	// private static int refreshCnt = 0;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		geneItems();

		mAdapter = new ArrayAdapter<String>(this, R.layout.list_item, items);

		mListView = (XListView) findViewById(R.id.xListView);
		mListView.setAdapter(mAdapter);
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);

		mHandler = new Handler();
	}

	private void geneItems()
	{
		for (int i = 0; i < 5; ++i)
		{
			items.add("第 " + (++start) + "条数据");
		}
	}

	private void onLoadFinish()
	{
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime("刚刚");
	}

	@Override
	public void onRefresh()
	{
		System.out.println("下拉刷新");

		mHandler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				items.clear();
				start = 0;

				geneItems();
				// mAdapter.notifyDataSetChanged();// 这么写的话, 位置不会改变, 但是本例看不出来效果
				mAdapter = new ArrayAdapter<String>(XListViewActivity.this, R.layout.list_item, items);
				mListView.setAdapter(mAdapter);
				onLoadFinish();
			}
		}, 1500);
	}

	@Override
	public void onLoadMore()
	{
		System.out.println("上拉加载");

		mHandler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				geneItems();
				mAdapter.notifyDataSetChanged();
				onLoadFinish();
			}
		}, 1500);
	}
}
