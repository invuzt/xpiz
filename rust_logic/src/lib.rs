use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;
use evalexpr::*;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_calculateNative(
    mut env: JNIEnv,
    _class: JClass,
    expression: JString,
    _unused: bool,
) -> jstring {
    let input: String = env.get_string(&expression).expect("Invalid string").into();
    
    // Ganti simbol visual ke operator matematika
    let sanitized = input.replace("×", "*").replace("÷", "/");

    let result = match eval(&sanitized) {
        Ok(res) => match res {
            Value::Int(i) => i.to_string(),
            Value::Float(f) => {
                let s = format!("{:.6}", f);
                s.trim_end_matches('0').trim_end_matches('.').to_string()
            },
            _ => "0".to_string(),
        },
        Err(_) => "".to_string(), // Kosongkan saja kalau belum lengkap ngetiknya
    };

    env.new_string(result).unwrap().into_raw()
}
