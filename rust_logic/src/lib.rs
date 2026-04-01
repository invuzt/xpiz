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

    if input == "print" || input == "struk" {
        return return_string(&mut env, "ACTION_PRINT");
    }

    if input.contains(':') {
        let parts: Vec<&str> = input.split(':').collect();
        return return_string(&mut env, &format!("NEW_BTN|{}|{}", parts[0].trim().to_uppercase(), parts[1].trim()));
    }

    if let Ok(val) = input.parse::<f32>() {
        return return_string(&mut env, &format!("CASH|{}", val));
    }

    return_string(&mut env, &format!("NEW_BTN|{}|0", input.to_uppercase()))
}

fn return_string(env: &mut JNIEnv, s: &str) -> jstring {
    env.new_string(s).expect("Gagal").into_raw()
}
