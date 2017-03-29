package com.andrew.systemservice;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.io.File;
import java.lang.reflect.Method;

public class SystemService extends Service {
	// 电话管理器
	private TelephonyManager tm;
	// 监听器对象
	private MyListener listener;


	private boolean recording = false;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 服务创建的时候调用的方法
	 */
	@Override
	public void onCreate() {
		// 后台监听电话的呼叫状态。
		// 得到电话管理器
		tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}
	//shuangka
	public static final String[] dualSimTypes = { "subscription", "Subscription",
			"com.android.phone.extra.slot",
			"phone", "com.android.phone.DialingMode",
			"simId", "simnum", "phone_type",
			"simSlot" };
	//shuangka
	private class MyListener extends PhoneStateListener {
		// 当电话的呼叫状态发生变化的时候调用的方法
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			try {
				switch (state) {
					case TelephonyManager.CALL_STATE_IDLE://空闲状态。

						break;
					case TelephonyManager.CALL_STATE_RINGING://零响状态。
						stop(incomingNumber);
						break;
					case TelephonyManager.CALL_STATE_OFFHOOK://通话状态
						break;
					default:
						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	//电话拦截17707143101
	public void stop(String s) {
		try {
			if (s.equals("17707143101")) {
				//Toast.makeText(this, "拦截成功", 0).show();
				Log.e("TAG", "此来电为黑名单号码，已被拦截！");
				//调用ITelephony.endCall()结束通话
				Method method = Class.forName("android.os.ServiceManager")
						.getMethod("getService", String.class);
				IBinder binder = (IBinder) method.invoke(null,
						new Object[] { TELEPHONY_SERVICE });
				ITelephony telephony = ITelephony.Stub.asInterface(binder);
				telephony.endCall();

				//用intent启动拨打电话
				try {
					Thread.currentThread().sleep(3000);//
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//shuangka
				Intent callIntent = new Intent(Intent.ACTION_CALL)
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				callIntent.setData(Uri.parse("tel:" + s));
				for (int i=0; i < dualSimTypes.length; i++) {
					callIntent.putExtra(dualSimTypes[i], 2);
				}
				Log.e("TAG", "zhunbstartActivity拨打电话");
				startActivity(callIntent);
				Log.e("TAG", "startActivity拨打电话");


			} else

				Log.e("TAG", "phone不需拦截");
			recording=false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 服务销毁的时候调用的方法
	 */
	@Override
	public void onDestroy() {

	}

}
