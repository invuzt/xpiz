use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};
use jni::JNIEnv;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(env: JNIEnv, _class: JClass, page_id: jint) -> jstring {
    let response = if page_id == 1 { "START ENGINE\nCHECK STATUS" } else { "REFRESH DATA\nCLEAR LOGS" };
    env.new_string(response).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_handleTouch(mut env: JNIEnv, _class: JClass, input: JString) -> jstring {
    let clicked: String = env.get_string(&input).unwrap().into();
    let res = match clicked.as_str() {
        "START ENGINE" => "RUST: Mesin dinyalakan!",
        "CLEAR LOGS" => "RUST: Log dibersihkan",
        _ => "RUST: Aksi diterima",
    };
    env.new_string(res).unwrap().into_raw()
}
