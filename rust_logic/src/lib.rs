use jni::objects::JClass;
use jni::sys::{jint, jstring};
use jni::JNIEnv;

// Fungsi simulasi AI (Nanti bisa baca file .tflite di assets)
fn hitung_ai_personal(id: i32) -> String {
    let base_xp = 4500;
    
    match id {
        1 => {
            // Logika AI Training: Memberikan target dinamis
            let target = 240 - 15;
            format!("XPIZ AI PREDICTION:\n- Focus: Speed Reaction\n- Target: {}ms\n- Status: High Potential", target)
        },
        2 => {
            // Logika AI Progress: Menghitung sisa level
            let percent = (base_xp as f32 / 5000.0) * 100.0;
            format!("AI PERFORMANCE REPORT:\n- Rank: ELITE\n- Efficiency: {:.1}%\n- Forecast: Level Up Soon", percent)
        },
        _ => "XPIZ System Stable".to_string(),
    }
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(
    mut env: JNIEnv,
    _class: JClass,
    page_id: jint,
) -> jstring {
    // Memanggil fungsi AI di atas
    let content = hitung_ai_personal(page_id as i32);

    let output = env.new_string(content).expect("Gagal buat string");
    output.into_raw()
}
