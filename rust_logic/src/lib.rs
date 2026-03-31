use jni::JNIEnv;
use jni::objects::{JClass, JByteArray};
use jni::sys::{jstring, jbyteArray, jint};

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_analyzeFrame(
    env: JNIEnv,
    _class: JClass,
    data: jbyteArray,
    width: jint,
    height: jint,
) -> jstring {
    // 1. Bungkus pointer mentah (data) menjadi objek JByteArray yang dimengerti Rust
    let byte_array = unsafe { JByteArray::from_raw(data) };
    
    // 2. Sekarang baru bisa dikonversi ke Vec<u8> (byte array milik Rust)
    let input = env.convert_byte_array(&byte_array).unwrap_or(vec![]);
    
    // 3. Simulasi Analisis (Contoh: ambil ukuran data)
    let info = format!(
        "Rust Engine: Frame {}x{} diterima. Data: {} bytes. Aman! 🦀", 
        width, height, input.len()
    );

    // Jangan lupa "lupakan" byte_array agar tidak terjadi double free karena JNI yang mengaturnya
    std::mem::forget(byte_array);

    // 4. Kirim hasil balik ke Java
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
