/*
 * Copyright (c) 2017 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */
package com.qualcomm.qti.qmmi.testcase.AudioInOut;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import com.qualcomm.qti.qmmi.R;
import com.qualcomm.qti.qmmi.bean.TestCase;
import com.qualcomm.qti.qmmi.framework.BaseService;
import com.qualcomm.qti.qmmi.utils.LogUtils;


public class AudioInOutService extends BaseService{
    static boolean isStarted = false;
    TestCase mTestCase = null;

    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.logi("onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void register() {
        LogUtils.logi("register service");
    }

    @Override
    public int stop(TestCase testCase) {
        LogUtils.logi("stop service");
        if(testCase.getThirdState() != null && testCase.getThirdState().equals("stop")){
            isStarted = false;
        }
        return 0;
    }

    public void UpdateResultInfo(boolean state)
    {
    	LogUtils.logi("UpdateResultInfo()");
        String result = null;
        StringBuffer sb = new StringBuffer();
        Resources resources = this.getResources();
        if(state == false){
            result = "Falied";
        }else{
            result = "Success";
        }
		sb.append("Audio In & Out Test");
        mTestCase.addTestData("AudioInOut", result);
        if(state == false){
            updateResultForCase(mTestCase.getName(), TestCase.STATE_FAIL);
        }else{
            updateResultForCase(mTestCase.getName(), TestCase.STATE_PASS);
        }
		 updateView(mTestCase.getName(), sb.toString());
    }

    private boolean isPkgInstalled(String pkgName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = this.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean startSuperRecorder()
    {
    	LogUtils.logi("startSuperRecorder");
        String packagename = "com.tianxingjian.superrecorder";
        if(isPkgInstalled(packagename) ==false){
            return false;
        }

        Intent intent2 = this.getPackageManager()
                .getLaunchIntentForPackage(packagename);
        String classNameString = intent2.getComponent().getClassName();//得到app类名
        Intent intent  = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setComponent(new ComponentName(packagename, classNameString));
        this.startActivity(intent);
        return true;
    }


    @Override
    public int run(TestCase testCase) {
        LogUtils.logi("AudioInOutService service run isStarted:"+isStarted);
		mTestCase = testCase;
        if(isStarted == false){
			mTestCase.setThirdState("start");
            isStarted = startSuperRecorder();
        }else{

        }

		UpdateResultInfo(isStarted);

        /*while(isStarted){
			if(isStarted == false){
                break;
            }
        }*/

        return 0;
    }

}
