use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};
use jni::JNIEnv;

// State statis di Rust (Simulasi database/state sederhana)
static mut NOTIF_TEXT: &str = "71 LEVEL";

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getSystemConfig(
    env: JNIEnv,
    _class: JClass,
    key: jstring,
) -> jstring {
    let k: String = env.get_string(&key.into()).unwrap().into();
    
    let response = match k.as_str() {
        "LOGO" => "XPIZ®",
        "NOTIF" => unsafe { NOTIF_TEXT },
        "NAVBAR" => "TRAINING|PROGRESS|SETTING", // Navbar dikirim sebagai string terpisah pipe
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
    input: JString,
) -> jstring {
    let clicked: String = env.get_string(&input).unwrap().into();
    
    // Logika Reaksi: Mengubah Notifikasi di Header berdasarkan klik
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
