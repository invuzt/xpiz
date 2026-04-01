use std::ffi::CString;
use std::os::raw::{c_char, c_int};

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(
    _env: *mut std::ffi::c_void,
    _class: *mut std::ffi::c_void,
    page_id: c_int,
) -> *mut c_char {
    let content = match page_id {
        1 => "TRAINING (RUST ENGINE):\n\n1. Rhythm Match\n2. Speed Test\n3. Sequence Rush",
        2 => "PROGRESS (RUST ENGINE):\n\nLevel: 71\nTotal XP: 4.500\nRank: Gold",
        _ => "XPIZ System Ready",
    };
    
    CString::new(content).unwrap().into_raw()
}
