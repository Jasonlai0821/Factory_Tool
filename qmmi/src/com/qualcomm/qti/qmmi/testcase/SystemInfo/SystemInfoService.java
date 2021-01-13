/*
 * Copyright (c) 2017 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */
package com.qualcomm.qti.qmmi.testcase.SystemInfo;

import android.bluetooth.BluetoothAdapter;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.qualcomm.qti.qmmi.utils.Utils;

import com.qualcomm.qti.qmmi.R;
import com.qualcomm.qti.qmmi.bean.TestCase;
import com.qualcomm.qti.qmmi.framework.BaseService;
import com.qualcomm.qti.qmmi.model.HidlManager;
import com.qualcomm.qti.qmmi.service.diag.MmiDiagJNIInterface;
import com.qualcomm.qti.qmmi.utils.LogUtils;

import com.quectel.modemtool.ModemTool;
import com.quectel.modemtool.NvConstants;

import java.net.NetworkInterface;
import java.net.SocketException;


public class SystemInfoService extends BaseService {
    public static ModemTool modemTool = new ModemTool();
    public static String mSerialNo = null;
    /**
     * Get IMEI
     */
    public String getIMEI(int slotIndex) {
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        return telephonyManager.getImei(slotIndex);
    }

    /**
     * Get system version
     */
    private String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * Get bluetooth_act address
     */
    private String getBluetoothAddress() {
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth != null) {
            return bluetooth.getAddress();
        }

        return null;
    }

	/**
     * Get SerialNO
     */
	 
    public static String getSerialNO() {
        if(mSerialNo != null)
        {
            return mSerialNo;
        }

        String result = modemTool.sendAtCommand(NvConstants.REQUEST_SEND_AT_COMMAND,"AT+QCSN?");
		if(result == null || result.contains("ERROR")){
			result = null;
		}else if(result.contains("OK") && result.contains("+QCSN:")){
            Log.d("getSerialNO","result:"+result);
            if(result != null && result.length() > 22){
                int pos = result.indexOf("+QCSN: \"");
                String temp = result.substring(pos+8,pos+23);
                Log.d("getSerialNO","temp:"+temp);
                result = temp;
            }else{
                result = null;
            }
		}
		if(result != null){
            mSerialNo = result;
        }

        return result;
    }

	/**
     * Get Product version
     */
	 
    private String getPRVersion() {
        String result = null;

		result = Utils.getSystemProperties("ro.product.version", "0.0.0");
        return result;
    }

	/**
     * Get Product Model
     */
	 
    private String getPRModel() {
        String result = null;

		result = Utils.getSystemProperties("ro.product.model", "default");
        return result;
    }


	/**
	*Get Product VersionBuild
     */

	private String getVersionBuild() {
			String result = null;
	
			result = Utils.getSystemProperties("ro.build.version.incremental", "default");
			return result;
	}

	

    /**
     * Get wifi mac address
     */
    private String getWifiMacAddress() {
        WifiManager mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (mWifiManager != null) {
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                return wifiInfo.getMacAddress();
            }
        }

        return null;
    }

	/*
	 * 字节数组转16进制字符串
	 */
	public String byteHexString(byte[] array) {
	    StringBuilder builder = new StringBuilder();

	    for (byte b : array) {
	        String hex = Integer.toHexString(b & 0xFF);
	        if (hex.length() == 1) {
	            hex = '0' + hex;
	        }
	        builder.append(hex);
			if(b != array[array.length-1])
				builder.append(":");
	    }

	    return builder.toString().toUpperCase();
	}
		

	 /**
     * Get Ethernet mac address
     */
    private String getEthernetMacAddress() {
       	String ethernetMac = null;
	    try {
	        NetworkInterface NIC = NetworkInterface.getByName("eth0");
	        byte[] buf = NIC.getHardwareAddress();
	        ethernetMac = byteHexString(buf);
	    } catch (SocketException e) {
	        e.printStackTrace();
	    }
	    return ethernetMac;
    }

    /**
     * Get diag support status
     */
    private String diagSupportStatus() {
        return MmiDiagJNIInterface.isMmiJniLoaded ? this.getResources().getString(R.string.systemInfo_support)
                : this.getResources().getString(R.string.systemInfo_not_support);
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
    	LogUtils.logi("SystemInfo service run");

        //HidlManager hidlManager = HidlManager.getInstance();

        //   hidlManager.chargerEnable(true);
        //  hidlManager.enterShipMode();

        //  hidlManager.getSmbStatus();
        //hidlManager.chargerEnable(false);
        //hidlManager.enterShipMode();
        //hidlManager.wifiEnable(true);
	
        StringBuffer sb = new StringBuffer();
        Resources resources = this.getResources();
        sb.append(resources.getString(R.string.systemInfo_version)).append(getSystemVersion()).append("\n")
				.append(resources.getString(R.string.systemInfo_product_version)).append(getPRVersion()).append("\n")
				.append(resources.getString(R.string.systemInfo_product_model)).append(getPRModel()).append("\n")
				.append(resources.getString(R.string.systemInfo_version_build)).append(getVersionBuild()).append("\n")
                .append(resources.getString(R.string.systemInfo_modem)).append(Build.getRadioVersion()).append("\n")
                .append(resources.getString(R.string.systemInfo_serial)).append(getSerialNO()).append("\n")
                .append(resources.getString(R.string.systemInfo_imei1)).append(getIMEI(0)).append("\n")
                .append(resources.getString(R.string.systemInfo_imei2)).append(getIMEI(1)).append("\n")
                .append(resources.getString(R.string.systemInfo_bt_address)).append(getBluetoothAddress()).append("\n")
                .append(resources.getString(R.string.systemInfo_wifi_mac)).append(getWifiMacAddress()).append("\n")
                .append(resources.getString(R.string.systemInfo_ethernet_mac)).append(getEthernetMacAddress()).append("\n");

		LogUtils.logi("SystemInfo:" +sb.toString());			
	
        updateView(testCase.getName(), sb.toString());
        testCase.addTestData("version", getSystemVersion());
		testCase.addTestData("PRversion", getPRVersion());
		testCase.addTestData("PRModuel", getPRModel());
		testCase.addTestData("Vbuild", getVersionBuild());
        testCase.addTestData("modem", Build.getRadioVersion());
        testCase.addTestData("serial", getSerialNO());
        testCase.addTestData("imei1", getIMEI(0));
        testCase.addTestData("imei2", getIMEI(1));
        testCase.addTestData("bt_address", getBluetoothAddress());
        testCase.addTestData("wifi_mac", getWifiMacAddress());
        testCase.addTestData("ethernet_mac", getEthernetMacAddress());
        updateResultForCase(testCase.getName(), TestCase.STATE_PASS);

        return 0;
    }

}
