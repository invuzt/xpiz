use serde::{Serialize, Deserialize};
use std::collections::HashMap;

#[derive(Serialize, Deserialize, Default, Debug)]
pub struct XpizBrain {
    pub weights: HashMap<String, HashMap<String, i32>>, // Label -> (Kata -> Skor)
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
