use jni::JNIEnv;
use jni::objects::JClass;
use jni::sys::{jint, jstring};
use std::sync::atomic::{AtomicI32, Ordering};

static KOPI: AtomicI32 = AtomicI32::new(0);
static SABUN: AtomicI32 = AtomicI32::new(0);
static STOK: AtomicI32 = AtomicI32::new(0);

#[no_mangle]
pub extern "system" fn Java_com_invuzt_xpiz_MainActivity_predictBestButton(
    env: JNIEnv,
    _class: JClass,
    id: jint,
) -> jstring {
    let mut response = String::new();

    match id {
        1 => { KOPI.fetch_add(1, Ordering::SeqCst); response = "Logged: KOPI (+1)".to_string(); }
        2 => { SABUN.fetch_add(1, Ordering::SeqCst); response = "Logged: SABUN (+1)".to_string(); }
        3 => { STOK.fetch_add(1, Ordering::SeqCst); response = "Logged: STOK (+1)".to_string(); }
        // ID 99 kita gunakan khusus untuk request STATUS
        99 => {
            let k = KOPI.load(Ordering::SeqCst);
            let s = SABUN.load(Ordering::SeqCst);
            let t = STOK.load(Ordering::SeqCst);
            let total = (k + s + t).max(1); // Hindari bagi nol
            
            response = format!("STATUS|{}|{}|{}|{}", k, s, t, total);
        }
        _ => { response = "Error: Invalid ID".to_string(); }
    }

    let output = env.new_string(response).expect("Gagal");
    output.into_raw()
}
