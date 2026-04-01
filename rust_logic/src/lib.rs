use jni::objects::JClass;
use jni::sys::{jint, jstring};
use jni::JNIEnv;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(
    env: JNIEnv,
    _class: JClass,
    page_id: jint,
) -> jstring {
    let mut response = String::new();

    if page_id == 1 {
        response.push_str("TRAINING ACTIVE\n");
        response.push_str("Engine: v1.0-Rust\n");
        response.push_str("Status: Running");
    } else if page_id == 2 {
        response.push_str("MONITORING SYSTEM\n");
        response.push_str("Uptime: 120ms\n");
        response.push_str("Threads: 4 Active\n");
        response.push_str("Security: Verified"); // Baris tambahan!
    } else {
        response.push_str("XPIZ READY");
    }

    env.new_string(response).unwrap().into_raw()
}
