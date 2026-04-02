mod ui;
use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};
use jni::JNIEnv;
use xpiz_brain::XpizBrain as Brain;
use std::fs;

static mut LAST_INPUT: String = String::new();
static mut AI_BRAIN: Option<Brain> = None;
const BRAIN_PATH: &str = "/data/user/0/com.invuzt.xpiz/files/brain.json";

fn get_brain() -> &'static mut Brain {
    unsafe {
        if AI_BRAIN.is_none() {
            let mut brain = fs::read_to_string(BRAIN_PATH)
                .ok()
                .and_then(|data| serde_json::from_str(&data).ok())
                .unwrap_or_else(|| Brain::default());
            
            // PRESET OTOMATIS
            if brain.layouts.is_empty() {
                // Register Kata Kunci
                brain.learn("kasir belanja bayar", "kasir");
                brain.learn("pos input export data", "pos");

                // Register Tombol (Layout)
                brain.layouts.insert("kasir".to_string(), vec![
                    "TRANSAKSI BARU|ACTION".to_string(),
                    "CEK STOK|ACTION".to_string(),
                    "LAPORAN HARIAN|ACTION".to_string()
                ]);
                brain.layouts.insert("pos".to_string(), vec![
                    "INPUT BARANG|ACTION".to_string(),
                    "EXPORT EXCEL|ACTION".to_string(),
                    "DATABASE SETTINGS|ACTION".to_string()
                ]);
            }
            AI_BRAIN = Some(brain);
        }
        AI_BRAIN.as_mut().unwrap()
    }
}

// ... (sisanya fungsi getSystemConfig & save_brain tetap sama)

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(env: JNIEnv, _class: JClass, id: jint) -> jstring {
    let brain = get_brain();
    if id == 2 {
        return env.new_string("BRAIN ANALYTICS|LABEL").unwrap().into_raw();
    }

    let input = unsafe { &LAST_INPUT };
    let prediction = brain.predict(input);
    
    let mut menu = format!("MODE: {}|LABEL\n", prediction.to_uppercase());
    
    // Ambil layout dari memori AI berdasarkan prediksi
    if let Some(buttons) = brain.layouts.get(&prediction) {
        menu.push_str(&buttons.join("\n"));
    } else {
        menu = "XPIZ READY|LABEL\nKETIK 'KASIR' ATAU 'POS'|LABEL".to_string();
    }

    env.new_string(menu).unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_handleTouch(mut env: JNIEnv, _class: JClass, tag: jstring, _val: jstring) -> jstring {
    let t: String = env.get_string(&unsafe { JString::from_raw(tag) }).unwrap().into();
    
    // Logika tombol dinamis
    match t.as_str() {
        "SEND_INPUT" => { /* Java handles input change */ },
        "EXPORT EXCEL" => { /* Logika export */ },
        "TRANSAKSI BARU" => { /* Logika kasir */ },
        _ => {}
    }
    
    env.new_string("REFRESH").unwrap().into_raw()
}
