use serde::{Serialize, Deserialize};
use std::collections::HashMap;

#[derive(Serialize, Deserialize, Default, Debug)]
pub struct XpizBrain {
    pub weights: HashMap<String, HashMap<String, i32>>,
    pub actions: HashMap<String, Vec<String>>, // Simpan tombol per label
}

impl XpizBrain {
    pub fn learn(&mut self, text: &str, label: &str) {
        let entry = self.weights.entry(label.to_string()).or_insert(HashMap::new());
        for word in text.split_whitespace() {
            let word_lc = word.to_lowercase();
            if word_lc.len() > 2 {
                let count = entry.entry(word_lc).or_insert(0);
                *count += 1;
            }
        }
        // Inisialisasi daftar aksi jika belum ada
        self.actions.entry(label.to_string()).or_insert(Vec::new());
    }

    pub fn add_action(&mut self, label: &str, action_name: &str) {
        let list = self.actions.entry(label.to_string()).or_insert(Vec::new());
        let full_action = format!("{}|ACTION", action_name);
        if !list.contains(&full_action) {
            list.push(full_action);
        }
    }

    pub fn predict(&self, text: &str) -> String {
        let mut best_label = "unknown".to_string();
        let mut max_score = -1;
        for (label, words_map) in &self.weights {
            let mut current_score = 0;
            for word in text.split_whitespace() {
                current_score += words_map.get(&word.to_lowercase()).unwrap_or(&0);
            }
            if current_score > max_score && current_score > 0 {
                max_score = current_score;
                best_label = label.clone();
            }
        }
        best_label
    }
}
