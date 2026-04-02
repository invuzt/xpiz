use serde::{Serialize, Deserialize};
use naive_bayes::NaiveBayes;

#[derive(Serialize, Deserialize, Default)]
pub struct Brain {
    // Model AI: Memetakan teks ke label (misal: "engine", "camera", "system")
    pub model: NaiveBayes<String>,
    pub total_trains: usize,
}

impl Brain {
    pub fn learn(&mut self, input: &str, label: &str) {
        // AI Belajar: "Kalimat ini" artinya "Menu ini"
        self.model.train(input.to_string(), label.to_string());
        self.total_trains += 1;
    }

    pub fn predict_menu(&self, input: &str) -> String {
        if self.total_trains < 2 {
            return "SCAN SYSTEM|ACTION\nCONNECT AI|ACTION\nWAITING FOR DATA|LABEL".to_string();
        }

        // Prediksi label terbaik berdasarkan input user
        let prediction = self.model.predict(input.to_string());
        
        match prediction.as_deref() {
            Some("engine") => "ENGINE: ACTIVE|LABEL\nDIAGNOSTIC|ACTION\nSTOP ENGINE|ACTION".to_string(),
            Some("camera") => "OPEN ZAMERA|ACTION\nAI FILTERS|ACTION\nSTORAGE FULL|LABEL".to_string(),
            Some("system") => "CPU: 12%|LABEL\nRAM: 1.5GB|LABEL\nREBOOT|ACTION".to_string(),
            _ => "UNKNOWN CMD|LABEL\nKEEP TRAINING|ACTION".to_string(),
        }
    }
}

pub struct AppPath;
impl AppPath {
    pub fn from_id(_id: i32) -> Self { AppPath }
}
