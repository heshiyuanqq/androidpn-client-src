package com.lte.util;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

public class WakeLockUtil {
	
	private static final String TAG = "WakeLockUtil";
	private static PowerManager.WakeLock wakeLock;
//	// 声明键盘管理器
//	private static KeyguardManager mKeyguardManager = null;
	// 声明键盘锁
	private static KeyguardLock keyguardLock = null;

	/**
	 * 解锁 && 亮屏
	 */
	public static void acquireWakeLock(Context context) {
//		if (wakeLock == null || keyguardLock == null) {
		//亮屏
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, NotificationService.SERVICE_NAME);
			wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
			wakeLock.acquire();
			//解锁
			// 获取系统服务
			KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
			// 初始化键盘锁，可以锁定或解开键盘锁
			keyguardLock = keyguardManager.newKeyguardLock("");
			// 禁用显示键盘锁定
			keyguardLock.disableKeyguard();
			Log.d(TAG, "解锁亮屏null");
//		} else {
//			// 禁用显示键盘锁定
//			wakeLock.acquire();
//			keyguardLock.disableKeyguard();
//			Log.d(TAG, "解锁亮屏");
//		}
	}

	/**
	 * 取消亮屏
	 */
	public static void releaseWakeLock() {
		if (wakeLock != null) {
			Log.d(TAG, "取消解锁亮屏");
			wakeLock.release();
			wakeLock = null;
		}
	}
	
}
