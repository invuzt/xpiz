mod ui;
use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};
use jni::JNIEnv;
use crate::ui::pages::AppPath;
use crate::ui::styles;

static mut NOTIF: &str = "XPIZ READY";
static mut CURRENT_ID: i32 = 1;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getSystemConfig(mut env: JNIEnv, _class: JClass, key: jstring) -> jstring {
    let k: String = env.get_string(&unsafe { JString::from_raw(key) }).unwrap().into();
    let res = match k.as_str() {
        "LOGO" => "XPIZ®",
        "NOTIF" => unsafe { NOTIF },
        "NAVBAR" => "TRAINING|PROGRESS|SETTING",
        "COLOR_GELAP" => styles::GELAP,
        _ => "",
    };
    env.new_string(res).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getStyleConfig(env: JNIEnv, _class: JClass, id: jint) -> jstring {
    let is_active = unsafe { id == CURRENT_ID };
    env.new_string(styles::get_nav_style(is_active)).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(env: JNIEnv, _class: JClass, id: jint) -> jstring {
    unsafe { CURRENT_ID = id; }
    let page = AppPath::from_id(id);
    env.new_string(page.get_content()).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_handleTouch(mut env: JNIEnv, _class: JClass, input: jstring) -> jstring {
    let txt: String = env.get_string(&unsafe { JString::from_raw(input) }).unwrap().into();
    unsafe {
        NOTIF = match txt.as_str() {
            "START ENGINE" => "ENGINE ON",
            "UPDATE CORE" => "CORE UPDATED",
            _ => "ACTION REGISTERED",
        };
    }
    env.new_string("OK").unwrap().into_raw()
}
