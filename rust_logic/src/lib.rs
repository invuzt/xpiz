use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jstring};
use std::collections::HashMap;
use std::sync::Mutex;

// Memori Otomatis: Menyimpan ribuan barang tanpa diketik manual
lazy_static::lazy_static! {
    static ref INVENTORY_MAP: Mutex<HashMap<String, i32>> = Mutex::new(HashMap::new());
    static ref PATTERN_HISTORY: Mutex<Vec<i32>> = Mutex::new(Vec::new());
    static ref ID_COUNTER: Mutex<i32> = Mutex::new(1);
    static ref REVERSE_MAP: Mutex<HashMap<i32, String>> = Mutex::new(HashMap::new());
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    mut env: JNIEnv,
    _class: JClass,
    input_java: jstring,
) -> jstring {
    let j_obj = unsafe { jni::objects::JObject::from_raw(input_java) };
    let j_str: &JString = &JString::from(j_obj);
    let input: String = env.get_string(j_str).expect("ERR").into();
    let word = input.trim().to_lowercase();

    if word.is_empty() { return return_string(&mut env, "EMPTY|NONE"); }

    let mut inv = INVENTORY_MAP.lock().unwrap();
    let mut rev = REVERSE_MAP.lock().unwrap();
    let mut counter = ID_COUNTER.lock().unwrap();
    let mut history = PATTERN_HISTORY.lock().unwrap();

    // 1. REGISTRASI OTOMATIS: Kalau ada kata baru, langsung jadi ID baru
    let current_id = *inv.entry(word.clone()).or_insert_with(|| {
        let new_id = *counter;
        rev.insert(new_id, word.clone());
        *counter += 1;
        new_id
    });

    // 2. BELAJAR POLA: Cek apa yang biasanya diketik SETELAH kata ini
    let mut prediction = "NONE".to_string();
    if let Some(&last_id) = history.last() {
        // Cari di history, biasanya setelah last_id itu apa?
        for i in 0..history.len().saturating_sub(1) {
            if history[i] == last_id {
                let suspected_next = history[i+1];
                if let Some(name) = rev.get(&suspected_next) {
                    prediction = name.clone();
                    break;
                }
            }
        }
    }
    
    history.push(current_id);
    if history.len() > 100 { history.remove(0); } // Biar gak menuhi RAM

    let response = format!("INFO: '{}' Registered (ID: {})", word.to_uppercase(), current_id);
    let final_out = format!("{}|{}", response, prediction);
    
    return_string(&mut env, &final_out)
}

fn return_string(env: &mut JNIEnv, s: &str) -> jstring {
    let output = env.new_string(s).expect("Gagal");
    output.into_raw()
}
