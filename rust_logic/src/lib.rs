use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};
use std::collections::HashMap;

#[no_mangle]
pub extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    mut env: JNIEnv,
    _class: JClass,
    click_history: jint, // Simulasi ID tombol yang baru diklik
) -> jstring {
    // Di dunia nyata, data ini disimpan di database (SQLite)
    // Untuk demo Odfiz, kita simpan daftar menu favorit
    let menus = vec!["KOPI", "SABUN", "SAMPEL", "STOK"];
    
    // Logika AI Sederhana: Menebak berdasarkan ID atau urutan
    // Disini kita buat simulasi 'Smart Suggestion'
    let suggested = match click_history {
        1 => "SABUN (Sering dibeli bareng)",
        2 => "KOPI (Pas buat istirahat)",
        3 => "LAPORAN (Cek hasil kerja)",
        _ => "MENU UTAMA",
    };

    let hasil = format!("🤖 Odfiz Suggestion: {}", suggested);
    let output = env.new_string(hasil).expect("Gagal buat string");
    output.into_raw()
}
