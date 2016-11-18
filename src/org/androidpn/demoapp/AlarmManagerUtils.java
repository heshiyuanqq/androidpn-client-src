package org.androidpn.demoapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Polling Tools
 * 
 * @Author
 * @Create
 */
public class AlarmManagerUtils {

	/**
	 * @param context
	 * @param seconds
	 * @param cls
	 * @param action
	 */
	public static void registerAlarmManager(Context context, int seconds,
			Class<?> cls, String action) {
		// ELAPSED_REALTIME:当系统进入睡眠状态的时候，这种类型的状态不会唤醒系统，直到系统下次呗唤醒才会传递它。该闹铃所用的时间是相对的。
		// ELAPSED_REALTIME_WAKEUP:当系统进入睡眠状态的时候这种类型的闹铃能够被唤醒系统，直到系统下次被唤醒才传递它，该哪弄令所用的时间是相对的。
		// RTC:当系统进入睡眠状态时，这种类型的闹铃不会被唤醒系统，直到系统下次呗唤醒才传递它，该闹铃所用的时间是绝对的。
		// RTC_WAKEUP:当系统进入睡眠状态时，这种类型的闹铃能够唤醒系统，直到系统下次被唤醒才传递它，该闹铃所用的时间是绝对的。
		AlarmManager manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, cls);
		intent.setAction(action);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		long triggerAtTime = SystemClock.elapsedRealtime();
		// manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime,
		// seconds * 1000, pi);
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				triggerAtTime, seconds * 1000, pi);
	}

	/**
	 * 
	 * @param context
	 * @param cls
	 * @param action
	 */
	public static void cancelRegisterAlarmManager(Context context,
			Class<?> cls, String action) {
		AlarmManager manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, cls);
		intent.setAction(action);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		manager.cancel(pi);
	}

}
