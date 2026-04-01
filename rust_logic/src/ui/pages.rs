pub struct AppPath;

impl AppPath {
    pub fn get_ai_menu(input: &str) -> String {
        let cmd = input.to_lowercase();
        
        if cmd.contains("mesin") || cmd.contains("engine") || cmd.contains("cek") {
            "START ENGINE|ACTION\nSTOP ENGINE|ACTION\nSYSTEM SCAN|ACTION".to_string()
        } else if cmd.contains("gelap") || cmd.contains("tema") || cmd.contains("dark") {
            "THEME: DARK|LABEL\nTOGGLE LIGHT|ACTION\nOLED MODE|ACTION".to_string()
        } else if cmd.contains("zamera") || cmd.contains("foto") || cmd.contains("cam") {
            "CAPTURE|ACTION\nAI ENHANCE|ACTION\nSAVE PATH|INPUT".to_string()
        } else if cmd.contains("metrik") || cmd.contains("status") || cmd.contains("ram") {
            "CPU: 15%|LABEL\nRAM: 2GB|LABEL\nUPTIME: 12H|LABEL".to_string()
        } else {
            // Default Menu jika input kosong atau tidak dikenal
            "SCAN SYSTEM|ACTION\nCONNECT AI|ACTION\nSHOW STATUS|ACTION".to_string()
        }
    }
}
