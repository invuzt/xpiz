use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::{jint, jstring};
use smartcore::neighbors::knn_classifier::KNNClassifier;
use smartcore::linalg::basic::matrix::DenseMatrix;

#[no_mangle]
pub extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    env: JNIEnv,
    _class: JClass,
    id_sekarang: jint,
) -> jstring {
    // 1. DATA LATIHAN
    // Smartcore v0.3.x langsung mengembalikan DenseMatrix, tanpa Result.
    let x = DenseMatrix::from_2d_array(&[
        &[1.0], // Klik Kopi
        &[2.0], // Klik Sabun
        &[3.0], // Klik Stok
    ]);

    // Label: 1=Kopi, 2=Sabun, 3=Stok
    let y = vec![3, 1, 2]; 

    // 2. LATIH AI
    let knn = KNNClassifier::fit(&x, &y, Default::default()).unwrap();

    // 3. PREDIKSI
    let input_user = DenseMatrix::from_2d_array(&[&[id_sekarang as f64]]);
    let prediksi = knn.predict(&input_user).unwrap();
    let id_prediksi = prediksi[0];

    let pesan = match id_prediksi {
        1 => "🤖 Prediksi: Habis ini Mas bakal klik KOPI",
        2 => "🤖 Prediksi: Sepertinya Mas mau cek SABUN",
        3 => "🤖 Prediksi: Mas mau update STOK ya?",
        _ => "📊 AI sedang membaca pola...",
    };

    let output = env.new_string(pesan).expect("Gagal");
    output.into_raw()
}
