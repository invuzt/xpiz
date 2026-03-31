LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := hello
LOCAL_SRC_FILES := hello.c
# Urutan sangat menentukan di NDK
LOCAL_LDLIBS    := -llog -landroid -lcamera2ndk -lmediandk

# Ambil semua simbol dari Rust
LOCAL_WHOLE_STATIC_LIBRARIES := xpiz_core_static

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := xpiz_core_static
LOCAL_SRC_FILES := libxpiz_core.a
include $(PREBUILT_STATIC_LIBRARY)
