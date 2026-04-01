use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jstring};

#[no_mangle]
pub unsafe extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    mut env: JNIEnv,
    _class: JClass,
    input_java: jstring,
) -> jstring {
    let input: String = env.get_string(&JString::from(unsafe { jni::objects::JObject::from_raw(input_java) }))
        .expect("ERR").into();
    let input = input.trim();

    if input.is_empty() { return return_string(&mut env, ""); }

    // AI AUTO-DETECTION
    // 1. Jika input mengandung titik dua (Contoh: Dimsum : 15000)
    if input.contains(':') {
        let parts: Vec<&str> = input.split(':').collect();
        return return_string(&mut env, &format!("NEW_BTN|{}|{}", parts[0].trim(), parts[1].trim()));
    }

    // 2. Jika input adalah angka murni (Contoh: 50000) -> Pembayaran
    if let Ok(val) = input.parse::<f32>() {
        return return_string(&mut env, &format!("CASH|{}", val));
    }

    // 3. Jika teks biasa (Contoh: Bakpao) -> Buat tombol tanpa harga (default 0)
    return return_string(&mut env, &format!("NEW_BTN|{}|0", input.to_uppercase()));
}

fn return_string(env: &mut JNIEnv, s: &str) -> jstring {
    env.new_string(s).expect("Gagal").into_raw()
}
