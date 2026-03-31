use jni::JNIEnv;
use jni::objects::{JClass};
use jni::sys::{jint, jstring};
use std::sync::atomic::{AtomicI32, Ordering};

// Pakai Atomic agar data tersimpan aman di RAM selama App jalan
// Ini yang bikin AI "Ingat" hitungan sebelumnya
static KOPI_COUNT: AtomicI32 = AtomicI32::new(0);
static SABUN_COUNT: AtomicI32 = AtomicI32::new(0);
static STOK_COUNT: AtomicI32 = AtomicI32::new(0);

#[no_mangle]
pub extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    mut env: JNIEnv,
    _class: JClass,
    id: jint,
) -> jstring {
    // 1. Tambah hitungan berdasarkan ID yang dikirim Java
    match id {
        1 => { KOPI_COUNT.fetch_add(1, Ordering::SeqCst); }
        2 => { SABUN_COUNT.fetch_add(1, Ordering::SeqCst); }
        3 => { STOK_COUNT.fetch_add(1, Ordering::SeqCst); }
        _ => {}
    }

    // 2. Ambil nilai terbaru
    let k = KOPI_COUNT.load(Ordering::SeqCst);
    let s = SABUN_COUNT.load(Ordering::SeqCst);
    let t = STOK_COUNT.load(Ordering::SeqCst);

    // 3. Tentukan siapa yang paling banyak diklik (Pemenang)
    let mut pemenang = "⚖️ Skor masih seri...";
    
    if k > s && k > t {
        pemenang = "🔥 FAVORIT: KOPI";
    } else if s > k && s > t {
        pemenang = "🔥 FAVORIT: SABUN";
    } else if t > k && t > s {
        pemenang = "🔥 FAVORIT: STOK";
    }

    let hasil = format!("{}\n(Kopi: {} | Sabun: {} | Stok: {})", pemenang, k, s, t);

    let output = env.new_string(hasil).expect("Gagal buat string");
    output.into_raw()
}
