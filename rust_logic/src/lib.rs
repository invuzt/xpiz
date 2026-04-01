use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::{jstring};
use std::sync::atomic::{AtomicI32, Ordering};

static KOPI: AtomicI32 = AtomicI32::new(0);
static SABUN: AtomicI32 = AtomicI32::new(0);

#[no_mangle]
pub extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    env: JNIEnv,
    _class: JClass,
    input_java: jstring,
) -> jstring {
    let input: String = env.get_string(input_java.into()).expect("Gagal read string").into();
    let parts: Vec<&str> = input.split_whitespace().collect();
    
    if parts.is_empty() {
        return return_string(&env, "> ERR: Empty command");
    }

    let response = match parts[0] {
        // PERINTAH: kopi --add [jumlah]
        "kopi" => {
            if parts.len() > 2 && parts[1] == "--add" {
                let amt = parts[2].parse::<i32>().unwrap_or(1);
                KOPI.fetch_add(amt, Ordering::SeqCst);
                format!("> XPIZ-LANG: Added {} Kopi to Neural Cache.", amt)
            } else {
                format!("> XPIZ-LANG: Kopi count is {}.", KOPI.load(Ordering::SeqCst))
            }
        },
        // PERINTAH: sabun --set [jumlah]
        "sabun" => {
            if parts.len() > 2 && parts[1] == "--set" {
                let amt = parts[2].parse::<i32>().unwrap_or(0);
                SABUN.store(amt, Ordering::SeqCst);
                format!("> XPIZ-LANG: Sabun overwritten to {}.", amt)
            } else {
                format!("> XPIZ-LANG: Sabun count is {}.", SABUN.load(Ordering::SeqCst))
            }
        },
        // PERINTAH: xpiz --status
        "xpiz" => {
            if parts.len() > 1 && parts[1] == "--status" {
                let k = KOPI.load(Ordering::SeqCst);
                let s = SABUN.load(Ordering::SeqCst);
                let total = (k + s).max(1);
                format!("STATUS|{}|{}|0|{}", k, s, total)
            } else {
                "> XPIZ-LANG: Try 'xpiz --status'".to_string()
            }
        },
        "help" => "> COMMANDS: kopi --add [n], sabun --set [n], xpiz --status".to_string(),
        _ => format!("> XPIZ-LANG: Unknown token '{}'", parts[0]),
    };

    return_string(&env, &response)
}

fn return_string(env: &JNIEnv, s: &str) -> jstring {
    let output = env.new_string(s).expect("Gagal create string");
    output.into_raw()
}
