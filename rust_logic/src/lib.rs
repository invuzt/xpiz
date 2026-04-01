use jni::JNIEnv;
use jni::objects::{JClass};
use jni::sys::jstring;

#[no_mangle]
pub unsafe extern "system" fn Java_com_invuzt_xpiz_MainActivity_getHelloFromRust(
    mut env: JNIEnv,
    _class: JClass,
) -> jstring {
    let message = "Halo Dunia dari Rust Ponorogo!";
    env.new_string(message).expect("Gagal").into_raw()
}

