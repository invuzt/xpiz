pub enum AppPath {
    Dynamic(String), // Konten ditentukan oleh input AI
}

impl AppPath {
    pub fn get_ai_menu(input: &str) -> String {
        let input_lc = input.to_lowercase();
        
        // Logika "Otak" AI Sederhana
        if input_lc.contains("engine") || input_lc.contains("mesin") {
            "START ENGINE|ACTION\nSTOP ENGINE|ACTION\nBACK|GOTO:1".to_string()
        } else if input_lc.contains("zamera") || input_lc.contains("foto") {
            "OPEN CAMERA|ACTION\nFLASH MODE|ACTION\nBACK|GOTO:1".to_string()
        } else if input_lc.contains("status") {
            "CPU OPTIMAL|LABEL\nRAM STABLE|LABEL\nBACK|GOTO:1".to_string()
        } else {
            // Default Menu jika AI tidak mengenali perintah
            "TRAINING CORE\nSYSTEM CHECK\nAI SYNC".to_string()
        }
    }
}
