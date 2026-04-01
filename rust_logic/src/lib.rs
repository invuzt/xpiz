mod ui;
use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};
use jni::JNIEnv;
use crate::ui::pages::AppPath;

static mut NOTIF: &str = "AI AWAKE";
static mut LAST_INPUT: String = String::new();

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getSystemConfig(mut env: JNIEnv, _class: JClass, key: jstring) -> jstring {
    let k: String = env.get_string(&unsafe { JString::from_raw(key) }).unwrap().into();
    let res = match k.as_str() {
        "LOGO" => "XPIZ-AI",
        "NOTIF" => unsafe { NOTIF },
        "NAVBAR" => "AI-HOME|METRICS",
        "COLOR_GELAP" => "#081512",
        _ => "",
    };
    env.new_string(res).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(env: JNIEnv, _class: JClass, _id: jint) -> jstring {
    // Isi konten sekarang diambil dari hasil analisa AI terhadap input terakhir
    let ai_content = unsafe { AppPath::get_ai_menu(&LAST_INPUT) };
    env.new_string(ai_content).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_handleTouch(mut env: JNIEnv, _class: JClass, tag: jstring, val: jstring) -> jstring {
    let t: String = env.get_string(&unsafe { JString::from_raw(tag) }).unwrap().into();
    let v: String = env.get_string(&unsafe { JString::from_raw(val) }).unwrap().into();
    
    match t.as_str() {
        "SEND_INPUT" => {
            unsafe { 
                LAST_INPUT = v.clone();
                NOTIF = "AI PROCESSING..."; 
            }
            "REFRESH".to_string()
        },
        _ => "NONE".to_string(),
    };
    
    env.new_string("REFRESH").unwrap().into_raw()
}
