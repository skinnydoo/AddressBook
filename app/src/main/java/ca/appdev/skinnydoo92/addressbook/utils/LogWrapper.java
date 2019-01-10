package ca.appdev.skinnydoo92.addressbook.utils;

import android.util.Log;

/**
 * LogWrapper is used like android.util.Log but adds additional information like
 * method, file name, and line number
 */
public class LogWrapper {

	static String fileName;
	static String className;
	static String fullClassName;
	static String methodName;
	static int lineNumber;
	static String positionMsg;

	/*
	 * debug variable enables/disables all log messages to logcat
	 * Useful to disable prior to app store submission
	 */
	public static final boolean debug = true;

	/*
	 * trace
	 * Gathers the calling file, method, and line from the stack
	 * returns a string array with element 0 as file name and
	 * element 1 as method[line]
	 */
	private static String[] trace(StackTraceElement[] e, int level) {
		// Examine the method in which LogWrapper was used.

		if (e != null && e.length >= level) {

			StackTraceElement s = e[level];
			if (s != null) {

				fileName = s.getFileName();
				fullClassName = s.getClassName();
				className = fileName.substring(0, fileName.indexOf("."));
				methodName = s.getMethodName();
				lineNumber = s.getLineNumber();

				return new String[] {className, methodName + "[" + lineNumber + "] "};
			}
		}
		return null;
	}
	
	/*
	 * e method used to log passed error string and returns the
	 * calling file as the tag, method and line number prior
	 * to the string's message
	 */
	public static void e(String msg, Throwable thr) {
		
		if(!debug) return;
		
		String[] t = trace(Thread.currentThread().getStackTrace(), 3);
		Log.e(t[0], t[1] + msg, thr);
	}
	
	/*
	 * e method used to log passed error string and returns the
	 * calling file as the tag, method and line number prior
	 * to the string's message
	 */
	public static void e(String msg) {
		
		if(!debug) return;
		
		String[] t = trace(Thread.currentThread().getStackTrace(), 3);
		Log.e(t[0], t[1] + msg);
	}

	/*
	 * d method used to log passed debug string and returns the
	 * calling file as the tag, method and line number prior
	 * to the string's message
	 */
	public static void d(String msg) {
		
		if (!debug) return;
		
		String[] t = trace(Thread.currentThread().getStackTrace(), 3);
		Log.d(t[0], t[1] + msg);
		
	}

	/*
	 * d (tag, string)
	 * used to pass logging messages as normal but can be disabled
	 * when debug == false
	 */
	public static void d(String TAG, String msg) {
		
		if(!debug) return;
		
		Log.d(TAG, msg);
	}
	
	
	/*
	 * e method used to log passed error string and returns the
	 * calling file as the tag, method and line number prior
	 * to the string's message
	 */
	public static void wtf(String msg, Throwable thr) {
		
		if(!debug) return;
		
		String[] t = trace(Thread.currentThread().getStackTrace(), 3);
		Log.wtf(t[0], t[1] + msg, thr);
	}
	
	/*
	 * e method used to log passed error string and returns the
	 * calling file as the tag, method and line number prior
	 * to the string's message
	 */
	public static void wtf(String msg) {
		
		if(!debug) return;
		
		String[] t = trace(Thread.currentThread().getStackTrace(), 3);
		Log.wtf(t[0], t[1] + msg);
	}
}
