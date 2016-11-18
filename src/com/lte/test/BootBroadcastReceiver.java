package com.lte.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lte.AcRegister;

/**
 * 开机启动
 * 
 * @author aaa
 * 
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
	static final String action_boot = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(action_boot)) {
			Intent reIntent = new Intent(context, AcRegister.class);
			reIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(reIntent);
		}

	}

}
