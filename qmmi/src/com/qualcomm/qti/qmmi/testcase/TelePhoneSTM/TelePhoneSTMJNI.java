package com.qualcomm.qti.qmmi.testcase.TelePhoneSTM;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.qualcomm.qti.qmmi.utils.LogUtils;

import java.lang.ref.WeakReference;

public class TelePhoneSTMJNI {
    private static String TAG = TelePhoneSTMJNI.class.getSimpleName();

    private EventHandler mEventHandler;
    private Context mContext;
    private CmdControlCallback mCmdControlCallback = null;

    private long mNativeContext; // accessed by native methods

    public static final int    TEST_PASS = 0x55;
    public static final int    TEST_FAIL = 0xaa;

    private static final int 	START_TEST  = 0x0;
    private static final int 	KT113_1_TEST = 0x1;
    private static final int    KT113_2_TEST = 0x2;
    private static final int    AC483_TEST = 0x3;
    private static final int    STM32_TEST = 0x4;


    private int kt113_1_test_result = -1;
    private int kt113_2_test_result = -1;
    private int ac483_test_result = -1;
    private int stm32_test_result = -1;

    private static final int sum_test_num = 4;
    private int pass_test_num = 0;
    private int fail_test_num = 0;

    private class EventHandler extends Handler {
        private TelePhoneSTMJNI mProductTestJNI;

        public EventHandler(TelePhoneSTMJNI dc, Looper looper) {
            super(looper);
            mProductTestJNI = dc;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mProductTestJNI.mNativeContext == 0) {
                LogUtils.logi("Display Control went away with unhandled events");
                return;
            }

            switch (msg.what) {
                case START_TEST:
                    pass_test_num = 0;
                    fail_test_num = 0;
                    LogUtils.logi("Test Start...");
                    break;

                case KT113_1_TEST:
                    kt113_1_test_result = msg.arg2;
                    if (TEST_PASS == msg.arg2) {
                        pass_test_num ++;
                        LogUtils.logi("KT113_1:test pass!");
                    }
                    if (TEST_FAIL == msg.arg2) {
                        fail_test_num ++;
                        LogUtils.logi("KT113_1:test fail!!!");
                    }
                    break;
                case KT113_2_TEST:
                    kt113_2_test_result = msg.arg2;
                    if (TEST_PASS == msg.arg2) {
                        pass_test_num ++;
                        LogUtils.logi("KT113_2:test pass!");
                    }
                    if (TEST_FAIL == msg.arg2) {
                        fail_test_num ++;
                        LogUtils.logi("KT113_2:test fail!!!");
                    }
                    break;
                case AC483_TEST:
                    ac483_test_result = msg.arg2;
                    if (TEST_PASS == msg.arg2) {
                        pass_test_num ++;
                        LogUtils.logi( "AC483:test pass!");
                    }
                    if (TEST_FAIL == msg.arg2) {
                        fail_test_num ++;
                        LogUtils.logi( "AC483:test fail!!!");
                    }
                    break;
                case STM32_TEST:
                    stm32_test_result = msg.arg2;
                    if (TEST_PASS == msg.arg2) {
                        pass_test_num ++;
                        LogUtils.logi("STM32:test pass!");
                    }
                    if (TEST_FAIL == msg.arg2) {
                        fail_test_num ++;
                        LogUtils.logi("STM32:test fail!!!");
                    }
                    break;
                default:
                    LogUtils.logi("message error!!!");
            }
            if (sum_test_num == pass_test_num) {
                LogUtils.logi("All test pass!!!");
                if(mCmdControlCallback != null){
                    mCmdControlCallback.onCmdExcuteResult(0x00,TEST_PASS);
                }
            }
            if((sum_test_num == (pass_test_num + fail_test_num))&&(0 < fail_test_num)){
                LogUtils.logi(fail_test_num +" test fail!!!");
                if(mCmdControlCallback != null){
                    mCmdControlCallback.onCmdExcuteResult(0x00,TEST_FAIL);
                }
            }
        }
    }

    static {
        System.loadLibrary("TelePhoneSTMJNI");
        native_init();
    }

    public TelePhoneSTMJNI(Context context) {
        Looper looper;
        if ((looper = Looper.myLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else if ((looper = Looper.getMainLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else {
            mEventHandler = null;
        }

        native_setup(new WeakReference<TelePhoneSTMJNI>(this));
        mContext =context;
    }

    /*
     * Called from native code when an interesting event happens. This method
     * just uses the EventHandler system to post the event back to the main app
     * thread. We use a weak reference to the original DisplayControl object so
     * that the native code is safe from the object disappearing from underneath
     * it. (This is the cookie passed to native_setup().)
     */
    @SuppressWarnings("rawtypes")
    public static void postEventFromNative(Object dc_ref, int what, int arg1,int arg2) {
        TelePhoneSTMJNI dc = (TelePhoneSTMJNI) ((WeakReference) dc_ref).get();
        if (dc == null) {

        }
        if (dc.mEventHandler != null) {
            Message m = dc.mEventHandler.obtainMessage(what, arg1, arg2);
            dc.mEventHandler.sendMessage(m);
        }
        //return 0;
    }

    public int onStartTest()
    {
        String cmd = "5C"+"05"+"00";
        return onExecuteCmd(cmd);
    }

    private static native final void native_init();

    private native final void native_setup(Object dc_this);

    private native void native_finalize();

    public native int onExecuteCmd(String cmd);

    public void finalize() throws Throwable {
        // TODO Auto-generated method stub
        super.finalize();
        if (mNativeContext != 0) {
            native_finalize();
        }
        mNativeContext = 0L;
    }

    public void setCmdControlCallback(CmdControlCallback callback){
        mCmdControlCallback = callback;
    }

    public interface CmdControlCallback{
        void onCmdExcuteResult(int cmd_type,int value);
    }
}
