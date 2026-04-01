use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jstring};
use std::sync::Mutex;
use std::collections::HashMap;

// Memori Pintar AI Odfiz
struct AiBrain {
    word_count: HashMap<String, i32>,
}

lazy_static::lazy_static! {
    static ref BRAIN: Mutex<AiBrain> = Mutex::new(AiBrain {
        word_count: HashMap::new(),
    });
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    mut env: JNIEnv,
    _class: JClass,
    input_java: jstring,
) -> jstring {
    let input: String = env.get_string(&JString::from(unsafe { jni::objects::JObject::from_raw(input_java) }))
        .expect("ERR").into();
    let input = input.trim().to_lowercase();
    let mut brain = BRAIN.lock().unwrap();

    // AI LEARNING: Hitung seberapa sering kata ini muncul
    let count = brain.word_count.entry(input.clone()).or_insert(0);
    *count += 1;

    // AI DECISION: Kalau kata diketik 2x, jadikan TOMBOL OTOMATIS
    if *count == 2 {
        return return_string(&mut env, &format!("AUTO_BTN|{}", input.to_uppercase()));
    }

    // LOGIKA HARGA (Kasir Otomatis)
    if let Ok(harga) = input.parse::<f32>() {
        return return_string(&mut env, &format!("PAY|{}", harga));
    }

    return_string(&mut env, &format!("LEARNING|Data '{}' tersimpan. Ketik sekali lagi untuk jadikan shortcut.", input))
}

fn return_string(env: &mut JNIEnv, s: &str) -> jstring {
    env.new_string(s).expect("Gagal").into_raw()
}
