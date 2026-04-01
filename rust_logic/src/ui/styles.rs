pub struct ColorStack {
    pub bg: &'static str,
    pub text: &'static str,
}

pub const AKTIF: ColorStack = ColorStack { bg: "#D0C9FF", text: "#000000" };
pub const PASIF: ColorStack = ColorStack { bg: "#1A1A1A", text: "#888888" };
pub const GELAP: &str = "#081512";
pub const PUTIH: &str = "#FFFFFF";

pub fn get_nav_style(is_active: bool) -> String {
    let s = if is_active { AKTIF } else { PASIF };
    format!("{}|{}", s.bg, s.text)
}
