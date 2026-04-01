use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};
use jni::JNIEnv;

static mut NOTIF_TEXT: &str = "71 LEVEL";
static mut CURRENT_PAGE: i32 = 1;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getSystemConfig(
    mut env: JNIEnv,
    _class: JClass,
    key: jstring,
) -> jstring {
    let j_key = unsafe { JString::from_raw(key) };
    let k: String = env.get_string(&j_key).expect("Couldn't get java string!").into();
    
    let response = match k.as_str() {
        "LOGO" => "XPIZ®",
        "NOTIF" => unsafe { NOTIF_TEXT },
        "NAVBAR" => "TRAINING|PROGRESS|SETTING",
        // Palet Warna Global
        "COLOR_GELAP" => "#081512",
        "COLOR_AKSEN" => "#D0C9FF",
        "COLOR_PUTIH" => "#FFFFFF",
        "COLOR_ABU"   => "#1A1A1A",
        _ => "",
    };
    
    env.new_string(response).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getStyleConfig(
    mut env: JNIEnv,
    _class: JClass,
    page_id: jint,
) -> jstring {
    // Logika Navbar Aktif: Jika page_id sama dengan CURRENT_PAGE, kasih warna AKSEN
    let is_active = unsafe { page_id == CURRENT_PAGE };
    let color = if is_active { "#D0C9FF" } else { "#1A1A1A" };
    let text_color = if is_active { "#000000" } else { "#888888" };
    
    // Kirim data style gabungan: warna_bg|warna_teks
    let res = format!("{}|{}", color, text_color);
    env.new_string(res).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(env: JNIEnv, _class: JClass, page_id: jint) -> jstring {
    unsafe { CURRENT_PAGE = page_id; } // Update state halaman aktif
    let response = match page_id {
        1 => "START ENGINE\nCHECK STATUS\nUPDATE CORE",
        2 => "CPU: OPTIMAL\nRAM: STABLE\nOS: ANDROID 14",
        3 => "THEME: DARK\nLANGUAGE: RUST\nVERSION: 1.0.0",
        _ => "XPIZ READY",
    };
    env.new_string(response).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_handleTouch(mut env: JNIEnv, _class: JClass, input: jstring) -> jstring {
    let j_input = unsafe { JString::from_raw(input) };
    let clicked: String = env.get_string(&j_input).unwrap().into();
    
    let res = match clicked.as_str() {
        "START ENGINE" => { unsafe { NOTIF_TEXT = "ENGINE ON"; } "OK" },
        "UPDATE CORE" => { unsafe { NOTIF_TEXT = "CORE UPDATED"; } "OK" },
        "CHECK STATUS" => { unsafe { NOTIF_TEXT = "ALL OK"; } "OK" },
        _ => "OK",
    };
    env.new_string(res).unwrap().into_raw()
}
