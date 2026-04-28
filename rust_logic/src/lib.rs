use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;
use evalexpr::*;

// Modul logika terpisah agar mudah ditambah fitur kedepannya
fn core_calculator(input: &str) -> Result<String, String> {
    let sanitized = input.replace("×", "*").replace("÷", "/");
    if sanitized.trim().is_empty() { return Ok("0".to_string()); }

    match eval(&sanitized) {
        Ok(res) => match res {
            Value::Int(i) => Ok(i.to_string()),
            Value::Float(f) => {
                let s = format!("{:.6}", f);
                Ok(s.trim_end_matches('0').trim_end_matches('.').to_string())
            },
            _ => Err("Invalid Type".to_string()),
        },
        Err(e) => Err(format!("Error: {}", e)),
    }
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_calculateNative(
    mut env: JNIEnv,
    _class: JClass,
    expression: JString,
    _unused: bool,
) -> jstring {
    let input: String = env.get_string(&expression)
        .map(|s| s.into())
        .unwrap_or_default();

    let output = match core_calculator(&input) {
        Ok(hasil) => hasil,
        Err(_) => "".to_string(),
    };

    env.new_string(output).unwrap().into_raw()
}
