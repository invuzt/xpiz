use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::jstring;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_stringFromRust(
    env: JNIEnv,
    _class: JClass,
) -> jstring {
    let output = env.new_string("xpiz Engine: Aktif! 🦀")
        .expect("Gagal buat string!");
    output.into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_openCameraRust(
    env: JNIEnv,
    _class: JClass,
) -> jstring {
    // Di sini nanti titik masuk ACameraManager NDK
    let status = "Menghubungi Sensor Kamera via NDK... OK! ✅";
    let output = env.new_string(status).expect("Gagal buat string!");
    output.into_raw()
}
