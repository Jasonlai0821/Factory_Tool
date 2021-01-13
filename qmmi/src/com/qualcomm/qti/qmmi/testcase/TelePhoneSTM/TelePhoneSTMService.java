/*
 * Copyright (c) 2017 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */
package com.qualcomm.qti.qmmi.testcase.TelePhoneSTM;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import com.qualcomm.qti.qmmi.R;
import com.qualcomm.qti.qmmi.bean.TestCase;
import com.qualcomm.qti.qmmi.framework.BaseService;
import com.qualcomm.qti.qmmi.utils.LogUtils;


public class TelePhoneSTMService extends BaseService implements TelePhoneSTMJNI.CmdControlCallback {
    TelePhoneSTMJNI mTelePhoneSTMJNI = null;
    boolean isResult = false;
    TestCase mTestCase = null;
    static boolean isStarted = false;

    public int onStartCommand(Intent intent, int flags, int startId) {
        mTelePhoneSTMJNI = new TelePhoneSTMJNI(this);
        mTelePhoneSTMJNI.setCmdControlCallback(this);

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void register() {

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
        String result = null;
        StringBuffer sb = new StringBuffer();
        Resources resources = this.getResources();
        if(state == false){
            result = "Falied";
        }else{
            result = "Success";
        }
        sb.append(resources.getString(R.string.telephone_stm)).append(result).append("\n");
        updateView(mTestCase.getName(), sb.toString());
        mTestCase.addTestData("TelePhoneSTM", result);
        if(state == false){
            updateResultForCase(mTestCase.getName(), TestCase.STATE_FAIL);
        }else{
            updateResultForCase(mTestCase.getName(), TestCase.STATE_PASS);
        }
    }

    public void UpdateTelePhoneVoip(boolean state)
    {
        LogUtils.logi("UpdateTelePhoneVoip()");
        String result = null;
        StringBuffer sb = new StringBuffer();
        Resources resources = this.getResources();
        if(state == false){
            result = "Falied";
        }else{
            result = "Success";
        }
        sb.append(resources.getString(R.string.telephone_viop));
        mTestCase.addTestData("TelePhone VOIP", result);
        if(state == false){
            updateResultForCase(mTestCase.getName(), TestCase.STATE_FAIL);
        }else{
            updateResultForCase(mTestCase.getName(), TestCase.STATE_PASS);
        }
        updateView(mTestCase.getName(), sb.toString());
    }

    @Override
    public int run(TestCase testCase) {
        isResult = false;
        mTestCase = testCase;
        String method = mTestCase.getMethod();
        LogUtils.logi("TelePhoneSTMService service run");
        if(method != null && method.equalsIgnoreCase("stm")){
            mTelePhoneSTMJNI.onStartTest();
            try {
                Thread.sleep(1000);
                //mTelePhoneSTMJNI.finalize();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            if(isResult ==false){
                UpdateResultInfo(false);
            }
        }else if(method != null && method.equalsIgnoreCase("voip")){
            if(isStarted == false){
                mTestCase.setThirdState("start");
                isStarted = startTelePhoneServer();

                UpdateTelePhoneVoip(isStarted);
            }
        }

        return 0;
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

    public boolean startTelePhoneServer()
    {
        LogUtils.logi("startTelePhoneServer");
        String packagename = "com.xinshiyun.telephoneserver";
        if(isPkgInstalled(packagename) ==false){
            return false;
        }

        Intent intent2 = this.getPackageManager()
                .getLaunchIntentForPackage(packagename);
        String classNameString = intent2.getComponent().getClassName();
        Intent intent  = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.setComponent(new ComponentName(packagename, classNameString));
        this.startActivity(intent);
        return true;
    }


    @Override
    public void onCmdExcuteResult(int cmd_type, int value) {
        isResult = true;
        if(value == TelePhoneSTMJNI.TEST_FAIL){
            UpdateResultInfo(false);
        }else if(value == TelePhoneSTMJNI.TEST_PASS){
            UpdateResultInfo(true);
        }
    }
}
