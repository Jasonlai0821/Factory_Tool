/*
 * Copyright (c) 2017 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */
package com.qualcomm.qti.qmmi.testcase.Hdmiout;

import android.content.Intent;
import android.content.res.Resources;
import com.qualcomm.qti.qmmi.bean.TestCase;
import com.qualcomm.qti.qmmi.framework.BaseService;
import com.qualcomm.qti.qmmi.utils.LogUtils;


public class HdmioutService extends BaseService{
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
		sb.append("Hdmi Out");
        mTestCase.addTestData("Hdmi_Out", result);
        if(state == false){
            updateResultForCase(mTestCase.getName(), TestCase.STATE_FAIL);
        }else{
            updateResultForCase(mTestCase.getName(), TestCase.STATE_PASS);
        }
        LogUtils.logi("UpdateResultInfo() sb:"+sb.toString());
        updateView(mTestCase.getName(), sb.toString());
    }

    @Override
    public int run(TestCase testCase) {
        LogUtils.logi("HdmioutService service run isStarted:"+isStarted);
		mTestCase = testCase;

        return 0;
    }

}
