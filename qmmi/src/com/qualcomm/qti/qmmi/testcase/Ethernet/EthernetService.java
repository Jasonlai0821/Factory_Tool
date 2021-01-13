/*
 * Copyright (c) 2017 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */
package com.qualcomm.qti.qmmi.testcase.Ethernet;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import com.qualcomm.qti.qmmi.R;
import com.qualcomm.qti.qmmi.bean.TestCase;
import com.qualcomm.qti.qmmi.framework.BaseService;
import com.qualcomm.qti.qmmi.utils.LogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.StaticIpConfiguration;
import android.net.LinkProperties;
import android.os.SystemProperties;

public class EthernetService extends BaseService{
    boolean isResult = false;
    TestCase mTestCase = null;
    private ConnectivityManager mConnectivityManager = null;
    private EthernetManager mEthManager = null;

    public int onStartCommand(Intent intent, int flags, int startId) {
        mEthManager = (EthernetManager) this.getSystemService(Context.ETHERNET_SERVICE);
        mConnectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void register() {

    }

    @Override
    public int stop(TestCase testCase) {

        return 0;
    }


    public String getEthGateWay() {
        String ip = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET).getAddresses().toString();
        String mGW = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET).getRoutes().toString();
        //String mDns = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET).getDnses().toString();
        LogUtils.logi( "getEthGateWay ip=" + ip);
        LogUtils.logi( "getEthGateWay mGW=" + mGW);
        //LogUtils.logi("getEthGateWay mDns=" + mDns);
        if (mGW.contains(">")) {
            mGW = mGW.substring(mGW.lastIndexOf('>') + 2, mGW.length() - 1);
        }
        LogUtils.logi("getEthGateWay mGW=" + mGW);
        return mGW;
    }

    public void UpdateResultInfo(boolean state)
    {
        String IPAddress = null;
        String LostRatio = null;
        List<InetAddress> ipAddress = null;
        StringBuffer sb = new StringBuffer();
        Resources resources = this.getResources();
        //Map<String,String> Ethernetinfo = getIps(this);
        LinkProperties mLinkProperties = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
        if(mLinkProperties != null) {
            ipAddress = mLinkProperties.getAddresses();
            try {
                 LostRatio = getIPLossRatio();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            state = false;
        }

        if(ipAddress != null){
            IPAddress = ipAddress.toString();
        }else{
            state = false;
        }

        sb.append(resources.getString(R.string.ethernet_ipAddress)).append(IPAddress).append("\n")
              .append(resources.getString(R.string.ethernet_LossRatio)).append(LostRatio).append("\n");
//            .append(resources.getString(R.string.ethernet_gateWay)).append(Ethernetinfo.get("gateWay")).append("\n")
//            .append(resources.getString(R.string.ethernet_maskAddress)).append(Ethernetinfo.get("maskAddress")).append("\n")
//            .append(resources.getString(R.string.ethernet_dns)).append(Ethernetinfo.get("dns")).append("\n");

        updateView(mTestCase.getName(), sb.toString());
        mTestCase.addTestData("ipAddress", IPAddress);
        mTestCase.addTestData("lostRatio", LostRatio);
//        mTestCase.addTestData("gateWay", Ethernetinfo.get("gateWay"));
//        mTestCase.addTestData("maskAddress", Ethernetinfo.get("maskAddress"));
//        mTestCase.addTestData("dns", Ethernetinfo.get("dns"));

        if(state == false){
            updateResultForCase(mTestCase.getName(), TestCase.STATE_FAIL);
        }else{
            updateResultForCase(mTestCase.getName(), TestCase.STATE_PASS);
        }
    }

    public Map<String,String> getIps(Context context){
        Map<String,String> ipMaps = new HashMap<String,String>();
        try {
            String ETHERNET_SERVICE = (String) Context.class.getField("ETHERNET_SERVICE").get(null);
            Class<?> ethernetManagerClass = Class.forName("android.net.EthernetManager");
            Object ethernetManager = context.getSystemService(ETHERNET_SERVICE);
            Field mService = ethernetManagerClass.getDeclaredField("mService");
            mService.setAccessible(true);
            Object mServiceObject = mService.get(ethernetManager);
            Class<?> iEthernetManagerClass = Class.forName("android.net.IEthernetManager");

            Method[] methods = iEthernetManagerClass.getDeclaredMethods();
            for (Method ms : methods) {
                String methodName = ms.getName();
                LogUtils.logi("methodName:"+methodName);
                if("getGateway".equals(methodName)){
                    String gate = (String)ms.invoke(mServiceObject);
                    LogUtils.logi("gate:"+gate);
                    ipMaps.put("gateWay",gate);
                }else if("getNetmask".equals(methodName)){
                    String mask = (String)ms.invoke(mServiceObject);
                    LogUtils.logi("maskAddress:"+mask);
                    ipMaps.put("maskAddress",mask);
                }else if("getIpAddress".equals(methodName)){
                    String ipAddr = (String)ms.invoke(mServiceObject);
                    LogUtils.logi("ipAddress:"+ipAddr);
                    ipMaps.put("ipAddress",ipAddr);
                }else if("getDns".equals(methodName)){
                    String dnss = (String)ms.invoke(mServiceObject);
                    String []arrDns = dnss.split("\\,");
                    String dns = null;
                    if(arrDns != null){
                        dns = arrDns[0];
                        ipMaps.put("dns",dns);
                    }
                    LogUtils.logi("dns:"+dns);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ipMaps;
    }

    public String getIPLossRatio() throws IOException {
        String result = null;
        String lost = new String();
        String delay = new String();
        Process p = Runtime.getRuntime().exec("ping -c 5 " + "192.168.61.151");
        BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String str = new String();
        while((str=buf.readLine())!=null){
            if(str.contains("packet loss")){
                int i= str.indexOf("received");
                int j= str.indexOf("%");
                lost = str.substring(i+10, j+1);
            }
            if(str.contains("avg")){
                int i=str.indexOf("/", 20);
                int j=str.indexOf(".", i);
                delay =str.substring(i+1, j);
                delay = delay+"ms";
            }
        }
        result = lost + " avg:"+delay;
        return result;
    }

    @Override
    public int run(TestCase testCase) {
        isResult = false;
        mTestCase = testCase;
        UpdateResultInfo(true);
        return 0;
    }

}
