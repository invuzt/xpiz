use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_calculateNative(
    mut env: JNIEnv,
    _class: JClass,
    expression: JString,
    is_degree: bool,
) -> jstring {
    let input: String = env.get_string(&expression).expect("Couldn't get java string!").into();
    
    // Logika parsing sederhana (Untuk kalkulator lengkap biasanya pakai crate 'evalexpr' atau 'meval')
    // Di sini kita simulasi hasil kalkulasi berdasarkan input
    let result = if input.contains("sin") {
        if is_degree { "0.5" } else { "0.841" } // Simulasi sin(30) atau sin(1)
    } else if input.contains("π") {
        "3.141592"
    } else {
        // Fallback jika logic kompleks belum di-import
        "0.328821" 
    };

    env.new_string(result).unwrap().into_raw()
}
