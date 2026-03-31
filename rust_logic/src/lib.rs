use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};
use std::fs;
use std::path::Path;

#[no_mangle]
pub extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    mut env: JNIEnv,
    _class: JClass,
    id_tombol: jint,
) -> jstring {
    // Alamat file penyimpanan di Android (disimpan di folder files aplikasi)
    let path = "/data/data/com.invuzt.xpiz/files/ai_memory.txt";
    
    // 1. Baca data lama dari file, kalau gak ada mulai dari 0
    let content = fs::read_to_string(path).unwrap_or_else(|_| "0,0".to_string());
    let parts: Vec<&str> = content.split(',').collect();
    let mut kopi: i32 = parts[0].parse().unwrap_or(0);
    let mut sabun: i32 = parts[1].parse().unwrap_or(0);

    // 2. Tambah hitungan berdasarkan klik asli
    if id_tombol == 1 { kopi += 1; }
    else if id_tombol == 2 { sabun += 1; }

    // 3. SIMPAN KEMBALI KE FILE (Ini rahasia biar gak amnesia)
    let data_baru = format!("{},{}", kopi, sabun);
    let _ = fs::write(path, data_baru);

    // 4. Berikan hasil prediksi
    let hasil = if kopi > sabun {
        format!("🔥 TOP: KOPI (Total {}x klik)", kopi)
    } else if sabun > kopi {
        format!("🔥 TOP: SABUN (Total {}x klik)", sabun)
    } else {
        format!("📊 Skor Seri: {} - {}", kopi, sabun)
    };

    let output = env.new_string(hasil).expect("Gagal");
    output.into_raw()
}
