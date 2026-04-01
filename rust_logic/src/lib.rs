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

    // 1. COMMAND: TAMBAH TOMBOL (Contoh: tambah dimsum : 15000)
    if input.starts_with("tambah ") {
        let clean = input[7..].trim();
        if let Some((nama, harga)) = clean.split_once(':') {
            return return_string(&mut env, &format!("CMD_ADD|{}|{}", nama.trim().to_uppercase(), harga.trim()));
        }
    }

    // 2. COMMAND: HAPUS TOMBOL (Contoh: hapus dimsum)
    if input.starts_with("hapus ") {
        let nama = input[6..].trim().to_uppercase();
        return return_string(&mut env, &format!("CMD_DEL|{}", nama));
    }

    // 3. COMMAND: PRINT (Contoh: print atau struk)
    if input == "print" || input == "struk" {
        return return_string(&mut env, "CMD_PRINT");
    }

    // 4. AI ANALYTICS (Jika input angka saja untuk bayar)
    if let Ok(val) = input.parse::<f32>() {
        return return_string(&mut env, &format!("CMD_CASH|{}", val));
    }

    return_string(&mut env, "AI: Gunakan perintah 'tambah', 'hapus', atau 'print'.")
}

fn return_string(env: &mut JNIEnv, s: &str) -> jstring {
    env.new_string(s).expect("Gagal").into_raw()
}
