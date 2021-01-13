/*
 * Copyright (c) 2017 Qualcomm Technologies, Inc.
 * All Rights Reserved.
 * Confidential and Proprietary - Qualcomm Technologies, Inc.
 */
package com.qualcomm.qti.qmmi.model;

import android.os.Environment;

import com.qualcomm.qti.qmmi.bean.TestCase;
import com.qualcomm.qti.qmmi.testcase.SystemInfo.SystemInfoService;
import com.qualcomm.qti.qmmi.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ResultThread {

    final Object mSyncLock = new Object();
    private List<TestCase> mTestCaseList;
    private String filePath;

    public ResultThread() {
    }

    public ResultThread(List<TestCase> mTestCaseList, String filePath) {
        this.mTestCaseList = mTestCaseList;
        this.filePath = filePath;

    }

    public void setTestCaseList(List<TestCase> mTestCaseList) {
        this.mTestCaseList = mTestCaseList;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return this.filePath;
    }

    //** flush the result to file
    public void flushResult() {
        synchronized (mSyncLock) {
            mSyncLock.notify();
        }
    }

    public void start() {
        Thread t = new Thread(new PersistRunnable(mSyncLock));
        t.start();
    }

    private class PersistRunnable implements Runnable {
        private Object obj;

        public PersistRunnable(Object obj) {
            this.obj = obj;
        }

        public void run() {
            for (; ; ) {
                synchronized (obj) {
                    try {
                        System.out.println("wait notify come to start" + Thread.currentThread().getName());
                        obj.wait();
                        ResultParser.saveResultToFile(filePath, mTestCaseList);
                        /*if(SystemInfoService.getSerialNO() == null || SystemInfoService.getSerialNO().equalsIgnoreCase("null")){
                            System.out.println("the devices serial number is null!!!");
                        }else{
                            //copy the file to media
                            Date dt = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm_E");
                            String str_time = sdf.format(dt);
                            String resultpath = null;

                            //String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_NOTIFICATIONS).getPath();
                            String storageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() +"/Qmmi";
                            File testresultFile = new File(storageDirectory);
                            if (!testresultFile.exists()) {
                                testresultFile.mkdirs();
                            }
                            resultpath = storageDirectory+"/"+SystemInfoService.getSerialNO()+str_time+".xml";
                            if(filePath != null && resultpath != null){
                                Utils.copyFile(filePath,resultpath);
                            }
                        }*/
                    } catch (Exception exc) {
                        exc.printStackTrace(System.out);
                    }
                }
            }
        }
    }

}
