mod ui;
use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};
use jni::JNIEnv;
// Hapus import AppPath jika tidak dipakai di bawah, 
// atau biarkan jika digunakan untuk navigasi
use xpiz_brain::XpizBrain as Brain;
use std::fs;

static mut NOTIF: &str = "XPIZ AI ONLINE";
static mut LAST_INPUT: String = String::new();
static mut CURRENT_NAV_ID: i32 = 1;
static mut AI_BRAIN: Option<Brain> = None;
const BRAIN_PATH: &str = "/data/user/0/com.invuzt.xpiz/files/brain.json";

fn get_brain() -> &'static mut Brain {
    unsafe {
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
    if let Some(brain) = unsafe { AI_BRAIN.as_ref() } {
        if let Ok(data) = serde_json::to_string(brain) {
            let _ = fs::write(BRAIN_PATH, data);
        }
    }
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getSystemConfig(mut env: JNIEnv, _class: JClass, key: jstring) -> jstring {
    let k: String = env.get_string(&unsafe { JString::from_raw(key) }).unwrap().into();
    let res = match k.as_str() {
        "LOGO" => "XPIZ-AI",
        "NOTIF" => unsafe { NOTIF },
        "NAVBAR" => "AI-HOME|ANALYTICS",
        "COLOR_GELAP" => "#081512",
        _ => "",
    };
    env.new_string(res).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getStyleConfig(env: JNIEnv, _class: JClass, id: jint) -> jstring {
    let is_active = unsafe { id == CURRENT_NAV_ID };
    let stl = if is_active { "#D0C9FF|#000000" } else { "#1A1A1A|#FFFFFF" };
    env.new_string(stl).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(env: JNIEnv, _class: JClass, id: jint) -> jstring {
    unsafe { CURRENT_NAV_ID = id; }
    let brain = get_brain();
    
    if id == 2 {
        let content = format!("KNOWLEDGE: {} LABELS|LABEL\nSAVE MEMORY|ACTION\nRESET BRAIN|ACTION", brain.weights.len());
        env.new_string(content).unwrap().into_raw()
    } else {
        let input = unsafe { &LAST_INPUT };
        let prediction = brain.predict(input);
        
        let menu = match prediction.as_str() {
            "engine" => "ENGINE: ACTIVE|LABEL\nDIAGNOSTIC|ACTION\nSTOP ENGINE|ACTION",
            "camera" => "OPEN ZAMERA|ACTION\nAI FILTERS|ACTION\nSTORAGE: OK|LABEL",
            _ => "AI READY|LABEL\nTYPE TO TRAIN|LABEL\nSCAN SYSTEM|ACTION",
        };
        env.new_string(menu).unwrap().into_raw()
    }
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_handleTouch(mut env: JNIEnv, _class: JClass, tag: jstring, val: jstring) -> jstring {
    let t: String = env.get_string(&unsafe { JString::from_raw(tag) }).unwrap().into();
    let v: String = env.get_string(&unsafe { JString::from_raw(val) }).unwrap().into();
    let brain = get_brain();

    match t.as_str() {
        "SEND_INPUT" => {
            unsafe { LAST_INPUT = v.clone(); }
            if v.contains("mesin") { brain.learn(&v, "engine"); }
            if v.contains("foto") || v.contains("kamera") { brain.learn(&v, "camera"); }
            save_brain();
            unsafe { NOTIF = "LEARNED"; }
        },
        "SAVE MEMORY" => { save_brain(); unsafe { NOTIF = "SAVED"; } },
        "RESET BRAIN" => { 
            brain.weights.clear(); 
            save_brain(); 
            unsafe { NOTIF = "WIPED"; } 
        },
        _ => { unsafe { NOTIF = "ACTION OK"; } }
    };
    
    env.new_string("REFRESH").unwrap().into_raw()
}
