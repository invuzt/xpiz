use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jstring};

#[no_mangle]
pub unsafe extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    mut env: JNIEnv,
    _class: JClass,
    input_java: jstring,
) -> jstring {
    let input: String = env.get_string(&JString::from(unsafe { jni::objects::JObject::from_raw(input_java) }))
        .expect("ERR").into();
    let input = input.trim().to_lowercase();

    // Logika Prediksi Uang Bayar (AI Mode)
    if input.starts_with("predict_pay|") {
        let total: f32 = input[12..].parse().unwrap_or(0.0);
        if total == 0.0 { return return_string(&mut env, "NONE"); }
        
        // AI menebak pecahan uang: misal 32rb -> sarankan 35rb, 40rb, 50rb, 100rb
        let p1 = (total / 5000.0).ceil() * 5000.0;
        let p2 = (total / 10000.0).ceil() * 10000.0;
        let p3 = 50000.0;
        let p4 = 100000.0;
        
        return return_string(&mut env, &format!("SUGGEST|{}|{}|{}|{}", p1, p2, p3, p4));
    }

    if input.contains(':') {
        let parts: Vec<&str> = input.split(':').collect();
        return return_string(&mut env, &format!("ADD|{}|{}", parts[0].trim().to_uppercase(), parts[1].trim()));
    }

    return_string(&mut env, "IDLE")
}

fn return_string(env: &mut JNIEnv, s: &str) -> jstring {
    env.new_string(s).expect("Gagal").into_raw()
}
