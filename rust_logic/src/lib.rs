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
    let input = input.trim().to_lowercase();

    // 1. Logika Prediksi Uang Pecahan (Request dari UI)
    if input.starts_with("predict|") {
        let total: f32 = input[8..].parse().unwrap_or(0.0);
        let p1 = (total / 5000.0).ceil() * 5000.0;
        let p2 = (total / 50000.0).ceil() * 50000.0;
        let p3 = 100000.0;
        return return_string(&mut env, &format!("SUGGEST|{}|{}|{}", p1, p2, p3));
    }

    // 2. Jika Input Angka Murni (Bayar Custom)
    if let Ok(bayar) = input.parse::<f32>() {
        return return_string(&mut env, &format!("PAY_CUSTOM|{}", bayar));
    }

    // 3. Jika Input Nama : Harga (Tambah Menu)
    if input.contains(':') {
        let parts: Vec<&str> = input.split(':').collect();
        return return_string(&mut env, &format!("ADD|{}|{}", parts[0].trim().to_uppercase(), parts[1].trim()));
    }

    return_string(&mut env, "IDLE")
}

fn return_string(env: &mut JNIEnv, s: &str) -> jstring {
    env.new_string(s).expect("Gagal").into_raw()
}
