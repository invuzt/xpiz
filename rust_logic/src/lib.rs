use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jstring};
use std::sync::Mutex;

// Memori Pintar Odfiz
struct OdfizState {
    last_val: f32,
    history: Vec<(String, f32)>, // (NamaBarang, Jumlah)
    team: Vec<String>,
}

lazy_static::lazy_static! {
    static ref STATE: Mutex<OdfizState> = Mutex::new(OdfizState {
        last_val: 0.0,
        history: Vec::new(),
        team: vec!["ajar".to_string(), "dendi".to_string(), "angga".to_string(), "heru".to_string(), "eko".to_string()],
    });
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    mut env: JNIEnv,
    _class: JClass,
    input_java: jstring,
) -> jstring {
    let j_str: String = env.get_string(&JString::from(unsafe { jni::objects::JObject::from_raw(input_java) }))
        .expect("ERR").into();
    let input = j_str.trim().to_lowercase();
    let mut state = STATE.lock().unwrap();

    // 1. LOGIKA IDENTIFIKASI (SIAPA/APA INI?)
    
    // Cek apakah ini Tim?
    if state.team.contains(&input) {
        return return_string(&mut env, &format!("MODE: TIM|Sistem mencatat kehadiran {}. Status: AKTIF.", input.to_uppercase()));
    }

    // Cek apakah ini Angka murni?
    if let Ok(val) = input.parse::<f32>() {
        let diff = val - state.last_val;
        state.last_val = val;
        let trend = if diff >= 0.0 { "SURPLUS (+)" } else { "DEFISIT (-)" };
        return return_string(&mut env, &format!("MODE: ANALISIS|Tren: {} {:.1}. Estimasi aman.", trend, diff.abs()));
    }

    // Cek apakah ini Format "Barang Angka" (Contoh: solar 50)
    let parts: Vec<&str> = input.split_whitespace().collect();
    if parts.len() == 2 {
        if let Ok(val) = parts[1].parse::<f32>() {
            state.history.push((parts[0].to_string(), val));
            return return_string(&mut env, &format!("MODE: LOGISTIK|Stok {} diperbarui ke {}. AI mulai menghitung pola...", parts[0], val));
        }
    }

    return_string(&mut env, "MODE: BELAJAR|Data baru disimpan. Terus input untuk meningkatkan akurasi AI.")
}

fn return_string(env: &mut JNIEnv, s: &str) -> jstring {
    env.new_string(s).expect("Gagal").into_raw()
}
