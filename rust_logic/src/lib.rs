use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jint;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_checkPasswordStrength(
    mut env: JNIEnv,
    _class: JClass,
    input: JString,
) -> jint {
    let password: String = match env.get_string(&input) {
        Ok(s) => s.into(),
        Err(_) => return 0,
    };

    if password.is_empty() { return 0; }

    let mut score = 0;
    
    // Kriteria 1: Panjang
    if password.len() >= 8 { score += 1; }
    if password.len() >= 12 { score += 1; }
    
    // Kriteria 2: Angka
    if password.chars().any(|c| c.is_numeric()) { score += 1; }
    
    // Kriteria 3: Huruf Besar & Kecil
    let has_upper = password.chars().any(|c| c.is_uppercase());
    let has_lower = password.chars().any(|c| c.is_lowercase());
    if has_upper && has_lower { score += 1; }
    
    // Kriteria 4: Simbol/Karakter Khusus
    if password.chars().any(|c| !c.is_alphanumeric()) { score += 1; }

    score as jint
}
