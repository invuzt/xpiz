use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::{jstring};
use std::sync::atomic::{AtomicI32, Ordering};

static KOPI: AtomicI32 = AtomicI32::new(0);
static SABUN: AtomicI32 = AtomicI32::new(0);
// Menyimpan perintah terakhir untuk prediksi
static LAST_CMD: AtomicI32 = AtomicI32::new(0); // 1:Kopi, 2:Sabun, 3:Stok

#[no_mangle]
pub extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    env: JNIEnv,
    _class: JClass,
    input_java: jstring,
) -> jstring {
    let input: String = env.get_string(input_java.into()).expect("ERR").into();
    let parts: Vec<&str> = input.split_whitespace().collect();
    
    if parts.is_empty() { return return_string(&env, "EMPTY"); }

    let mut response = String::new();
    let mut prediction = "NONE";

    match parts[0] {
        "kopi" => {
            KOPI.fetch_add(1, Ordering::SeqCst);
            LAST_CMD.store(1, Ordering::SeqCst);
            response = "LOG: KOPI_REGISTERED".to_string();
            prediction = "stok --check"; // AI nebak abis kopi biasanya cek stok
        },
        "sabun" => {
            SABUN.fetch_add(1, Ordering::SeqCst);
            LAST_CMD.store(2, Ordering::SeqCst);
            response = "LOG: SABUN_REGISTERED".to_string();
            prediction = "kopi --add 1"; // AI nebak abis sabun biasanya beli kopi
        },
        "stok" => {
            LAST_CMD.store(3, Ordering::SeqCst);
            response = format!("STOK_INFO: K={}, S={}", KOPI.load(Ordering::SeqCst), SABUN.load(Ordering::SeqCst));
            prediction = "xpiz --status";
        },
        "xpiz" => {
            let k = KOPI.load(Ordering::SeqCst);
            let s = SABUN.load(Ordering::SeqCst);
            response = format!("STATUS|{}|{}|{}", k, s, (k+s).max(1));
            prediction = "clear";
        },
        _ => { response = format!("UNKNOWN_TOKEN: {}", parts[0]); }
    }

    // Format output: RESPONSE|PREDICTION
    let final_out = format!("{}|{}", response, prediction);
    return_string(&env, &final_out)
}

fn return_string(env: &JNIEnv, s: &str) -> jstring {
    let output = env.new_string(s).expect("Gagal");
    output.into_raw()
}
