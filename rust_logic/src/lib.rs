use jni::objects::JClass;
use jni::sys::{jint, jstring};
use jni::JNIEnv;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(
    mut env: JNIEnv,
    _class: JClass,
    page_id: jint,
) -> jstring {
    // --- MINI AI ENGINE ---
    let user_xp: i32 = 4500;
    let reaction_speed: i32 = 240; // ms
    
    let ai_response = match page_id {
        1 => {
            // AI di Mode Training
            format!(
                "XPIZ AI ANALYTICS:\n                 - Status: Optimal Performance\n                 - Target: Beat {}ms\n                 - Recommendation: Focus on Speed", 
                reaction_speed - 15
            )
        },
        2 => {
            // AI di Mode Progress
            let rank = if user_xp > 4000 { "ELITE" } else { "ROOKIE" };
            let next_lv = 5000 - user_xp;
            format!(
                "PERFORMANCE DATA:\n                 - Rank: {}\n                 - Power Level: {:.1}\n                 - To Next Level: {} XP", 
                rank, (user_xp as f32 / 100.0), next_lv
            )
        },
        _ => "XPIZ Core Online. System Secure.".to_string(),
    };

    let output = env.new_string(ai_response).expect("Failed to create Java string");
    output.into_raw()
}
