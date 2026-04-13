use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;
use std::fs;

// ... (fungsi check dan save yang lama tetap ada) ...

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_readMarkdownNative(
    mut env: JNIEnv,
    _class: JClass,
    path: JString,
) -> jstring {
    let file_path: String = env.get_string(&path).expect("Err").into();

    match fs::read_to_string(file_path) {
        Ok(content) => env.new_string(content).unwrap().into_raw(),
        Err(e) => env.new_string(format!("Error baca file: {}", e)).unwrap().into_raw(),
    }
}

// ... (fungsi listVaultFiles tetap ada) ...
