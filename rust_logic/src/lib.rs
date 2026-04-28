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
    let hasil_proses = format!("RUST MENGOLAH: {}", input_java.to_uppercase());
    env.new_string(hasil_proses).unwrap().into_raw()
}
