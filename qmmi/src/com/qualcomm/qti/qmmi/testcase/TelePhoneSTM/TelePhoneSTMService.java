/*
 * Copyright (c) 2017 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */
package com.qualcomm.qti.qmmi.testcase.TelePhoneSTM;

import android.content.Intent;
import android.content.res.Resources;

import com.qualcomm.qti.qmmi.R;
import com.qualcomm.qti.qmmi.bean.TestCase;
import com.qualcomm.qti.qmmi.framework.BaseService;
import com.qualcomm.qti.qmmi.utils.LogUtils;


public class TelePhoneSTMService extends BaseService implements TelePhoneSTMJNI.CmdControlCallback {
    TelePhoneSTMJNI mTelePhoneSTMJNI = null;
    boolean isResult = false;
    TestCase mTestCase = null;

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

    @Override
    public int run(TestCase testCase) {
        isResult = false;
        mTestCase = testCase;
        LogUtils.logi("TelePhoneSTMService service run");
        mTelePhoneSTMJNI.onStartTest();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(isResult ==false){
            UpdateResultInfo(false);
        }
        return 0;
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
