mod ui;
use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};
use jni::JNIEnv;
use crate::ui::pages::{AppPath, Brain};
use std::fs;

static mut LAST_INPUT: String = String::new();
static mut NOTIF: &str = "XPIZ AI ONLINE";
const BRAIN_PATH: &str = "/data/user/0/com.invuzt_xpiz/files/brain.json";

fn get_brain() -> &'static mut Brain {
    unsafe {
        static mut AI_BRAIN: Option<Brain> = None;
        if AI_BRAIN.is_none() {
            let loaded = fs::read_to_string(BRAIN_PATH)
                .ok()
                .and_then(|data| serde_json::from_str(&data).ok())
                .unwrap_or_else(|| Brain::default());
            AI_BRAIN = Some(loaded);
        }
        AI_BRAIN.as_mut().unwrap()
    }
}

fn save_brain() {
    let brain = get_brain();
    if let Ok(data) = serde_json::to_string(brain) {
        let _ = fs::write(BRAIN_PATH, data);
    }
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getSystemConfig(mut env: JNIEnv, _class: JClass, key: jstring) -> jstring {
    let k: String = env.get_string(&unsafe { JString::from_raw(key) }).unwrap().into();
    let res = match k.as_str() {
        "LOGO" => "XPIZ-REAL-AI",
        "NOTIF" => unsafe { NOTIF },
        "NAVBAR" => "AI-HOME|METRICS",
        "COLOR_GELAP" => "#081512",
        _ => "",
    };
    env.new_string(res).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(env: JNIEnv, _class: JClass, id: jint) -> jstring {
    let brain = get_brain();
    let content = if id == 2 {
        format!("TRAINED: {} TIMES|LABEL\nCLEAR BRAIN|ACTION", brain.total_trains)
    } else {
        brain.predict_menu(unsafe { &LAST_INPUT })
    };
    env.new_string(content).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_handleTouch(mut env: JNIEnv, _class: JClass, tag: jstring, val: jstring) -> jstring {
    let t: String = env.get_string(&unsafe { JString::from_raw(tag) }).unwrap().into();
    let v: String = env.get_string(&unsafe { JString::from_raw(val) }).unwrap().into();
    let brain = get_brain();

    match t.as_str() {
        "SEND_INPUT" => {
            unsafe { LAST_INPUT = v.clone(); }
            // Coba tebak kategori secara otomatis berdasarkan keyword (Supervised Training awal)
            if v.contains("mesin") || v.contains("cek") { brain.learn(&v, "engine"); }
            if v.contains("foto") || v.contains("kamera") { brain.learn(&v, "camera"); }
            if v.contains("ram") || v.contains("status") { brain.learn(&v, "system"); }
            
            save_brain();
            unsafe { NOTIF = "AI THINKING..."; }
        },
        "CLEAR BRAIN" => {
            *brain = Brain::default();
            save_brain();
            unsafe { NOTIF = "MEMORY ERASED"; }
        },
        _ => { unsafe { NOTIF = "ACTION OK"; } }
    };
    env.new_string("REFRESH").unwrap().into_raw()
}
