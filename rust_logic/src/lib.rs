use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::{jint, jstring};
use smartcore::neighbors::knn_classifier::KNNClassifier;
use smartcore::linalg::naive::dense_matrix::DenseMatrix;

#[no_mangle]
pub extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    env: JNIEnv,
    _class: JClass,
    id_sekarang: jint,
) -> jstring {
    // 1. DATA LATIHAN (Simulasi)
    // Fitur: [ID_Sekarang], Label: [ID_Selanjutnya]
    let x = DenseMatrix::from_2d_array(&[
        &[1.0], // Klik Kopi -> Biasanya lanjut Stok (3)
        &[2.0], // Klik Sabun -> Biasanya lanjut Kopi (1)
        &[3.0], // Klik Stok -> Biasanya lanjut Sabun (2)
    ]);
    let y = vec![3.0, 1.0, 2.0];

    // 2. LATIH AI (Sangat cepat di Rust)
    let knn = KNNClassifier::fit(&x, &y, Default::default()).unwrap();

    // 3. PREDIKSI
    let input_user = DenseMatrix::from_2d_array(&[&[id_sekarang as f64]]);
    let prediksi = knn.predict(&input_user).unwrap();
    let id_prediksi = prediksi[0] as i32;

    let pesan = match id_prediksi {
        1 => "🤖 Prediksi: Habis ini Mas bakal klik KOPI",
        2 => "🤖 Prediksi: Sepertinya Mas mau cek SABUN",
        3 => "🤖 Prediksi: Mas mau update STOK ya?",
        _ => "📊 AI sedang membaca pola...",
    };

    let output = env.new_string(pesan).expect("Gagal");
    output.into_raw()
}
