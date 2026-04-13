use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;
use std::fs;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_checkRustConnection(
    mut env: JNIEnv,
    _class: JClass,
) -> jstring {
    let response = "Status: Rust Engine Connected! 🚀";
    env.new_string(response).expect("Err").into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_saveMarkdownNative(
    mut env: JNIEnv,
    _class: JClass,
    path: JString,
    content: JString,
) -> jstring {
    let file_path: String = env.get_string(&path).expect("Err").into();
    let file_content: String = env.get_string(&content).expect("Err").into();

    match fs::write(&file_path, &file_content) {
        Ok(_) => env.new_string("✅ Tersimpan").unwrap().into_raw(),
        Err(e) => env.new_string(format!("❌ Gagal: {}", e)).unwrap().into_raw(),
    }
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_listVaultFiles(
    mut env: JNIEnv,
    _class: JClass,
    dir_path: JString,
) -> jstring {
    let path: String = env.get_string(&dir_path).expect("Err").into();
    
    // Membaca direktori dan menggabungkan nama file dengan pemisah "|"
    match fs::read_dir(path) {
        Ok(entries) => {
            let file_names: Vec<String> = entries
                .filter_map(|entry| entry.ok())
                .map(|entry| entry.file_name().to_string_lossy().into_owned())
                .filter(|name| name.ends_with(".md"))
                .collect();
            
            let result = if file_names.is_empty() {
                "Kosong".to_string()
            } else {
                file_names.join("|")
            };
            env.new_string(result).unwrap().into_raw()
        },
        Err(_) => env.new_string("Error Membaca Vault").unwrap().into_raw(),
    }
}
