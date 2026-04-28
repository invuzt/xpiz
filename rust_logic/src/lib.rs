use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;

#[no_mangle]
pub extern "C" fn Java_co_xpiz_MainActivity_getMsg(
    mut env: JNIEnv,
    _class: JClass,
    input: JString,
) -> jstring {
    let name: String = env.get_string(&input).map(|s| s.into()).unwrap_or_default();
    let response = format!("xpiz.co: {} siap digunakan!", name);
    env.new_string(response).unwrap().into_raw()
}
