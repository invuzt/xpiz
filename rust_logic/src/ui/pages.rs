pub enum AppPath {
    Training,
    Progress,
    Settings,
}

impl AppPath {
    pub fn from_id(id: i32) -> Self {
        match id {
            1 => AppPath::Training,
            2 => AppPath::Progress,
            _ => AppPath::Settings,
        }
    }

    pub fn get_content(&self) -> &'static str {
        match self {
            AppPath::Training => "START ENGINE\nCHECK STATUS\nUPDATE CORE",
            AppPath::Progress => "CPU: OPTIMAL\nRAM: STABLE\nOS: ANDROID 14",
            AppPath::Settings => "THEME: DARK\nLANGUAGE: RUST\nVERSION: 2.0-MODULAR",
        }
    }
}
