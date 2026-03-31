LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

# Nama modul yang akan dipanggil di Java: System.loadLibrary("hello")
LOCAL_MODULE    := hello
LOCAL_SRC_FILES := hello.c
LOCAL_LDLIBS    := -llog -landroid
# Pakai library static xpiz yang baru
LOCAL_STATIC_LIBRARIES := xpiz_core_static

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
# Definisikan sumber file static dari Rust
LOCAL_MODULE := xpiz_core_static
LOCAL_SRC_FILES := libxpiz_core.a
include $(PREBUILT_STATIC_LIBRARY)
