use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::{jstring};
use std::sync::atomic::{AtomicI32, Ordering};

static KOPI: AtomicI32 = AtomicI32::new(0);
static SABUN: AtomicI32 = AtomicI32::new(0);
static LAST_CMD: AtomicI32 = AtomicI32::new(0);

#[no_mangle]
pub unsafe extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    mut env: JNIEnv,
    _class: JClass,
    input_java: jstring,
) -> jstring {
    // 1. CARA BENAR: Ubah pointer mentah jstring menjadi JString Rust
    let j_obj = unsafe { jni::objects::JObject::from_raw(input_java) };
    let j_str: &JString = &JString::from(j_obj);
    
    let input: String = env.get_string(j_str).expect("ERR").into();
    let parts: Vec<&str> = input.split_whitespace().collect();
    
    if parts.is_empty() { return return_string(&mut env, "EMPTY|NONE"); }

    let mut response = String::new();
    let mut prediction = "NONE";

    match parts[0] {
        "kopi" => {
            KOPI.fetch_add(1, Ordering::SeqCst);
            LAST_CMD.store(1, Ordering::SeqCst);
            response = "LOG: KOPI_REGISTERED".to_string();
            prediction = "stok"; 
        },
        "sabun" => {
            SABUN.fetch_add(1, Ordering::SeqCst);
            LAST_CMD.store(2, Ordering::SeqCst);
            response = "LOG: SABUN_REGISTERED".to_string();
            prediction = "kopi";
        },
        "stok" => {
            LAST_CMD.store(3, Ordering::SeqCst);
            response = format!("STOK: K={}, S={}", KOPI.load(Ordering::SeqCst), SABUN.load(Ordering::SeqCst));
            prediction = "xpiz --status";
        },
        "xpiz" => {
            let k = KOPI.load(Ordering::SeqCst);
            let s = SABUN.load(Ordering::SeqCst);
            response = format!("STATUS|{}|{}|0|{}", k, s, (k+s).max(1));
            prediction = "clear";
        },
        "clear" => {
            response = "TERMINAL_CLEANED".to_string();
            prediction = "kopi";
        },
        _ => { response = format!("UNKNOWN: {}", parts[0]); }
    }

    let final_out = format!("{}|{}", response, prediction);
    return_string(&mut env, &final_out)
}

fn return_string(env: &mut JNIEnv, s: &str) -> jstring {
    let output = env.new_string(s).expect("Gagal");
    output.into_raw()
}
