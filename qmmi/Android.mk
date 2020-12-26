TOP_LOCAL_PATH := $(call my-dir)
#
# Build JNI library
#
include $(TOP_LOCAL_PATH)/jni/Android.mk

#
# Build sample application package
#
LOCAL_PATH:= $(TOP_LOCAL_PATH)
include $(CLEAR_VARS)

LOCAL_POST_PROCESS_COMMAND := $(shell cp -r $(LOCAL_PATH)/lib/arm64-v8a/* $(TARGET_OUT)/lib64/)
LOCAL_POST_PROCESS_COMMAND := $(shell cp -r $(LOCAL_PATH)/lib/armeabi-v7a/* $(TARGET_OUT)/lib/)

LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_PACKAGE_NAME := Qmmi
LOCAL_CERTIFICATE := platform
LOCAL_PRIVATE_PLATFORM_APIS := true

LOCAL_SHARED_LIBRARIES := libqlmodem \
							libTelePhoneSTMJNI \
							libLcdMiniJNI

LOCAL_STATIC_JAVA_LIBRARIES := wigig_manager \
                               vendor.qti.hardware.sensorscalibrate-V1.0-java \
                               android.hidl.manager-V1.0-java \
                               android.hardware.light-V2.0-java \
                               vendor.qti.hardware.factory-V1.0-java \
                               vendor.qti.hardware.fingerprint-V1.0-java

LOCAL_JNI_SHARED_LIBRARIES := libmmi_jni 
							  

ifeq ($(TARGET_ARCH),arm)
LOCAL_PREBUILT_JNI_LIBS := \
@lib/armeabi-v7a/libqlmodem.so \
@lib/armeabi-v7a/libTelePhoneSTMJNI.so \
@lib/armeabi-v7a/libLcdMiniJNI.so
else ifeq ($(TARGET_ARCH),arm64)
LOCAL_PREBUILT_JNI_LIBS := \
@lib/armeabi-v8a/libqlmodem.so \
@lib/armeabi-v8a/libTelePhoneSTMJNI.so \
@lib/armeabi-v8a/libLcdMiniJNI.so
endif

#LOCAL_JNI_SHARED_LIBRARIES  += libqlmodem

LOCAL_MODULE_TAGS := debug optional

LOCAL_PROGUARD_ENABLED := disabled
include $(BUILD_PACKAGE)
