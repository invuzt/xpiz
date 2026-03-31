use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;

#[no_mangle]
pub extern "system" fn Java_com_invuzt_xpiz_MainActivity_analyzeFrame(
    mut env: JNIEnv,
    _class: JClass,
    file_path: JString,
    _w: i32,
    _h: i32,
) -> jstring {
    let path: String = env.get_string(&file_path).unwrap().into();
    
    // Nanti di sini Rust akan buka file 'path' dan gambar tulisan GPS/Jam
    let response = format!("Rust Editor: File {} siap diberi Watermark GPS & Jam!", path);

    env.new_string(response).unwrap().into_raw()
}
