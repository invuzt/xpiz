use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jstring, jboolean, jint};
use std::sync::Mutex;

// Singleton sederhana untuk menyimpan password di memori Rust
static PASSWORD_VAULT: Mutex<Vec<String>> = Mutex::new(Vec::new());

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getPasswordAdvice(
    mut env: JNIEnv,
    _class: JClass,
    input: JString,
) -> jstring {
    let password: String = match env.get_string(&input) {
        Ok(s) => s.into(),
        Err(_) => return env.new_string("").unwrap().into_raw(),
    };
    if password.is_empty() { return env.new_string("Masukkan password...").unwrap().into_raw(); }

    let mut advice = Vec::new();
    if password.len() < 8 { advice.push("Kurang panjang"); }
    if !password.chars().any(|c| c.is_uppercase()) { advice.push("Butuh huruf besar"); }
    if !password.chars().any(|c| c.is_numeric()) { advice.push("Butuh angka"); }

    let res = if advice.is_empty() { "Sangat Kuat".to_string() } else { format!("Lemah: {}", advice.join(", ")) };
    env.new_string(res).expect("Err").into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_savePasswordNative(
    mut env: JNIEnv,
    _class: JClass,
    input: JString,
) -> jboolean {
    let password: String = match env.get_string(&input) {
        Ok(s) => s.into(),
        Err(_) => return 0,
    };
    let mut vault = PASSWORD_VAULT.lock().unwrap();
    if !vault.contains(&password) {
        vault.push(password);
        return 1;
    }
    0
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getVaultSize(_env: JNIEnv, _class: JClass) -> jint {
    PASSWORD_VAULT.lock().unwrap().len() as jint
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_deletePasswordNative(mut env: JNIEnv, _class: JClass, index: jint) -> jboolean {
    let mut vault = PASSWORD_VAULT.lock().unwrap();
    let idx = index as usize;
    if idx < vault.len() {
        vault.remove(idx);
        return 1;
    }
    0
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getVaultItem(mut env: JNIEnv, _class: JClass, index: jint) -> jstring {
    let vault = PASSWORD_VAULT.lock().unwrap();
    let idx = index as usize;
    let item = if idx < vault.len() { &vault[idx] } else { "" };
    env.new_string(item).unwrap().into_raw()
}
