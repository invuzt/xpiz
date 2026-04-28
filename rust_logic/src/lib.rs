use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;

#[no_mangle]
pub extern "C" fn Java_co_xpiz_MainActivity_prosesDataRust(
    mut env: JNIEnv,
    _class: JClass,
    input: JString,
) -> jstring {
    let input_java: String = env.get_string(&input).map(|s| s.into()).unwrap_or_default();
    
    // Teks normal (tidak kapital semua) agar lebih bagus
    let hasil_proses = format!("Respon Rust: {}", input_java);
    
    env.new_string(hasil_proses).unwrap().into_raw()
}
