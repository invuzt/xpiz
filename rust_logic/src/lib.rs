use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jstring};
use std::time::{SystemTime, UNIX_EPOCH};

#[no_mangle]
pub unsafe extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    mut env: JNIEnv,
    _class: JClass,
    input_java: jstring,
) -> jstring {
    let input: String = env.get_string(&JString::from(unsafe { jni::objects::JObject::from_raw(input_java) }))
        .expect("ERR").into();
    let input = input.trim().to_lowercase();

    // Ambil Jam Lokal (Sederhana)
    let total_secs = SystemTime::now().duration_since(UNIX_EPOCH).unwrap().as_secs();
    let hour = ((total_secs / 3600) % 24) + 7; // Estimasi GMT+7 Ponorogo

    // 1. DETEKSI SHIFT (PERSONALIA)
    if input.chars().all(|c| c.is_alphabetic()) && input.len() > 1 {
        let shift = if hour < 12 { "PAGI" } else { "SORE" };
        return return_string(&mut env, &format!("AI_MODE: HRD|Presensi {} tercatat pada Shift {}. Semangat kerja!", input.to_uppercase(), shift));
    }

    // 2. DETEKSI STOK (INVENTORY)
    let parts: Vec<&str> = input.split_whitespace().collect();
    if parts.len() == 2 && parts[1].parse::<f32>().is_ok() {
        return return_string(&mut env, &format!("AI_MODE: INVENTORY|Update stok {}: {}. Data waktu tersimpan.", parts[0], parts[1]));
    }

    return_string(&mut env, &format!("AI_MODE: ANALYTICS|Memproses data: {}. Jam operasional: {}:00", input, hour))
}

fn return_string(env: &mut JNIEnv, s: &str) -> jstring {
    env.new_string(s).expect("Gagal").into_raw()
}
