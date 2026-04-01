use serde::{Serialize, Deserialize};
use std::collections::HashMap;

// Pastikan ada 'pub' di depan struct dan field-nya
#[derive(Serialize, Deserialize, Default, Debug)]
pub struct Brain {
    pub memory: HashMap<String, i32>,
}

impl Brain {
    pub fn learn(&mut self, input: &str) {
        for word in input.split_whitespace() {
            let word_lc = word.to_lowercase();
            if word_lc.len() > 2 {
                let count = self.memory.entry(word_lc).or_insert(0);
                *count += 1;
            }
        }
    }

    pub fn get_dynamic_menu(&self, input: &str) -> String {
        let cmd = input.to_lowercase();
        
        if cmd.contains("mesin") || self.memory.get("mesin").unwrap_or(&0) > &5 {
            "ENGINE: ACTIVE|LABEL\nDIAGNOSTIC|ACTION\nSTOP ENGINE|ACTION".to_string()
        } else if cmd.contains("gelap") || self.memory.get("gelap").unwrap_or(&0) > &3 {
            "OLED THEME|ACTION\nBRIGHTNESS: 10%|LABEL".to_string()
        } else {
            "AI SYNC|ACTION\nCHECK STATUS|ACTION\nEXPLORE|ACTION".to_string()
        }
    }
}

// Tambahkan struct AppPath jika lib.rs membutuhkannya
pub struct AppPath;
impl AppPath {
    pub fn from_id(id: i32) -> Self { AppPath }
    pub fn get_content(&self) -> String { "".to_string() }
}
