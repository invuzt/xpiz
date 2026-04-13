use jni::JNIEnv;
use jni::objects::{JClass, JString};

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

    if password.is_empty() {
        return env.new_string("Masukkan password...").unwrap().into_raw();
    }

    let mut advice = Vec::new();
    
    if password.len() < 8 {
        advice.push("Terlalu pendek (min. 8 karakter)");
    }
    if !password.chars().any(|c| c.is_uppercase()) {
        advice.push("Tambahkan huruf besar");
    }
    if !password.chars().any(|c| c.is_numeric()) {
        advice.push("Tambahkan angka");
    }
    if !password.chars().any(|c| !c.is_alphanumeric()) {
        advice.push("Tambahkan simbol (@,#,$,dll)");
    }

    let res = if advice.is_empty() {
        if password.len() >= 12 {
            "Sangat Kuat: Kombinasi sempurna!".to_string()
        } else {
            "Cukup Kuat: Pertimbangkan buat lebih panjang.".to_string()
        }
    } else {
        format!("Lemah: {}", advice.join(", "))
    };

    env.new_string(res).expect("Gagal buat string").into_raw()
}
