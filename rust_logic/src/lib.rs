use jni::objects::{JClass, JByteArray};
use jni::sys::{jint, jstring};
use jni::JNIEnv;

#[no_mangle]
pub extern "system" fn Java_com_invuzt_xpiz_MainActivity_analyzeFrame(
    mut env: JNIEnv,
    _class: JClass,
    image_data: JByteArray,
    width: jint,
    height: jint,
) -> jstring {
    // Ambil data dari Java
    let input = env.convert_byte_array(&image_data).unwrap_or_default();
    let len = input.len();

    // Logika Sat-set: Hitung rata-rata kecerahan (Grayscale sederhana)
    // Ini cuma contoh, tapi ini jalan di CPU Native (Cepet banget!)
    let avg_brightness = if len > 0 {
        let sum: u64 = input.iter().take(1000).map(|&b| b as u64).sum();
        sum / 1000
    } else {
        0
    };

    let response = format!(
        "Rust Engine: {}x{} px processed. Data size: {} bytes. Avg Bright: {}",
        width, height, len, avg_brightness
    );

    env.new_string(response).unwrap().into_raw()
}
