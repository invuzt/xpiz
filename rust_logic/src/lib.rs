use jni::objects::JClass;
use jni::sys::{jint, jstring};
use jni::JNIEnv;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(
    env: JNIEnv,
    _class: JClass,
    page_id: jint,
) -> jstring {
    
    // Status Logic untuk UI (Tanpa File IO)
    let response = match page_id {
        1 => "TRAINING ACTIVE:\n- System: Online\n- Engine: Stable\n- Logic: Ready",
        2 => "PROGRESS DATA:\n- Status: Monitoring\n- Interface: Connected",
        _ => "XPIZ READY",
    };

    env.new_string(response).unwrap().into_raw()
}
