use std::ffi::CString;
use std::os::raw::c_char;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(
    _env: *mut std::ffi::c_void,
    _class: *mut std::ffi::c_void,
    page_id: i32,
) -> *mut c_char {
    let content = match page_id {
        1 => "LIST TRAINING DARI RUST:\n\n1. Finger Speed Test\n2. Rhythm Accuracy\n3. Memory Sequence",
        2 => "STATISTIK PROGRESS (RUST ENGINE):\n\nTotal XP: 12.540\nRank: Gold Master\nAccuracy: 98.5%",
        _ => "XPIZ Ready!",
    };
    
    CString::new(content).unwrap().into_raw()
}
