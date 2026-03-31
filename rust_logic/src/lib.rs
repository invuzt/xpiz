use jni::JNIEnv;
use jni::objects::{JClass};
use jni::sys::jstring;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_stringFromRust(
    mut env: JNIEnv,
    _class: JClass,
) -> jstring {
    // Teks khas xpiz!
    let output = env.new_string("Hello dari xpiz! 🦀\nBerhasil Terhubung via JNI!")
        .expect("Gagal buat string!");
    
    output.into_raw()
}

