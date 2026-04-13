use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;
use std::fs;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_checkRustConnection(
    mut env: JNIEnv,
    _class: JClass,
) -> jstring {
    let response = "Status: Rust Engine Connected! 🚀";
    env.new_string(response).expect("Err").into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_saveMarkdownNative(
    mut env: JNIEnv,
    _class: JClass,
    path: JString,
    content: JString,
) -> jstring {
    let file_path: String = env.get_string(&path).expect("Err").into();
    let file_content: String = env.get_string(&content).expect("Err").into();

    match fs::write(&file_path, &file_content) {
        Ok(_) => env.new_string("✅ Tersimpan di internal storage").unwrap().into_raw(),
        Err(e) => env.new_string(format!("❌ Gagal Simpan: {}", e)).unwrap().into_raw(),
    }
}
