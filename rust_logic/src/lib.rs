use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;
use std::fs;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_checkRustConnection(
    mut env: JNIEnv,
    _class: JClass,
) -> jstring {
    env.new_string("Status: Rust Engine Connected! 🚀").unwrap().into_raw()
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
    if let Ok(entries) = fs::read_dir(path) {
        let file_names: Vec<String> = entries
            .filter_map(|e| e.ok())
            .map(|e| e.file_name().to_string_lossy().into_owned())
            .filter(|n| n.ends_with(".md"))
            .collect();
        let res = if file_names.is_empty() { "Kosong".to_string() } else { file_names.join("|") };
        return env.new_string(res).unwrap().into_raw();
    }
    env.new_string("Kosong").unwrap().into_raw()
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_readMarkdownNative(
    mut env: JNIEnv,
    _class: JClass,
    path: JString,
) -> jstring {
    let file_path: String = env.get_string(&path).expect("Err").into();
    match fs::read_to_string(file_path) {
        Ok(c) => env.new_string(c).unwrap().into_raw(),
        Err(_) => env.new_string("").unwrap().into_raw(),
    }
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_renderMarkdownNative(
    mut env: JNIEnv,
    _class: JClass,
    content: JString,
) -> jstring {
    let raw: String = env.get_string(&content).expect("Err").into();
    let html = raw.lines().map(|line| {
        let mut p = line.to_string();
        
        // Zettelkasten Link Builder Logic
        while let (Some(start), Some(end)) = (p.find("[["), p.find("]]")) {
            let link_text = &p[start+2..end];
            let replacement = format!("<a href='{}'>{}</a>", link_text, link_text);
            p.replace_range(start..end+2, &replacement);
        }

        if p.starts_with("# ") { format!("<h1>{}</h1>", &p[2..]) }
        else if p.starts_with("## ") { format!("<h2>{}</h2>", &p[3..]) }
        else { format!("<p>{}</p>", p) }
    }).collect::<Vec<String>>().join("");
    env.new_string(html).unwrap().into_raw()
}
