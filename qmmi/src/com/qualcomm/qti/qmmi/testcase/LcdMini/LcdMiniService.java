/*
 * Copyright (c) 2017 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */
package com.qualcomm.qti.qmmi.testcase.LcdMini;

import android.content.Intent;
import android.content.res.Resources;

import com.qualcomm.qti.qmmi.R;
import com.qualcomm.qti.qmmi.bean.TestCase;
import com.qualcomm.qti.qmmi.framework.BaseService;
import com.qualcomm.qti.qmmi.utils.LogUtils;


public class LcdMiniService extends BaseService{
    boolean isResult = false;
    TestCase mTestCase = null;
    LcdMiniJNI mLcdMiniJNI = null;

    public int onStartCommand(Intent intent, int flags, int startId) {
        mLcdMiniJNI = new LcdMiniJNI(this);
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
        sb.append(resources.getString(R.string.lcd_mini_display)).append("\n");
        updateView(mTestCase.getName(), sb.toString());
        mTestCase.addTestData("LCDMini", result);
        if(state == false){
            updateResultForCase(mTestCase.getName(), TestCase.STATE_FAIL);
        }else{
            updateResultForCase(mTestCase.getName(), TestCase.STATE_PASS);
        }
    }

    @Override
    public int run(TestCase testCase) {
        LogUtils.logi("LcdMiniService service run");
		mTestCase = testCase;
        int ret = -1;
        if(mLcdMiniJNI != null){
           ret = mLcdMiniJNI.onDispLcdTest();
        }

        if(ret == 0){
            UpdateResultInfo(true);
        }else{
            UpdateResultInfo(false);
        }
        return 0;
    }

}
