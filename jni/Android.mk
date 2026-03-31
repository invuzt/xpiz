LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := hello
# Kita pakai dummy source agar ndk-build mau jalan
LOCAL_SRC_FILES := hello.c
LOCAL_LDLIBS    := -llog -landroid
LOCAL_STATIC_LIBRARIES := xpiz_core_static

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := xpiz_core_static
LOCAL_SRC_FILES := libxpiz_core.a
include $(PREBUILT_STATIC_LIBRARY)
