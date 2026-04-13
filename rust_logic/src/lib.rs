use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;

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
    
    // Rincian Detail Alasan:
    if password.len() < 8 {
        advice.push("Kurang panjang (min. 8)");
    }
    if !password.chars().any(|c| c.is_uppercase()) {
        advice.push("Butuh huruf besar");
    }
    if !password.chars().any(|c| c.is_numeric()) {
        advice.push("Butuh angka");
    }
    if !password.chars().any(|c| !c.is_alphanumeric()) {
        advice.push("Butuh simbol (@,#,$,dll)");
    }

    let res = if advice.is_empty() {
        if password.len() >= 12 {
            "Sangat Kuat: Mantap, sulit ditembus!".to_string()
        } else {
            "Cukup: Aman, tapi lebih panjang lebih baik.".to_string()
        }
    } else {
        format!("Lemah: {}", advice.join(", "))
    };

    // Kita ubah String Rust menjadi jstring JNI
    env.new_string(res).expect("Gagal buat string").into_raw()
}
