mod ui;
use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};
use jni::JNIEnv;
use crate::ui::pages::AppPath;
use crate::ui::styles;

static mut NOTIF: &str = "71 LEVEL";

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getSystemConfig(mut env: JNIEnv, _class: JClass, key: jstring) -> jstring {
    let k: String = env.get_string(&unsafe { JString::from_raw(key) }).unwrap().into();
    let res = match k.as_str() {
        "LOGO" => "XPIZ",
        "NOTIF" => unsafe { NOTIF },
        "NAVBAR" => "TRAINING|PROGRESS",
        "COLOR_GELAP" => styles::GELAP,
        _ => "",
    };
    env.new_string(res).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(env: JNIEnv, _class: JClass, id: jint) -> jstring {
    let page = AppPath::from_id(id);
    env.new_string(page.get_content()).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_handleTouch(mut env: JNIEnv, _class: JClass, label: jstring, input_val: jstring) -> jstring {
    let tag: String = env.get_string(&unsafe { JString::from_raw(label) }).unwrap().into();
    let val: String = env.get_string(&unsafe { JString::from_raw(input_val) }).unwrap().into();
    
    // Logika: Jika ada input, simpan ke Notif Header untuk pembuktian
    let response = match tag.as_str() {
        "HEADER_CLICK" => "GOTO:99",
        "BACK TO MENU" => "GOTO:1",
        "START ENGINE" => {
            if !val.is_empty() {
                unsafe { NOTIF = Box::leak(format!("VAL: {}", val).into_boxed_str()); }
            }
            "REFRESH"
        },
        "NOTIF_CLICK" => { unsafe { NOTIF = "SYNCED"; } "REFRESH" },
        _ => "NONE",
    };
    
    env.new_string(response).unwrap().into_raw()
}
