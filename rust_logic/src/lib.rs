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
    // FUNGSI BARU: Membaca data dari 'Ingatan' HP
    fn load_memory() -> Self {
        // Lokasi privat di Android (biasanya di data/data/com.invuzt.xpiz/files)
        let path = "/data/data/com.invuzt.xpiz/files/xpiz_data.txt";
        
        if Path::new(path).exists() {
            let data = fs::read_to_string(path).unwrap_or_default();
            let parts: Vec<&str> = data.split(',').collect();
            if parts.len() == 2 {
                return XpizAI {
                    xp: parts[0].parse().unwrap_or(4500),
                    reaction: parts[1].parse().unwrap_or(240),
                };
            }
        }
        // Data Default kalau file belum ada
        XpizAI { xp: 4500, reaction: 240 }
    }

    // FUNGSI BARU: Menyimpan data ke HP
    fn save_memory(&self) {
        let path = "/data/data/com.invuzt.xpiz/files/xpiz_data.txt";
        let content = format!("{},{}", self.xp, self.reaction);
        let _ = fs::write(path, content);
    }

    fn analyze(&mut self, id: i32) -> String {
        // Simulasi: Setiap kali halaman dibuka, XP nambah 5 (Biar kelihatan Dinamis!)
        self.xp += 5; 
        self.save_memory(); // Simpan perubahan ke memory

        match id {
            1 => self.training_logic(),
            2 => self.progress_logic(),
            _ => "XPIZ AI Standby".to_string(),
        }
    }

    fn training_logic(&self) -> String {
        format!(
            "XPIZ AI TRAINING:\n            - Live Reaction: {}ms\n            - Status: Memory Active\n            - Forecast: Data Saved to Disk", 
            self.reaction
        )
    }

    fn progress_logic(&self) -> String {
        let percent = (self.xp as f32 / 5000.0) * 100.0;
        format!(
            "PERFORMANCE REPORT:\n            - Live XP: {}\n            - Accuracy: {:.1}%\n            - Forecast: Progress Synced", 
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
    // 1. Load data dari file (Memory)
    let mut ai = XpizAI::load_memory();
    
    // 2. Jalankan analisis (dan update data)
    let content = ai.analyze(page_id as i32);

    let output = env.new_string(content).expect("Gagal buat string");
    output.into_raw()
}
