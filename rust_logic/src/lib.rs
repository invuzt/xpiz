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
    
    // Ganti simbol yang user-friendly dengan operator pemrograman
    let sanitized = input
        .replace("×", "*")
        .replace("÷", "/")
        .replace("π", &PI.to_string());

    // Fungsi pembantu untuk konversi sudut
    let context = HashMapContext::new();
    
    // Default formatting untuk hasil
    let mut final_result = String::from("Error");

    // Tangani fungsi trigonometri dan sqrt secara khusus
    if sanitized.contains("sin") || sanitized.contains("cos") || sanitized.contains("tan") || sanitized.contains("sqrt") {
        // Implementasi sederhana: Evaluasi dulu angka di dalam kurung, lalu terapkan fungsi
        // Ini adalah cara cepat. Untuk parser lengkap butuh fungsi regex/custom context evalexpr.
        let mut processed = sanitized.clone();
        
        // Pola: sin(angka)
        for func in &["sin", "cos", "tan", "sqrt"] {
            while let Some(start_idx) = processed.find(&format!("{}(", func)) {
                if let Some(end_idx) = processed[start_idx..].find(')') {
                    let actual_end = start_idx + end_idx;
                    let inner_expr = &processed[start_idx + func.len() + 1..actual_end];
                    
                    if let Ok(Value::Float(mut val)) = eval_with_context(inner_expr, &context) {
                        if *func != "sqrt" && is_degree {
                            val = val * (PI / 180.0); // Convert Degree to Radian
                        }
                        
                        let res_val = match *func {
                            "sin" => val.sin(),
                            "cos" => val.cos(),
                            "tan" => val.tan(),
                            "sqrt" => val.sqrt(),
                            _ => 0.0
                        };
                        
                        processed.replace_range(start_idx..actual_end + 1, &format!("{:.6}", res_val));
                    } else { break; } // Gagal parse angka dalam kurung
                } else { break; } // Kurung tutup hilang
            }
        }
        
        // Evaluasi hasil akhir setelah fungsi diproses
        if let Ok(res) = eval_with_context(&processed, &context) {
             final_result = format_value(res);
        }

    } else {
        // Hitungan standar (seperti 3+2)
        if let Ok(res) = eval_with_context(&sanitized, &context) {
            final_result = format_value(res);
        }
    }

    env.new_string(final_result).unwrap().into_raw()
}

// Fungsi pembantu untuk format output (hapus .0 jika bilangan bulat)
fn format_value(v: Value) -> String {
    match v {
        Value::Int(i) => i.to_string(),
        Value::Float(f) => {
            let s = format!("{:.6}", f);
            if s.contains('.') {
                s.trim_end_matches('0').trim_end_matches('.').to_string()
            } else { s }
        },
        _ => String::from("Error")
    }
}
