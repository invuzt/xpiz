use jni::objects::JClass;
use jni::sys::{jint, jstring};
use jni::JNIEnv;

struct XpizAI {
    xp: i32,
    reaction: i32,
}

impl XpizAI {
    fn analyze(&self, id: i32) -> String {
        match id {
            1 => self.training_logic(),
            2 => self.progress_logic(),
            _ => "XPIZ System Active".to_string(),
        }
    }

    fn training_logic(&self) -> String {
        let prediction = self.reaction - 12;
        format!(
            "XPIZ AI ANALYTICS:\n            - Mode: Predictive\n            - Recommendation: Aggressive\n            - Target Speed: {}ms", 
            prediction
        )
    }

    fn progress_logic(&self) -> String {
        let rank = if self.xp > 4000 { "ELITE" } else { "STARTER" };
        let efficiency = (self.xp as f32 / 50.0).min(100.0);
        format!(
            "PERFORMANCE REPORT:\n            - Rank: {}\n            - Efficiency: {:.1}%\n            - Forecast: Level Up Ready", 
            rank, efficiency
        )
    }
}

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(
    mut env: JNIEnv,
    _class: JClass,
    page_id: jint,
) -> jstring {
    // Simulasi data yang nantinya bisa diambil dari storage
    let ai = XpizAI { xp: 4500, reaction: 240 };
    let content = ai.analyze(page_id as i32);

    let output = env.new_string(content).expect("Gagal buat string");
    output.into_raw()
}
