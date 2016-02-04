package com.basv.gifmoviewview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.basv.gifmoviewview.widget.GifMovieView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final GifMovieView gif1 = (GifMovieView) findViewById(R.id.gif1);
		gif1.setMovieResource(R.drawable.gif_heart);
	}

	public void onGifClick(View v) {
		GifMovieView gif = (GifMovieView) v;
		gif.setPaused(!gif.isPaused());
	}
}
