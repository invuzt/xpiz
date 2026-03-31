use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::{jstring, jbyteArray, jint};

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_analyzeFrame(
    env: JNIEnv,
    _class: JClass,
    data: jbyteArray,
    width: jint,
    height: jint,
) -> jstring {
    // 1. Ambil data byte array dari Java (Data Gambar)
    let input = env.convert_byte_array(data).unwrap_or(vec![]);
    
    // 2. Simulasi Analisis di Rust
    let info = format!(
        "Rust Engine: Menerima Frame {}x{}. Ukuran: {} bytes. Status: Lancar Jaya! 🦀", 
        width, height, input.len()
    );

    // 3. Kirim hasil balik ke Java
    let output = env.new_string(info).expect("Gagal buat string!");
    output.into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_stringFromRust(
    env: JNIEnv,
    _class: JClass,
) -> jstring {
    let output = env.new_string("xpiz Hybrid System: Online!").unwrap();
    output.into_raw()
}
