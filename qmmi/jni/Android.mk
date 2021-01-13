LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
    MmiDiagJNIInterface.cpp

LOCAL_C_INCLUDES += \
    $(JNI_H_INCLUDE) \
    $(TARGET_OUT_HEADERS)/common/inc \
    $(TARGET_OUT_HEADERS)/diag/include \
    $(TOP)/libnativehelper/include/nativehelper

LOCAL_SHARED_LIBRARIES := \
    libutils \
    libcutils \
    libdiag_system \
    liblog

LOCAL_MODULE:= libmmi_jni
LOCAL_MODULE_TAGS := optional
LOCAL_LDLIBS := -llog
LOCAL_CFLAGS += -Wall -Werror
LOCAL_CFLAGS += -Wno-unused-const-variable \
                                -Wno-null-conversion \
                                -Wno-unused-variable \
                                -Wno-unused-parameter \
                                -Wno-unknown-pragmas \
                                -Wno-unused-value \
                                -Wno-macro-redefined \
                                -Wno-sign-compare \
                                -Wno-incompatible-pointer-types \
                                -Wno-pointer-sign \
                                -Wno-parentheses \
                                -Wno-unused-function \
                                -Wno-format \
                                -Wno-address-of-packed-member \
                                -Wno-missing-declarations

include $(BUILD_SHARED_LIBRARY)
