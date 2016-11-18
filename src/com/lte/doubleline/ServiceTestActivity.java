package com.lte.doubleline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lte.listener.ListenerService;

public class ServiceTestActivity extends Activity {

	private Context context;
	private Button startServiceBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.context = this;
		startServiceBtn = new Button(context);
		startServiceBtn.setText("启动监听Service");
		this.setContentView(startServiceBtn);
		Log.i("---info---", "点击事件");
		startServiceBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ListenerService.class);
				context.startService(intent);
				// DelFile.delFile("/sdcard/testtest");
				// File myFile = new File("/sdcard/testtest");
				// if (myFile.exists()) {
				// Toast.makeText(getApplicationContext(), "false", 0).show();
				// } else {
				// Toast.makeText(getApplicationContext(), "true", 0).show();
				// }
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent intent = new Intent(context, ListenerService.class);
		context.stopService(intent);
	}

}
