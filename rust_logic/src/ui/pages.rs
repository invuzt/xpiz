pub enum AppPath {
    Training,
    Progress,
    Settings, // Akses via Header
}

impl AppPath {
    pub fn from_id(id: i32) -> Self {
        match id {
            1 => AppPath::Training,
            2 => AppPath::Progress,
            99 => AppPath::Settings, // ID khusus untuk Settings
            _ => AppPath::Training,
        }
    }

    pub fn get_content(&self) -> &'static str {
        match self {
            AppPath::Training => "START ENGINE|INPUT\nCHECK STATUS|LABEL\nUPDATE CORE|INPUT",
            AppPath::Progress => "CPU: 12%|LABEL\nRAM: 1.2GB|LABEL\nBACK TO MENU|ACTION",
            AppPath::Settings => "THEME: DARK|ACTION\nUSER: ADMIN|INPUT\nVERSION: 3.0|LABEL",
        }
    }
}
