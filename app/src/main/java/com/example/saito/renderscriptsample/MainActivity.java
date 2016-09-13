package com.example.saito.renderscriptsample;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v8.renderscript.RenderScript;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{


	private RenderScript rs;
	private Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		rs = RenderScript.create(this);

		findViewById(R.id.blur_button_1).setOnClickListener(this);
		findViewById(R.id.blur_button_2).setOnClickListener(this);
		findViewById(R.id.blur_button_3).setOnClickListener(this);
		findViewById(R.id.blur_button_4).setOnClickListener(this);

		Drawable drawable;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			drawable = getDrawable(R.drawable.screen);
		} else {
			drawable = getResources().getDrawable(R.drawable.screen);
		}
		bitmap = ((BitmapDrawable) drawable).getBitmap();
	}

	@Override
	public void onClick(View v) {
		final long start = System.currentTimeMillis();
		switch (v.getId()) {
			case R.id.blur_button_1:
				new AsyncTask<Void, Bitmap, Bitmap>() {
					@Override
					protected Bitmap doInBackground(Void... params) {
						return ImageUtil.process1(getApplicationContext(), bitmap);
					}
					@Override
					protected void onPostExecute(Bitmap blurBitmap) {
						// 画像をセット
						((ImageView) findViewById(R.id.blur_image_1)).setImageBitmap(blurBitmap);
						long end = System.currentTimeMillis();
						((TextView) findViewById(R.id.msec_text_1)).setText((end - start) + "msec");
					}
				}.execute(null, null, null);
				break;
			case R.id.blur_button_2:
				new AsyncTask<Void, Bitmap, Bitmap>() {
					@Override
					protected Bitmap doInBackground(Void... params) {
						return ImageUtil.process2(getApplicationContext(), bitmap);
					}
					@Override
					protected void onPostExecute(Bitmap blurBitmap) {
						// 画像をセット
						((ImageView) findViewById(R.id.blur_image_2)).setImageBitmap(blurBitmap);
						long end = System.currentTimeMillis();
						((TextView) findViewById(R.id.msec_text_2)).setText((end - start) + "msec");
					}
				}.execute(null, null, null);
				break;
			case R.id.blur_button_3:
				new AsyncTask<Void, Bitmap, Bitmap>() {
					@Override
					protected Bitmap doInBackground(Void... params) {
						return ImageUtil.process3(getApplicationContext(), bitmap);
					}
					@Override
					protected void onPostExecute(Bitmap blurBitmap) {
						// 画像をセット
						((ImageView) findViewById(R.id.blur_image_3)).setImageBitmap(blurBitmap);
						long end = System.currentTimeMillis();
						((TextView) findViewById(R.id.msec_text_3)).setText((end - start) + "msec");
					}
				}.execute(null, null, null);
				break;
			case R.id.blur_button_4:
				new AsyncTask<Void, Bitmap, Bitmap>() {
					@Override
					protected Bitmap doInBackground(Void... params) {
						return ImageUtil.process4(getApplicationContext(), bitmap);
					}
					@Override
					protected void onPostExecute(Bitmap blurBitmap) {
						// 画像をセット
						((ImageView) findViewById(R.id.blur_image_4)).setImageBitmap(blurBitmap);
						long end = System.currentTimeMillis();
						((TextView) findViewById(R.id.msec_text_4)).setText((end - start) + "msec");
					}
				}.execute(null, null, null);
				break;
		}
	}
}
