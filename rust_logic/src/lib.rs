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

    // 1. Jika Input mengandung ":" berarti Tambah Produk Baru
    if input.contains(':') {
        let parts: Vec<&str> = input.split(':').collect();
        return return_string(&mut env, &format!("ADD|{}|{}", parts[0].trim().to_uppercase(), parts[1].trim()));
    }

    // 2. Jika Input Angka Murni berarti Nominal Uang Bayar
    if let Ok(nominal) = input.parse::<f32>() {
        return return_string(&mut env, &format!("CALC_CHANGE|{}", nominal));
    }

    // 3. Jika Input "hapus [nama]"
    if input.starts_with("hapus ") {
        return return_string(&mut env, &format!("DEL|{}", input[6..].trim().to_uppercase()));
    }

    return_string(&mut env, "AI: Ketik 'Nama : Harga' untuk produk baru.")
}

fn return_string(env: &mut JNIEnv, s: &str) -> jstring {
    env.new_string(s).expect("Gagal").into_raw()
}
