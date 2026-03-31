use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};

// Di sini kita pakai simulasi 'Counter' sederhana
// Di project beneran, Mas bisa pakai SQLite agar datanya tidak hilang saat HP mati
static mut KOPI_COUNT: i32 = 0;
static mut SABUN_COUNT: i32 = 0;

#[no_mangle]
pub extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    mut env: JNIEnv,
    _class: JClass,
    id_tombol: jint,
) -> jstring {
    unsafe {
        // AI Menambah hitungan berdasarkan klik asli user
        if id_tombol == 1 { KOPI_COUNT += 1; }
        else if id_tombol == 2 { SABUN_COUNT += 1; }

        // AI Menentukan siapa pemenangnya
        let pemenang = if KOPI_COUNT > SABUN_COUNT {
            format!("🔥 Rekomendasi: KOPI (Diklik {}x)", KOPI_COUNT)
        } else if SABUN_COUNT > KOPI_COUNT {
            format!("🔥 Rekomendasi: SABUN (Diklik {}x)", SABUN_COUNT)
        } else {
            "📊 AI sedang mempelajari pola klik Mas...".to_string()
        };

        let output = env.new_string(pemenang).expect("Gagal");
        output.into_raw()
    }
}
