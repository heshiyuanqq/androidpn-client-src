package com.lte.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class PluginUtil {

	public static PluginUtil plugIn;
	/**
	 * cpu总值
	 */
	private long cpuTotal = 0;
	/**
	 * cpu使用
	 */
	private double cpuUsage = 0;
	/**
	 * cpu空闲
	 */
    private long cpuIdle = 0;
	
	public static PluginUtil getInstance() {
		if (plugIn == null) {
			plugIn = new PluginUtil();
		}
		return plugIn;
	}
	
///**
// * 读取系统启动时间
// * @return 开机启动时间（单位 秒/float类型）
// */
//	public float getBootUpTime() {
//		String command = "cat /proc/uptime";
//		CommandResult commandResult = ShellUtil.execCommand(command, false, true);
//		System.out.println("result："+ commandResult.result);
//		System.out.println("successMsg："+ commandResult.successMsg);
//		System.out.println("errorMsg："+ commandResult.errorMsg);
//		String[] successMsg = commandResult.successMsg.split(" ");
////		float temp = Float.parseFloat(successMsg[0]);
////		float bootUpTime = temp/60<1 ? temp : temp/60;
//		return Float.parseFloat(successMsg[0]);
//	}
	
/**
 * 判断手机数据网络开关是否已经打开
 * @param context
 * @return Boolean
 */
	public boolean isMobileNetOn(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobileNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		return mobileNetworkInfo.isConnected();
	}
	
/***
 * 打开/关闭数据网络开关
 * @param paramContext	Context
 * @param paramBoolean 打开/关闭数据网络开关
 * @return boolean 是否设置成功
 */
	public boolean switchMobileNetwork(Context paramContext, boolean paramBoolean) {
		ConnectivityManager connectivityManager=(ConnectivityManager) paramContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			Method method = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			method.setAccessible(true);
			method.invoke(connectivityManager, paramBoolean);
			return true;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return false;
	}
	
///**
// * 重启
// */
//	public void reboot() {
//		String command = "reboot";
//		ShellUtil.execCommand(command, true, false);
//	}
	
/**
 * 读取系统运行总内存、可用内存
 * @param context
 * @return List<String> {系统内存}
 */
	public List<String> getRamList(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(memoryInfo);
		long availMem = memoryInfo.availMem;				//系统可用内存，单位是Byte
		String availMemM = Formatter.formatFileSize(context, availMem);
		long threshold = memoryInfo.threshold;				//系统内存不足的阀值
		String thresholdM = Formatter.formatFileSize(context, threshold);
		
		
		

//		String str1 = "/proc/meminfo";							// 系统内存信息文件
//		String meminfo;
//		String[] arrayOfString;
//		long memTotal = 0;
		String memTotalM = null;
		try {
			String filePath = "/proc/meminfo";											// 系统内存信息文件
			FileReader localFileReader = new FileReader(filePath);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			String meminfo = localBufferedReader.readLine();					// 读取meminfo第一行，系统总内存大小
			String[] arrayOfString = meminfo.split("\\s+");						//\\s表示空格,回车,换行等空白符，+号表示一个或多个的意思
//			for (String num : arrayOfString) {
//				System.out.println(str2+ num + "\t");
//			}
			long memTotal = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();
			memTotalM = Formatter.formatFileSize(context, memTotal);	// Byte转换为KB或者MB，内存大小规格化
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		
		
//		String command = "cat /proc/meminfo";				// 系统内存信息文件
//		CommandResult commandResult = ShellUtil.execCommand(command, false, true);
//		String[] successMsgArr = commandResult.successMsg.split("\n");
//		System.out.println("result："+ commandResult.result);
//		System.out.println("successMsg："+ commandResult.successMsg);
//		System.out.println("MemTotal："+ successMsgArr[0]);
//		System.out.println("errorMsg："+ commandResult.errorMsg);
		
		
//		long totalMem = memoryInfo.totalMem;
//		System.out.println("totalMem："+ totalMem);
		ArrayList<String> returnList = new ArrayList<String>();
		returnList.add(memTotalM);
		returnList.add(availMemM);
//		System.out.println("一共："+ memTotalM+ "；空闲："+ availMemM);
//		returnList.add("\n系统内存不足的阀值："+ thresholdM);
		return returnList;
	}
	
/**
 * 获得手机屏幕宽高 此大小是基于当前屏幕旋转而调整的
 * @return int[] {宽, 高}
 */
	public String getResolution(Activity activity) {
		DisplayMetrics displaysMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
		int windowWidth = displaysMetrics.widthPixels;							//此大小是基于当前屏幕旋转而调整的，前提是允许横
		int windowHeight = displaysMetrics.heightPixels;						//此大小是基于当前屏幕旋转而调整的，前提是允许横
		String resolution = windowWidth+ "x"+ windowHeight+ "像素";
//		int[] windowSize = {windowWidth, windowHeight};
//		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
//		int heigth = activity.getWindowManager().getDefaultDisplay().getHeight();
//		String str = width + "" + heigth + "";
		return resolution;
	}
	
/**
 * 读取手机分辨率
 * @param context
 * @return
 */
	public String getScreenResolution(Context context) {
		WindowManager windowManager = ((Activity) context).getWindowManager();
	    Display display = windowManager.getDefaultDisplay();
	    DisplayMetrics displayMetrics = new DisplayMetrics();
	    display.getMetrics(displayMetrics);
	    // since SDK_INT = 1;
	    int widthPixels = displayMetrics.widthPixels;
	    int heightPixels = displayMetrics.heightPixels;
	    return heightPixels+ "x"+ widthPixels+ "像素";
	}
	
/**
 * 读取屏幕英寸数
 * @param activity Activity
 * @return double 屏幕英寸
 */
	public double getScreenInch(Activity activity) {
		WindowManager windowManager = activity.getWindowManager();
	    Display display = windowManager.getDefaultDisplay();
	    DisplayMetrics displayMetrics = new DisplayMetrics();
	    display.getMetrics(displayMetrics);


	    // since SDK_INT = 1;
	    int mWidthPixels = displayMetrics.widthPixels;
	    int mHeightPixels = displayMetrics.heightPixels;

	    // includes window decorations (statusbar bar/menu bar)
	    if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
	    {
	        try
	        {
	            mWidthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
	            mHeightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
	        }
	        catch (Exception ignored)
	        {
	        }
	    }

	    // includes window decorations (statusbar bar/menu bar)
	    if (Build.VERSION.SDK_INT >= 17)
	    {
	        try
	        {
	            Point realSize = new Point();
	            Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
	            mWidthPixels = realSize.x;
	            mHeightPixels = realSize.y;
	        }
	        catch (Exception ignored)
	        {
	        }
	    }


		
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
//		int densityDpi = dm.densityDpi;
		double x = Math.pow(dm.widthPixels/dm.xdpi, 2);
		double y = Math.pow(dm.heightPixels/dm.ydpi, 2);
		double screenInches = Math.sqrt(x+y);
		
		
		
		
		DisplayMetrics dm1 = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm1);
		int width=dm1.widthPixels;
		int height=dm1.heightPixels;
		int dens=dm1.densityDpi;
		double wi=(double)width/(double)dens;
		double hi=(double)height/(double)dens;
		double x1 = Math.pow(wi,2);
		double y1 = Math.pow(hi,2);
		double screenInches1 = Math.sqrt(x1+y1);
		
		
		return screenInches1;
	}

/**
 * 获取CPU名字
 * @return
 */
	public String getCpuName() {
		try {
			FileReader fr = new FileReader("/proc/cpuinfo");
			BufferedReader br = new BufferedReader(fr);
			String text = br.readLine();
			String[] array = text.split(":\\s+", 2);
			for (int i = 0; i < array.length; i++) {
			}
			br.close();
			return array[1];
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
/**
 * 获取CPU最大频率（单位MHZ）
 * @return
 */
	public String getCpuMaxFreq(Context context) {
		String resultM = "";
		String result = "";
		ProcessBuilder cmd;
		try {
			String[] args = { "/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
			cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[24];
			while (in.read(re) != -1) {
				result = result + new String(re);
			}
			resultM = Formatter.formatFileSize(context, Long.parseLong(result.trim())*1024);
//			resultM = Long.parseLong(result.trim())/1000;
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			result = "N/A";
		}
		return resultM;
	}

/**
 * 获取CPU最小频率（单位MHZ）
 * @return
 */
	public long getCpuMinFreq() {
		long resultM = 0;
		String result = "";
		ProcessBuilder cmd;
		try {
			String[] args = { "/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq" };
			cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[24];
			while (in.read(re) != -1) {
				result = result + new String(re);
			}
			resultM = Long.parseLong(result.trim())/1000;
//			resultM = Formatter.formatFileSize(context, Long.parseLong(result.trim())*1024);
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			result = "N/A";
		}
		return resultM;
	}

/**
 * 实时获取CPU当前频率（单位MHZ）
 * @return
 */
	public long getCpuCurFreq() {
		long resultM = 0;
		String result = "";
		ProcessBuilder cmd;
		try {
			String[] args = { "/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq" };
			cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[24];
			while (in.read(re) != -1) {
				result = result + new String(re);
			}
			resultM = Long.parseLong(result.trim())/1000;
			
//			FileReader fr = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
//			BufferedReader br = new BufferedReader(fr);
//			String text = br.readLine();
//			result = text.trim();
//			System.out.println("当前："+ result);
//			resultM = Long.parseLong(result)/1000;
//			resultM = Formatter.formatFileSize(context, Long.parseLong(result.trim())*1024);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			result = "N/A";
		}
		return resultM;
	}
	
	
/**
 * 返回Cpu核心数<br>
 * Gets the number of cores available in this device, across all processors.<br>
 * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"<br>
 * @return Cpu核心数量，失败的话返回1<br>(The number of cores, or 1 if failed to get result)
 */
	public int getCpuNumCores() {
	    //Private Class to display only CPU devices in the directory listing
	    class CpuFilter implements FileFilter {
	        @Override
	        public boolean accept(File pathname) {
	            //Check if filename is "cpu", followed by a single digit number
	            if(Pattern.matches("cpu[0-9]", pathname.getName())) {
	                return true;
	            }
	            return false;
	        }      
	    }

	    try {
	        //Get directory containing CPU info
	        File dir = new File("/sys/devices/system/cpu/");
	        //Filter to only list the devices we care about
	        File[] files = dir.listFiles(new CpuFilter());
	        //Return the number of cores (virtual CPU devices)
	        return files.length;
	    } catch(Exception e) {
	        //Default to return 1 core
	        return 1;
	    }
	}
	
	/**
	 * 读取cpu总值、空闲、占用率
	 */
	public long[] getCupSizes() {
        try {
            BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( "/proc/stat" ) ), 1000 );
            String load = reader.readLine();
            reader.close();     

            String[] toks = load.split(" ");

            long currTotal = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]);
            long currIdle = Long.parseLong(toks[5]);

            this.cpuUsage =(currTotal - cpuTotal) * 100.0f / (currTotal - cpuTotal + currIdle - cpuIdle);
            this.cpuTotal = currTotal;
            this.cpuIdle = currIdle;
//            System.out.println(this.cpuUsage+ " - "+ this.cpuTotal+ " - "+ this.cpuIdle);
        } catch( IOException ex ) {
            ex.printStackTrace();           
        }
        long[] cpuSizeArr = {this.cpuTotal, this.cpuIdle, (long) this.cpuUsage};
        return cpuSizeArr;
	}
	
/**
 * 返回机身存储相关
 * @param context Context
 * @return 机身存储相关 String
 */
	@SuppressWarnings("deprecation")
	public String getStorageSize(Context context) {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		// 文件系统的块的大小(byte)
		long blockSize = stat.getBlockSize();
		// 文件系统的总的块数
		long totalBlocks = stat.getBlockCount();
		// 文件系统上空闲的可用于程序的存储块数
		long availableBlocks = stat.getAvailableBlocks();
		// 总的容量
		long totalSize = blockSize * totalBlocks;
		// 可用内存
		long availableSize = blockSize * availableBlocks;
		String totalStr = Formatter.formatFileSize(context, totalSize);
		String availableStr = Formatter.formatFileSize(context, availableSize);
		String returnString = "机身存储总量：" + totalStr + "；可用：" + availableStr;
		return totalStr;
	}
	
/**
 * 读取手机品牌
 * @return 手机品牌 String
 */
	public String getBrand() {
		return android.os.Build.BRAND;
	}
	
/**
 * 读取手机型号
 * @return 手机型号 String
 */
	public String getModel() {
		return android.os.Build.MODEL;
	}
	
/**
 * 读取版本号
 * @return 版本号 String
 */
	public String getRelease() {
		return android.os.Build.VERSION.RELEASE;
	}
	
///**
// * 读取后置摄像头像素
// * @return 像素 int
// */
//	public String getRearCameraInfo() {
//		//摄像头个数
//		int numberOfCameras = Camera.getNumberOfCameras();
//		//获取像素
//		CameraInfo cameraInfo = new CameraInfo();
//		int largestPixels = 0;
//        for (int i = 0; i < numberOfCameras; i++) {
//            Camera.getCameraInfo(i, cameraInfo);
//            //后置
//            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
//				Camera camera = Camera.open(i);
//				Parameters parameters = camera.getParameters();
//				parameters.set("camera-id", 1);
//				//获取到所有支持的图片像素
//				List<Size> psizelist = parameters.getSupportedPictureSizes();
//				//循环取最大的width和height；最大值一般是List的第一个元素
//				if (null != psizelist && 0 < psizelist.size()) {
//					int heights[] = new int[psizelist.size()];
//					int widths[] = new int[psizelist.size()];
//					for (int j = 0; j < psizelist.size(); j++) {
//						Size size = (Size) psizelist.get(j);
//						int sizehieght = size.height;
//						int sizewidth = size.width;
//						heights[j] = sizehieght;
//						widths[j] = sizewidth;
//					}
//					//取得最大的宽和高
//					int Height_Pixels = heights[0];
//					int width_Pixels = widths[0];
//					largestPixels = Height_Pixels * width_Pixels;
//					//遍历所有支持的像素，并取得最大
//					for (int j = 0; j < widths.length; j++) {
//						int temp = widths[j]*heights[j];
//						largestPixels = temp > largestPixels ? temp : largestPixels;
//					}
////					for (int j = 0; j < heights.length; j++) {
////						Height_Pixels = heights[j] > Height_Pixels ? heights[j] : Height_Pixels;
////					}
//					largestPixels = largestPixels/10000;
////					System.out.println(largestPixels);
//				}
//				camera.release();
//            //前置
//            } else if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
//				
//			}
//        }
//		return largestPixels+ "万像素";
//	}
//	
///**
// * 读取后置摄像头像素
// * @return 像素 int
// */
//	public String getFrontCameraInfo() {
//		// 摄像头个数
//		int numberOfCameras = Camera.getNumberOfCameras();
//		if (numberOfCameras < 2) {
//			return "不支持";
//		} else {
//			// 获取像素
//			CameraInfo cameraInfo = new CameraInfo();
//			int largestPixels = 0;
//			for (int i = 0; i < numberOfCameras; i++) {
//				Camera.getCameraInfo(i, cameraInfo);
//				// 后置
//				if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
//					
//					// 前置
//				} else if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
//					Camera camera = Camera.open(i);
//					Parameters parameters = camera.getParameters();
//					parameters.set("camera-id", 2);
//					// 获取到所有支持的图片像素
//					List<Size> psizelist = parameters.getSupportedPictureSizes();
//					// 循环取最大的width和height；最大值一般是List的第一个元素
//					if (null != psizelist && 0 < psizelist.size()) {
//						int heights[] = new int[psizelist.size()];
//						int widths[] = new int[psizelist.size()];
//						for (int j = 0; j < psizelist.size(); j++) {
//							Size size = (Size) psizelist.get(j);
//							int sizehieght = size.height;
//							int sizewidth = size.width;
//							heights[j] = sizehieght;
//							widths[j] = sizewidth;
//						}
////						// 取得最大的宽和高
////						int Height_Pixels = heights[0];
////						int width_Pixels = widths[0];
////						largestPixels = Height_Pixels * width_Pixels / 10000;
////						// System.out.println(largestPixels);
//						//取得最大的宽和高
//						int Height_Pixels = heights[0];
//						int width_Pixels = widths[0];
//						largestPixels = Height_Pixels * width_Pixels;
//						//遍历所有支持的像素，并取得最大
//						for (int j = 0; j < widths.length; j++) {
//							int temp = widths[j]*heights[j];
//							largestPixels = temp > largestPixels ? temp : largestPixels;
//						}
//						largestPixels = largestPixels/10000;
//					}
//					camera.release();
//				}
//			}
//			return largestPixels + "万像素";
//		}
//	}
	
/**
 * 查看是否支持闪光灯
 * @param context
 * @return
 */
	public String getCameraFlashSupport(Context context) {
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH) == false) {
			// Toast.makeText(this, "当前设备没有闪光灯", Toast.LENGTH_LONG).show();
			return "0";
		}
		return "1";
	}
	
}
