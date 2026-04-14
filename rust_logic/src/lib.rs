use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;
use evalexpr::*;
use std::f64::consts::PI;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_calculateNative(
    mut env: JNIEnv,
    _class: JClass,
    expression: JString,
    is_degree: bool,
) -> jstring {
    let input: String = env.get_string(&expression).expect("Invalid string").into();
    
    // 1. Bersihkan input dan siapkan fungsi custom untuk evalexpr
    let mut context = HashMapContext::new();
    
    // Tambahkan konstanta PI
    let _ = context.set_value("pi".into(), Value::Float(PI));

    // 2. Fungsi pembantu untuk handle sin/cos/tan dengan DEG/RAD
    let factor = if is_degree { PI / 180.0 } else { 1.0 };

    // Daftarkan fungsi matematika ke dalam context evalexpr
    let _ = context.set_function("sin".into(), Function::new(move |v| {
        let val = v.as_float()?;
        Ok(Value::Float((val * factor).sin()))
    }));

    let _ = context.set_function("cos".into(), Function::new(move |v| {
        let val = v.as_float()?;
        Ok(Value::Float((val * factor).cos()))
    }));

    let _ = context.set_function("tan".into(), Function::new(move |v| {
        let val = v.as_float()?;
        Ok(Value::Float((val * factor).tan()))
    }));

    let _ = context.set_function("sqrt".into(), Function::new(|v| {
        let val = v.as_float()?;
        Ok(Value::Float(val.sqrt()))
    }));

    // 3. Sanitasi string input agar sesuai format pemrogaman
    let sanitized = input
        .to_lowercase()
        .replace("×", "*")
        .replace("÷", "/")
        .replace("π", "pi");

    // 4. Eksekusi perhitungan
    let final_result = match eval_with_context(&sanitized, &context) {
        Ok(res) => format_value(res),
        Err(_) => String::from("Error"),
    };

    env.new_string(final_result).unwrap().into_raw()
}

fn format_value(v: Value) -> String {
    match v {
        Value::Int(i) => i.to_string(),
        Value::Float(f) => {
            if f.is_nan() { return "Error".to_string(); }
            // Jika hasil sangat mendekati nol (karena floating point error)
            let val = if f.abs() < 1e-10 { 0.0 } else { f };
            let s = format!("{:.6}", val);
            if s.contains('.') {
                let trimmed = s.trim_end_matches('0').trim_end_matches('.');
                if trimmed.is_empty() { "0".to_string() } else { trimmed.to_string() }
            } else { s }
        },
        _ => String::from("Error")
    }
}
