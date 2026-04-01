use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jstring};
use std::sync::Mutex;

static QUEUE_NUMBER: Mutex<i32> = Mutex::new(1);

#[no_mangle]
pub unsafe extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    mut env: JNIEnv,
    _class: JClass,
    input_java: jstring,
) -> jstring {
    let input: String = env.get_string(&JString::from(unsafe { jni::objects::JObject::from_raw(input_java) }))
        .expect("ERR").into();
    let input = input.trim().to_lowercase();

    // Ambil & Naikkan Nomor Antrian
    if input == "get_next_queue" {
        let mut q = QUEUE_NUMBER.lock().unwrap();
        let current = *q;
        *q += 1;
        return return_string(&mut env, &format!("{}", current));
    }

    if input.contains(':') {
        let parts: Vec<&str> = input.split(':').collect();
        return return_string(&mut env, &format!("ADD|{}|{}", parts[0].trim().to_uppercase(), parts[1].trim()));
    }

    if let Ok(bayar) = input.parse::<f32>() {
        return return_string(&mut env, &format!("PAY_CUSTOM|{}", bayar));
    }

    return_string(&mut env, "IDLE")
}

fn return_string(env: &mut JNIEnv, s: &str) -> jstring {
    env.new_string(s).expect("Gagal").into_raw()
}
