/*
 * Copyright (c) 2017 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */
package com.qualcomm.qti.qmmi.testcase.Ethernet;

import android.content.Intent;
import android.content.res.Resources;

import com.qualcomm.qti.qmmi.R;
import com.qualcomm.qti.qmmi.bean.TestCase;
import com.qualcomm.qti.qmmi.framework.BaseService;
import com.qualcomm.qti.qmmi.utils.LogUtils;


public class EthernetService extends BaseService{
    boolean isResult = false;
    TestCase mTestCase = null;

    public int onStartCommand(Intent intent, int flags, int startId) {

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

        return 0;
    }

}
