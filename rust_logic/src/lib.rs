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

    // LOGIKA KASIR MODULAR: "add btn Nama : Harga"
    if input.to_lowercase().starts_with("add btn") {
        let clean_input = input[7..].trim(); // Hapus "add btn"
        if let Some((nama, harga)) = clean_input.split_once(':') {
            return return_string(&mut env, &format!("CREATE_BTN|{}|{}", nama.trim(), harga.trim()));
        }
    }

    // LOGIKA TRANSAKSI: Jika cuma angka (bayar)
    if let Ok(bayar) = input.parse::<f32>() {
        return return_string(&mut env, &format!("PAYMENT|{}", bayar));
    }

    return_string(&mut env, "MODE: TERMINAL|Gunakan 'add btn Nama : Harga' untuk buat tombol.")
}

fn return_string(env: &mut JNIEnv, s: &str) -> jstring {
    env.new_string(s).expect("Gagal").into_raw()
}
