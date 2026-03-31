use jni::JNIEnv;
use jni::objects::{JClass, JObject};
use ndk::native_window::NativeWindow;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_startCameraPreview(
    env: JNIEnv,
    _class: JClass,
    surface: JObject,
) {
    // 1. Ubah Surface Java menjadi NativeWindow Rust
    let window = unsafe { 
        ndk_sys::ANativeWindow_fromSurface(env.get_native_interface(), *surface as _) 
    };

    if !window.is_null() {
        // DI SINI: Rust sekarang memegang kendali layar!
        // Logika ACameraDevice_createCaptureSession akan masuk di sini
        println!("Rust: Berhasil mengunci Surface untuk Preview!");
    }
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_stringFromRust(
    env: JNIEnv,
    _class: JClass,
) -> jni::sys::jstring {
    let output = env.new_string("xpiz Engine Ready!").unwrap();
    output.into_raw()
}
