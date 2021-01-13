package com.qualcomm.qti.qmmi.utils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.io.IOException;

public class FTPClientUtils {
    private static final String TAG = FTPClientUtils.class.getSimpleName();

    private static FTPClient mFTPClient = null;

    private String FTP_SERVER = "192.168.61.151";
    private String FTP_USERNAME = "ftpguest";
    private String FTP_PASSWORD = "ftpguest";
    private int FTP_PORT = 21;
    private String FTP_DIR ="/ftp/Project/XTS100/Test_Result_Report/Qmmi_Test_Report";

    public boolean ftpConnect(String host, String username, String password, int port) {
        try {
            mFTPClient = new FTPClient();
            LogUtils.logi("ftpConnect() connecting to the ftp server " + host + " ：" + port);
            mFTPClient.connect(host, port);

            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                LogUtils.logi("login to the ftp server");
                boolean status = mFTPClient.login(username, password);
                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();
                return status;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.loge("Error: could not connect to host " + host);
        }
        return false;
    }

    public boolean ftpDisconnect() {
        if (mFTPClient == null) {
            return true;
        }

        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
            mFTPClient = null;
            return true;
        } catch (Exception e) {
            LogUtils.loge("Error occurred while disconnecting from ftp server.");
        }
        return false;
    }

    public boolean ftpUpload(String srcFilePath, String desFileName, String desDirectory) {
        boolean status = false;
        try {
            ftpChangeDir(desDirectory);
            FileInputStream srcFileStream = new FileInputStream(srcFilePath);
            status = mFTPClient.storeFile(desFileName, srcFileStream);
            srcFileStream.close();
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.loge("upload failed: " + e.getLocalizedMessage());
        }
        return status;
    }

    public boolean ftpChangeDir(String path) {
        boolean status = false;
        try {
            status = mFTPClient.changeWorkingDirectory(path);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.loge("change directory failed: " + e.getLocalizedMessage());
        }
        return status;
    }

    public void onStartUpload(String sourceFilePath,String descFileName,String desDirectory)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean connectResult = ftpConnect(FTP_SERVER, FTP_USERNAME, FTP_PASSWORD, FTP_PORT);
                if (connectResult) {
                    boolean changeDirResult = ftpChangeDir(FTP_DIR);
                    if (changeDirResult) {
                        try {
                            mFTPClient.makeDirectory(desDirectory);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        boolean uploadResult = ftpUpload(sourceFilePath, descFileName, desDirectory);
                        if (uploadResult) {
                            LogUtils.logi("Upload file success");
                            boolean disConnectResult = ftpDisconnect();
                            if(disConnectResult) {
                                LogUtils.logi("Close FTP Connect success");
                            } else {
                                LogUtils.loge("Close FTP Connect falied");
                            }
                        } else {
                            LogUtils.loge("Upload file failed");
                        }
                    } else {
                        LogUtils.loge("Change FTP Directory failed");
                    }

                } else {
                    LogUtils.loge("Connect FTP Server failed");
                }
            }
        }).start();
    }
}

