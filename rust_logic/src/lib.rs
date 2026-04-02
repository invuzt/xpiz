mod ui;
use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};
use jni::JNIEnv;
use xpiz_brain::XpizBrain as Brain;
use std::fs;

static mut NOTIF: &str = "XPIZ DYNAMIC AI";
static mut LAST_INPUT: String = String::new();
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
        "LOGO" => "XPIZ-OS",
        "NOTIF" => unsafe { NOTIF },
        "NAVBAR" => "AI-HOME|BRAIN",
        "COLOR_GELAP" => "#081512",
        _ => "",
    };
    env.new_string(res).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getStyleConfig(env: JNIEnv, _class: JClass, _id: jint) -> jstring {
    env.new_string("#1A1A1A|#FFFFFF").unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(env: JNIEnv, _class: JClass, id: jint) -> jstring {
    let brain = get_brain();
    if id == 2 {
        let content = format!("TRAINED: {} LABELS|LABEL\nRESET ALL|ACTION", brain.weights.len());
        return env.new_string(content).unwrap().into_raw();
    }

    let input = unsafe { &LAST_INPUT };
    let prediction = brain.predict(input);
    
    if prediction == "unknown" {
        if input.is_empty() {
            env.new_string("XPIZ READY|LABEL\nTYPE TO START|LABEL").unwrap().into_raw()
        } else {
            env.new_string(format!("NEW PATTERN: '{}'|LABEL\nMAP TO ENGINE|ACTION\nMAP TO CAMERA|ACTION", input)).unwrap().into_raw()
        }
    } else {
        // AMBIL TOMBOL DARI MEMORI AI
        let actions = brain.actions.get(&prediction).cloned().unwrap_or_default();
        if actions.is_empty() {
            env.new_string(format!("KATEGORI: {}|LABEL\nADD START|ACTION\nADD STATUS|ACTION", prediction)).unwrap().into_raw()
        } else {
            let mut menu = format!("PREDICTION: {}|LABEL\n", prediction.to_uppercase());
            menu.push_str(&actions.join("\n"));
            env.new_string(menu).unwrap().into_raw()
        }
    }
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_handleTouch(mut env: JNIEnv, _class: JClass, tag: jstring, _val: jstring) -> jstring {
    let t: String = env.get_string(&unsafe { JString::from_raw(tag) }).unwrap().into();
    let brain = get_brain();

    match t.as_str() {
        "SEND_INPUT" => { /* Java handles this by updating LAST_INPUT indirectly */ },
        "MAP TO ENGINE" => {
            let input = unsafe { &LAST_INPUT };
            brain.learn(input, "engine");
            save_brain();
        },
        "MAP TO CAMERA" => {
            let input = unsafe { &LAST_INPUT };
            brain.learn(input, "camera");
            save_brain();
        },
        "ADD START" => {
            brain.add_action("engine", "START ENGINE");
            save_brain();
        },
        "ADD STATUS" => {
            brain.add_action("engine", "CHECK STATUS");
            save_brain();
        },
        "RESET ALL" => {
            *brain = Brain::default();
            save_brain();
        },
        _ => { unsafe { NOTIF = "ACTION: OK"; } }
    };
    env.new_string("REFRESH").unwrap().into_raw()
}
