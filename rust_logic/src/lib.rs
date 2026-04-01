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
        "NAVBAR" => "TRAINING|PROGRESS", // Tanpa Settings di sini
        "COLOR_GELAP" => styles::GELAP,
        _ => "",
    };
    env.new_string(res).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getStyleConfig(env: JNIEnv, _class: JClass, id: jint) -> jstring {
    // Kita anggap ID 1 & 2 adalah Navbar
    let res = if id <= 2 { styles::get_nav_style(true) } else { styles::get_nav_style(false) };
    env.new_string(res).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(env: JNIEnv, _class: JClass, id: jint) -> jstring {
    let page = AppPath::from_id(id);
    env.new_string(page.get_content()).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_handleTouch(mut env: JNIEnv, _class: JClass, input: jstring) -> jstring {
    let txt: String = env.get_string(&unsafe { JString::from_raw(input) }).unwrap().into();
    
    let response = match txt.as_str() {
        "HEADER_CLICK" => "GOTO:99", // Masuk ke Settings
        "BACK TO MENU" => "GOTO:1",
        "NOTIF_CLICK" => { unsafe { NOTIF = "SYNCED"; } "REFRESH" },
        _ => "NONE",
    };
    
    env.new_string(response).unwrap().into_raw()
}
