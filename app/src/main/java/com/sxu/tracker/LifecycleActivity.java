package com.sxu.tracker;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LifecycleActivity extends AppCompatActivity {

	public final static int STYLE_NORMAL = 1;
	public final static int STYLE_INNER = 2;
	public final static int STYLE_PAGER = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lifecycle_layout);
		int style = getIntent().getIntExtra("style", STYLE_NORMAL);
		if (style == STYLE_NORMAL || style == STYLE_INNER) {
			final FragmentManager fm = getSupportFragmentManager();
			final FragmentTransaction transaction = fm.beginTransaction();
			if (style == STYLE_NORMAL) {
				final Fragment fragment = new InnerFragment();
				transaction.add(R.id.container_layout, fragment);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						final FragmentTransaction transaction = fm.beginTransaction();
						transaction.hide(fragment);
						transaction.commit();
					}
				}, 2000);

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						final FragmentTransaction transaction = fm.beginTransaction();
						transaction.show(fragment);
						transaction.commit();
					}
				}, 4000);
			} else {
				transaction.add(R.id.container_layout, new LifecycleFragment());
			}
			transaction.commit();
		} else {
			ViewPager viewPager = findViewById(R.id.view_pager);
			viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
				@Override
				public int getCount() {
					return 4;
				}

				@Override
				public long getItemId(int position) {
					return position;
				}

				@Override
				public Fragment getItem(int i) {
					Fragment fragment = new Fragment();
					switch (i) {
						case 0:
							fragment = new FirstFragment();
							break;
						case 1:
							fragment = new SecondFragment();
							break;
						case 2:
							fragment = new ThirdFragment();
							break;
						case 3:
							fragment = new FourFragment();
							break;
						default:
							break;
					}
					return fragment;
				}
			});
		}
	}

	public static void enter(Context context, int style) {
		Intent intent = new Intent(context, LifecycleActivity.class);
		intent.putExtra("style", style);
		context.startActivity(intent);
	}
}
