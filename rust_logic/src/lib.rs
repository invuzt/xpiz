mod ui;
use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};
use jni::JNIEnv;
use crate::ui::pages::AppPath;
use crate::ui::styles;

static mut NOTIF: &str = "XPIZ READY";
static mut LAST_INPUT: String = String::new();
static mut CURRENT_NAV_ID: i32 = 1;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getSystemConfig(mut env: JNIEnv, _class: JClass, key: jstring) -> jstring {
    let k: String = env.get_string(&unsafe { JString::from_raw(key) }).unwrap().into();
    let res = match k.as_str() {
        "LOGO" => "XPIZ-AI",
        "NOTIF" => unsafe { NOTIF },
        "NAVBAR" => "AI-HOME|ANALYTICS", // Cuma 2 menu
        "COLOR_GELAP" => styles::GELAP,
        _ => "",
    };
    env.new_string(res).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getStyleConfig(env: JNIEnv, _class: JClass, id: jint) -> jstring {
    let is_active = unsafe { id == CURRENT_NAV_ID };
    env.new_string(styles::get_nav_style(is_active)).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(env: JNIEnv, _class: JClass, id: jint) -> jstring {
    unsafe { CURRENT_NAV_ID = id; }
    let input = unsafe { &LAST_INPUT };
    
    // Nav 1: AI Home (Berdasarkan input) | Nav 2: Analytics (Statistik)
    let content = if id == 2 {
        AppPath::get_ai_menu("metrik")
    } else {
        AppPath::get_ai_menu(input)
    };
    
    env.new_string(content).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_handleTouch(mut env: JNIEnv, _class: JClass, tag: jstring, val: jstring) -> jstring {
    let t: String = env.get_string(&unsafe { JString::from_raw(tag) }).unwrap().into();
    let v: String = env.get_string(&unsafe { JString::from_raw(val) }).unwrap().into();
    
    match t.as_str() {
        "SEND_INPUT" => {
            unsafe { 
                LAST_INPUT = v.clone();
                NOTIF = "AI LEARNED"; // Simulasi belajar
            }
        },
        "NAV_CLICK" => {
            unsafe { CURRENT_NAV_ID = v.parse().unwrap_or(1); }
        },
        _ => { unsafe { NOTIF = "EXECUTED"; } }
    };
    
    env.new_string("REFRESH").unwrap().into_raw()
}
