use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jstring};
use std::sync::Mutex;
use std::collections::HashMap;

// Memori Pintar AI Odfiz
struct AiBrain {
    stok: HashMap<String, i32>,
    popularitas: HashMap<String, i32>,
}

lazy_static::lazy_static! {
    static ref BRAIN: Mutex<AiBrain> = Mutex::new(AiBrain {
        stok: HashMap::new(),
        popularitas: HashMap::new(),
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

    // 1. AI STOCK CONTROL: Ketik "stok dimsum : 50"
    if input.starts_with("stok ") {
        let clean = input[5..].trim();
        if let Some((n, s)) = clean.split_once(':') {
            let s_val = s.trim().parse().unwrap_or(0);
            brain.stok.insert(n.trim().to_uppercase(), s_val);
            return return_string(&mut env, &format!("AI_MSG|Stok {} diset ke {}", n.to_uppercase(), s_val));
        }
    }

    // 2. AI TRANSACTION & INVENTORY DEDUCTION
    if input.starts_with("jual ") {
        let nama = input[5..].trim().to_uppercase();
        let s = brain.stok.entry(nama.clone()).or_insert(10);
        *s -= 1;
        
        let pop = brain.popularitas.entry(nama.clone()).or_insert(0);
        *pop += 1;

        let mut msg = format!("OK|{}", nama);
        if *s <= 3 { msg = format!("AI_WARN|{}|Sisa stok kritis: {}!", nama, s); }
        return return_string(&mut env, &msg);
    }

    // 3. LOGIKA LAMA (ADD & PAY)
    if input.contains(':') {
        let parts: Vec<&str> = input.split(':').collect();
        return return_string(&mut env, &format!("ADD|{}|{}", parts[0].trim().to_uppercase(), parts[1].trim()));
    }
    
    if let Ok(val) = input.parse::<f32>() {
        return return_string(&mut env, &format!("PAY|{}", val));
    }

    return_string(&mut env, "AI_IDLE|Ready, Mas!")
}

fn return_string(env: &mut JNIEnv, s: &str) -> jstring {
    env.new_string(s).expect("Gagal").into_raw()
}
