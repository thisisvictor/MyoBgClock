/*
 * Copyright (C) 2014 Thalmic Labs Inc.
 * Distributed under the Myo SDK license agreement. See LICENSE.txt for details.
 */

package com.thalmic.android.sample.background;

import java.util.Calendar;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Myo.VibrationType;
import com.thalmic.myo.Pose;

public class BgClockService extends Service {
    private static final String TAG = "BgClockService";

    private Toast mToast;
    private Pose prevPose = Pose.UNKNOWN;
    private boolean isUnlocked = false;

    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener() {
        @Override
        public void onConnect(Myo myo, long timestamp) {
            showToast(getString(R.string.connected));
        }

        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            showToast(getString(R.string.disconnected));
        }

        // onPose() is called whenever the Myo detects that the person wearing it has changed their pose, for example,
        // making a fist, or not making a fist anymore.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Show the name of the pose in a toast.
            //showToast(getString(R.string.pose, pose.toString()));
            
            //unlock if a fist pose is made
            if(prevPose==Pose.FIST && pose==Pose.REST) {
            	//showToast("unlock");
            	unlock();
            	myo.vibrate(VibrationType.SHORT);
            } else if(isUnlocked && prevPose==Pose.FINGERS_SPREAD&& pose==Pose.REST) {
            	//showToast("tell time");
            	myo.vibrate(VibrationType.SHORT);
            	myo.vibrate(VibrationType.SHORT);
            	tellTime(myo, Calendar.getInstance());
            }
            
            prevPose = pose;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            showToast("Couldn't initialize Hub");
            stopSelf();
            return;
        }

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);

        // Finally, scan for Myo devices and connect to the first one found.
        //hub.pairWithAnyMyo();
        hub.attachToAdjacentMyo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Service is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);

        Hub.getInstance().shutdown();
    }

    private void showToast(String text) {
        Log.w(TAG, text);
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }
    
    private void unlock() {
    	isUnlocked = true;
    	//lock again after 1sec
    	new CountDownTimer(1000, 1000) {
    		public void onTick(long millisUntilFinished) {
    			isUnlocked = false;
    		}
    		public void onFinish() {
    			isUnlocked = false;
    			//showToast("locked");
    		}
    	}.start();
    }
    
    private void tellTime(final Myo myo, final Calendar rightNow) {
    	//wait for 1sec, then tell time
    	new CountDownTimer(1000, 1000) {
    		public void onTick(long millisUntilFinished) {
    			//do nothing
    		}
    		public void onFinish() {
    			//buzz the sequence for hour (12H)
    			for(int i=0; i<rightNow.get(Calendar.HOUR)%5; i++) {
    				myo.vibrate(VibrationType.SHORT);
    			}
    			if(rightNow.get(Calendar.HOUR)>=5) myo.vibrate(VibrationType.MEDIUM);
    			if(rightNow.get(Calendar.HOUR)>=10) myo.vibrate(VibrationType.MEDIUM);
    			
    			tellMinute(myo, rightNow);
    		}
    	}.start();
    }
    
    private void tellMinute(final Myo myo, final Calendar rightNow) {
    	//wait for 1.8sec, then tell the minute
    	new CountDownTimer(1800, 1800) {
    		public void onTick(long millisUntilFinished) {
    			//do nothing
    		}
    		public void onFinish() {
    			//buzz the sequence for the first digit of the minute
    			for(int i=0; i<rightNow.get(Calendar.MINUTE)/10; i++) {
    				myo.vibrate(VibrationType.SHORT);
    			}
    			
    			tellLastDigit(myo, rightNow);
    		}
    	}.start();
    }
    
    private void tellLastDigit(final Myo myo, final Calendar rightNow) {
    	new CountDownTimer(1800, 1800) {
    		public void onTick(long millisUntilFinished) {
    			//do nothing
    		}
    		public void onFinish() {
    			//buzz the sequence for the last digit of the minute
    			for(int i=0; i<rightNow.get(Calendar.MINUTE)%10%5; i++) {
    				myo.vibrate(VibrationType.SHORT);
    			}
    			if(rightNow.get(Calendar.MINUTE)%10>=5) myo.vibrate(VibrationType.MEDIUM);
    		}
    	}.start();
    }
}
