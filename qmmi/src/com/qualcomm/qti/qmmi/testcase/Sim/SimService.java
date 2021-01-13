/*
 * Copyright (c) 2017 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */
package com.qualcomm.qti.qmmi.testcase.Sim;


import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import com.qualcomm.qti.qmmi.R;
import com.qualcomm.qti.qmmi.bean.TestCase;
import com.qualcomm.qti.qmmi.framework.BaseService;
import com.qualcomm.qti.qmmi.utils.LogUtils;

public class SimService extends BaseService {

    TelephonyManager mTelephonyManager = null;
    private TestCase mTestCase = null;

    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.logi( "onStartCommand");
        mTelephonyManager = TelephonyManager.from(getApplicationContext());
        if (mTelephonyManager == null) {
            LogUtils.loge( "No mWifiManager service here");
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void register() {

    }

    @Override
    public int stop(TestCase testCase) {
        return 0;
    }

    @Override
    public int run(TestCase testCase) {
        mTestCase = testCase;
        int sub = Integer.valueOf(testCase.getParameter().get("sub"));
        StringBuffer sb = new StringBuffer();
        String iccId = null;
        sb.append(this.getResources().getString(R.string.sim_list)).append("\n");

        try {
            if (mTelephonyManager.isMultiSimEnabled()) {
                int[] subId = SubscriptionManager.getSubId(sub);
                iccId = mTelephonyManager.getSimSerialNumber(subId[0]);
            } else {
                iccId = mTelephonyManager.getSimSerialNumber();
            }

            createPhoneStateListener(sub);
            LogUtils.logi("iccId:" + iccId);
            if (iccId != null && !iccId.equals("")) {

                String simSlot = this.getResources().getString(R.string.sim_slot);
                SignalStrength signalStrength = mTelephonyManager.getSignalStrength();
                int signalDbm = signalStrength.getDbm();
                int signalAsu = signalStrength.getAsuLevel();

                sb.append(String.format(simSlot, sub+1))
                    .append(": ")
                    .append(this.getResources().getString(R.string.sim_deteched)).append("\n")
                    .append(this.getResources().getString(R.string.sim_deviceid)).append(mTelephonyManager.getDeviceId(sub)).append("\n")
                    .append(this.getResources().getString(R.string.sim_telenumber)).append(mTelephonyManager.getLine1Number()).append("\n")
                    .append(this.getResources().getString(R.string.sim_serialnumber)).append(mTelephonyManager.getSimSerialNumber(sub)).append("\n")
                    .append(this.getResources().getString(R.string.sim_userid)).append(mTelephonyManager.getSubscriberId()).append("\n")
                    .append(this.getResources().getString(R.string.sim_signalstrength))
                    .append(String.valueOf(signalDbm)).append(" dBm").append(" ")
                    .append(String.valueOf(signalAsu)).append("asu")
                    .append("\n");
                testCase.addTestData("SIM" + sub, "deteched");
                testCase.addTestData("dBm" + sub, String.valueOf(signalDbm));
                testCase.addTestData("asu" + sub, String.valueOf(signalAsu));
                updateResultForCase(testCase.getName(), TestCase.STATE_PASS);
            }else{

                String simSlot = this.getResources().getString(R.string.sim_slot);

                sb.append(String.format(simSlot, sub+1))
                .append(": ")
                .append(this.getResources().getString(R.string.sim_not_deteched))
                .append("\n");
                testCase.addTestData("SIM" + sub, "not deteched");
                updateResultForCase(testCase.getName(), TestCase.STATE_FAIL);
            }
        }catch (SecurityException e){
            LogUtils.logi( "getSimSerialNumber error:" + e);
        }

        updateView(testCase.getName(), sb.toString());
        LogUtils.logi( "simservice run");
        return 0;
    }

    public void updateSignalStrength(SignalStrength signalStrength){
        LogUtils.logi( "updateSignalStrength()");
        StringBuffer sb = new StringBuffer();
        int signalDbm = signalStrength.getDbm();
        int signalAsu = signalStrength.getAsuLevel();

        LogUtils.logi( "updateSignalStrength() signalDbm"+signalDbm);

        LogUtils.logi( "updateSignalStrength() signalAsu"+signalAsu);
        if (-1 == signalAsu) signalAsu = 0;

        String simSlot = this.getResources().getString(R.string.sim_slot);
        int sub = Integer.valueOf(mTestCase.getParameter().get("sub"));

        sb.append(String.format(simSlot, sub+1))
                .append(": ")
                .append(this.getResources().getString(R.string.sim_deteched)).append("\n")
                .append(this.getResources().getString(R.string.sim_signalstrength))
                .append(String.valueOf(signalDbm)).append(" dBm").append(" ")
                .append(String.valueOf(signalAsu)).append("asu");
        mTestCase.addTestData("SIM" + sub, "deteched");
        mTestCase.addTestData("dBm" + sub, String.valueOf(signalDbm));
        mTestCase.addTestData("asu" + sub, String.valueOf(signalAsu));
        try{
            updateResultForCase(mTestCase.getName(), TestCase.STATE_PASS);

            updateView(mTestCase.getName(), sb.toString());
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    public PhoneStateListener createPhoneStateListener(int subId){
        LogUtils.logi( "createPhoneStateListener()");
        return new PhoneStateListener(subId){
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                LogUtils.logi( "onSignalStrengthsChanged()");

                updateSignalStrength(signalStrength);
            }
        };
    }

}
