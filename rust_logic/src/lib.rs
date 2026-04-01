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

    // LOGIKA PERAMAL STOK (FORECASTING)
    let parts: Vec<&str> = input.split_whitespace().collect();
    if parts.len() == 2 {
        if let Ok(stok) = parts[1].parse::<f32>() {
            let item = parts[0];
            // Simulasi AI: Asumsi rata-rata pemakaian 5 unit/hari
            let sisa_hari = stok / 5.0; 
            
            return return_string(&mut env, &format!("AI_MODE: FORECAST|Stok {} sisa {}. Estimasi: HABIS DALAM {:.1} HARI.", item.to_uppercase(), stok, sisa_hari));
        }
    }

    // LOGIKA ABSENSI (HANYA JIKA NAMA TUNGGAL)
    if input.chars().all(|c| c.is_alphabetic()) {
        return return_string(&mut env, &format!("AI_MODE: HRD|Presensi {} Berhasil. AI sedang memantau produktivitas harian.", input.to_uppercase()));
    }

    return_string(&mut env, "AI_MODE: LEARNING|Gunakan format 'barang angka' untuk ramalan stok.")
}

fn return_string(env: &mut JNIEnv, s: &str) -> jstring {
    env.new_string(s).expect("Gagal").into_raw()
}
