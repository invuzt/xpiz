use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_helloRust(
    mut env: JNIEnv,
    _class: JClass,
    input: JString,
) -> jstring {
    let name: String = env.get_string(&input).map(|s| s.into()).unwrap_or_default();
    let response = format!("Hello {} dari Rust!", name);
    env.new_string(response).unwrap().into_raw()
}
