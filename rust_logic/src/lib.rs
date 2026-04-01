use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jstring};
use std::sync::Mutex;
use std::time::{SystemTime, UNIX_EPOCH};

lazy_static::lazy_static! {
    static ref DATA_POINTS: Mutex<Vec<(u64, f32)>> = Mutex::new(Vec::new());
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    mut env: JNIEnv,
    _class: JClass,
    input_java: jstring,
) -> jstring {
    let j_obj = unsafe { jni::objects::JObject::from_raw(input_java) };
    let j_str: &JString = &JString::from(j_obj);
    let input: String = env.get_string(j_str).expect("ERR").into();
    
    let now = SystemTime::now().duration_since(UNIX_EPOCH).unwrap().as_secs();
    let mut data = DATA_POINTS.lock().unwrap();

    // LOGIKA POWERFUL: Mendeteksi Angka & Tren
    if let Ok(val) = input.parse::<f32>() {
        data.push((now, val));
        if data.len() > 10 { data.remove(0); }
        
        // Algoritma Prediksi Sederhana (Trend Analysis)
        if data.len() >= 2 {
            let first = data[0];
            let last = data[data.len()-1];
            let diff_val = last.1 - first.1;
            let diff_time = (last.0 - first.0) as f32;
            let velocity = diff_val / (diff_time.max(1.0)); // Perubahan per detik

            let prediction = last.1 + (velocity * 3600.0); // Prediksi 1 jam ke depan
            let status = if velocity > 0.0 { "MENINGKAT" } else { "MENURUN" };
            
            return return_string(&mut env, &format!("TREND: {} ({:.2}/sec)|PREDIKSI 1 JAM: {:.2}", status, velocity, prediction));
        }
        return return_string(&mut env, "DATA DICATAT|MEMBUTUHKAN LEBIH BANYAK DATA...");
    }

    // Perintah Spesial: "xpiz --brain"
    if input == "xpiz --brain" {
        return return_string(&mut env, "BRAIN_ACTIVE|Sistem siap menganalisis tren stok & performa.");
    }

    return_string(&mut env, "INPUT BUKAN ANGKA|Gunakan angka untuk analisis tren.")
}

fn return_string(env: &mut JNIEnv, s: &str) -> jstring {
    let output = env.new_string(s).expect("Gagal");
    output.into_raw()
}
