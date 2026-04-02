// Kita tidak butuh import NaiveBayes lagi karena logikanya 
// sudah pindah ke crate xpiz_brain
pub struct AppPath;

impl AppPath {
    pub fn from_id(_id: i32) -> Self { 
        AppPath 
    }
    
    pub fn get_content(&self) -> String {
        "".to_string()
    }
}
