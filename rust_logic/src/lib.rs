use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};
use jni::JNIEnv;

// State statis di Rust
static mut NOTIF_TEXT: &str = "71 LEVEL";

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getSystemConfig(
    mut env: JNIEnv,
    _class: JClass,
    key: jstring, // Ini adalah raw pointer
) -> jstring {
    // FIX: Bungkus raw pointer menjadi JString object
    let j_key = unsafe { JString::from_raw(key) };
    let k: String = env.get_string(&j_key).expect("Couldn't get java string!").into();
    
    let response = match k.as_str() {
        "LOGO" => "XPIZ®",
        "NOTIF" => unsafe { NOTIF_TEXT },
        "NAVBAR" => "TRAINING|PROGRESS|SETTING",
        _ => "",
    };
    
    env.new_string(response).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(
    env: JNIEnv, 
    _class: JClass, 
    page_id: jint
) -> jstring {
    let response = match page_id {
        1 => "START ENGINE\nCHECK STATUS\nUPDATE CORE",
        2 => "CPU: OPTIMAL\nRAM: STABLE\nOS: ANDROID 14",
        3 => "THEME: DARK\nLANGUAGE: RUST\nVERSION: 1.0.0",
        _ => "XPIZ READY",
    };
    env.new_string(response).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_handleTouch(
    mut env: JNIEnv,
    _class: JClass,
    input: jstring, // Gunakan jstring raw pointer untuk konsistensi
) -> jstring {
    let j_input = unsafe { JString::from_raw(input) };
    let clicked: String = env.get_string(&j_input).expect("Couldn't get java string!").into();
    
    let res = match clicked.as_str() {
        "START ENGINE" => {
            unsafe { NOTIF_TEXT = "ENGINE ON"; }
            "Rust: Engine Started!"
        },
        "UPDATE CORE" => {
            unsafe { NOTIF_TEXT = "CORE UPDATED"; }
            "Rust: Core Logic Synced"
        },
        "CHECK STATUS" => {
            unsafe { NOTIF_TEXT = "ALL OK"; }
            "Rust: System Healthy"
        },
        _ => "Rust: Action Received",
    };
    
    env.new_string(res).unwrap().into_raw()
}
