use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::{jint, jstring};
use smartcore::neighbors::knn_classifier::KNNClassifier;
use smartcore::linalg::basic::matrix::DenseMatrix;
use std::sync::atomic::{AtomicI32, Ordering};

// Memori Jangka Pendek: Mengingat klik terakhir untuk bahan belajar
static LAST_CLICK: AtomicI32 = AtomicI32::new(1);
// Penampung Data Latihan (Sederhana)
static mut HISTORY_X: Vec<f64> = Vec::new();
static mut HISTORY_Y: Vec<i32> = Vec::new();

#[no_mangle]
pub extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    env: JNIEnv,
    _class: JClass,
    id_sekarang: jint,
) -> jstring {
    unsafe {
        let last = LAST_CLICK.load(Ordering::SeqCst);
        
        // 1. BELAJAR: Simpan pola "Klik Tadi -> Klik Sekarang"
        if HISTORY_X.len() < 50 { // Batasi biar RAM gak bengkak
            HISTORY_X.push(last as f64);
            HISTORY_Y.push(id_sekarang as i32);
        } else {
            // Kalau sudah penuh, hapus data paling lama (First In First Out)
            HISTORY_X.remove(0);
            HISTORY_Y.remove(0);
            HISTORY_X.push(last as f64);
            HISTORY_Y.push(id_sekarang as i32);
        }

        // Simpan klik sekarang sebagai 'Klik Tadi' untuk putaran berikutnya
        LAST_CLICK.store(id_sekarang, Ordering::SeqCst);

        // 2. PREDIKSI: Kalau data sudah cukup (minimal 3 pola), pakai Smartcore
        let pesan = if HISTORY_X.len() > 3 {
            let x = DenseMatrix::from_2d_array(&HISTORY_X.iter().map(|&v| vec![v]).collect::<Vec<_>>().iter().map(|v| v.as_slice()).collect::<Vec<_>>());
            
            let knn = KNNClassifier::fit(&x, &HISTORY_Y, Default::default()).unwrap();
            let input = DenseMatrix::from_2d_array(&[&[id_sekarang as f64]]);
            let id_prediksi = knn.predict(&input).unwrap()[0];

            match id_prediksi {
                1 => format!("🤖 AI Tebak: Habis ini Mas mau KOPI (Data: {} pola)", HISTORY_X.len()),
                2 => format!("🤖 AI Tebak: Kayaknya butuh SABUN (Data: {} pola)", HISTORY_X.len()),
                3 => format!("🤖 AI Tebak: Mau cek STOK kan? (Data: {} pola)", HISTORY_X.len()),
                _ => "📊 AI sedang berhitung...".to_string(),
            }
        } else {
            "📊 Klik beberapa kali lagi biar AI kenal Mas...".to_string()
        };

        let output = env.new_string(pesan).expect("Gagal");
        output.into_raw()
    }
}
