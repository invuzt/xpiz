use jni::objects::JClass;
use jni::sys::{jint, jstring};
use jni::JNIEnv;
use std::fs;
use std::path::Path;
use zip::write::FileOptions;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(
    env: JNIEnv,
    _class: JClass,
    page_id: jint,
) -> jstring {
    let dir = "/data/data/com.invuzt.xpiz/files";
    let path = "/data/data/com.invuzt.xpiz/files/XPIZ_Package.zip";

    // HALAMAN TRAINING: BUAT FILE ZIP
    if page_id == 1 {
        let _ = fs::create_dir_all(dir);
        if let Ok(file) = fs::File::create(path) {
            let mut zip = zip::ZipWriter::new(file);
            let _ = zip.start_file("build.txt", FileOptions::default());
            let _ = std::io::Write::write_all(&mut zip, b"BUILD OK");
            let _ = zip.finish();
            
            return env.new_string("BUILD SUCCESS:\n- File: XPIZ_Package.zip\n- Status: Zip Created\n- Logic: Active").unwrap().into_raw();
        }
    }

    // HALAMAN PROGRESS: CEK APAKAH FILE ADA (PENGGANTI LS)
    if page_id == 2 {
        let exists = Path::new(path).exists();
        let status = if exists { "FILE FOUND" } else { "NOT FOUND" };
        let size = if exists {
            fs::metadata(path).map(|m| m.len()).unwrap_or(0)
        } else { 0 };

        let res = format!("FILE EXPLORER:\n- Status: {}\n- Size: {} bytes\n- Path: Internal App", status, size);
        return env.new_string(res).unwrap().into_raw();
    }

    env.new_string("XPIZ READY").unwrap().into_raw()
}
