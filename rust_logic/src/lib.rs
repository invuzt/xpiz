use jni::JNIEnv;
use jni::objects::{JClass};
use jni::sys::jstring;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_checkRustConnection(
    mut env: JNIEnv,
    _class: JClass,
) -> jstring {
    let response = "Status: Rust Engine Connected! 🚀 (xpiz v1.0)";
    env.new_string(response).expect("Gagal membuat string Java").into_raw()
}
