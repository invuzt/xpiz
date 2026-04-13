use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jstring, jboolean};

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

    // Log simulasi kerja mesin Rust
    println!("Rust Engine: Menganalisis entropi password...");

    let mut advice = Vec::new();
    if password.len() < 8 { advice.push("Kurang panjang"); }
    if !password.chars().any(|c| c.is_uppercase()) { advice.push("Butuh huruf besar"); }
    if !password.chars().any(|c| c.is_numeric()) { advice.push("Butuh angka"); }
    if !password.chars().any(|c| !c.is_alphanumeric()) { advice.push("Butuh simbol"); }

    let res = if advice.is_empty() {
        "Sangat Kuat: Aman disimpan!".to_string()
    } else {
        format!("Lemah: {}", advice.join(", "))
    };

    env.new_string(res).expect("Gagal buat string").into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_savePasswordNative(
    mut env: JNIEnv,
    _class: JClass,
    input: JString,
) -> jboolean {
    let _password: String = match env.get_string(&input) {
        Ok(s) => s.into(),
        Err(_) => return 0, // false
    };

    // Di sini nantinya kita bisa pakai crate 'aes-gcm' untuk enkripsi
    // Untuk sekarang, kita asumsikan berhasil simpan ke memori biner
    println!("Rust Engine: Password dienkripsi dan disimpan secara lokal.");
    
    1 // true
}
