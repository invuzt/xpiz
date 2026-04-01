use jni::objects::JClass;
use jni::sys::{jint, jstring};
use jni::JNIEnv;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(
    mut env: JNIEnv,
    _class: JClass,
    page_id: jint,
) -> jstring {
    let content = match page_id {
        1 => "TRAINING (RUST):\n- Rhythm Match\n- Speed Test",
        2 => "PROGRESS (RUST):\n- Level: 71\n- XP: 4.500",
        _ => "XPIZ System Ready",
    };

    // Ini cara aman kirim string ke Java agar tidak SIGSEGV
    let output = env.new_string(content).expect("Gagal buat string Java");
    output.into_raw()
}
