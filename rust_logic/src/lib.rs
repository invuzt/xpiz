use jni::objects::JClass;
use jni::sys::{jint, jstring};
use jni::JNIEnv;
use std::fs;
use std::path::Path;

struct XpizAI {
    xp: i32,
    reaction: i32,
}

impl XpizAI {
    fn load_memory() -> Self {
        // Path absolut yang paling standar untuk internal app storage
        let dir = "/data/data/com.invuzt.xpiz/files";
        let path = format!("{}/xpiz_data.txt", dir);
        
        // PASTIKAN FOLDER ADA
        let _ = fs::create_dir_all(dir);

        if Path::new(&path).exists() {
            if let Ok(data) = fs::read_to_string(&path) {
                let parts: Vec<&str> = data.trim().split(',').collect();
                if parts.len() == 2 {
                    let saved_xp = parts[0].parse().unwrap_or(4500);
                    let saved_re = parts[1].parse().unwrap_or(240);
                    return XpizAI { xp: saved_xp, reaction: saved_re };
                }
            }
        }
        XpizAI { xp: 4500, reaction: 240 }
    }

    fn save_memory(&self) {
        let path = "/data/data/com.invuzt.xpiz/files/xpiz_data.txt";
        let content = format!("{},{}", self.xp, self.reaction);
        // Pakai write biasa, kalau gagal ya sudah (tapi folder sudah dibuat di load)
        let _ = fs::write(path, content);
    }

    fn analyze(&mut self, id: i32) -> String {
        // NAIKKAN XP TIAP KLIK (BUKTI DINAMIS)
        self.xp += 10; 
        self.save_memory(); 

        match id {
            1 => self.training_logic(),
            2 => self.progress_logic(),
            _ => "XPIZ Core Active".to_string(),
        }
    }

    fn training_logic(&self) -> String {
        format!(
            "XPIZ AI TRAINING:\n            - React Speed: {}ms\n            - Storage: ONLINE\n            - Forecast: +10 XP Gained, 
            self.reaction
        )
    }

    fn progress_logic(&self) -> String {
        let percent = (self.xp as f32 / 5000.0) * 100.0;
        format!(
            "PERFORMANCE REPORT:\n            - Total XP: {}\n            - Accuracy: {:.1}%\n            - Forecast: Syncing Memory...", 
            self.xp, percent
        )
    }
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(
    env: JNIEnv,
    _class: JClass,
    page_id: jint,
) -> jstring {
    let mut ai = XpizAI::load_memory();
    let content = ai.analyze(page_id as i32);
    let output = env.new_string(content).expect("Gagal buat string");
    output.into_raw()
}
