use std::ffi::CString;
use std::os::raw::c_char;

#[no_mangle]
pub extern "C" fn Java_com_invuzt_xpiz_MainActivity_getContentFromRust(
    _env: *mut std::ffi::c_void,
    _class: *mut std::ffi::c_void,
    page_id: i32,
) -> *mut c_char {
    let content = match page_id {
        1 => "TRAINING (RUST MODE):\n1. Rhythm\n2. Speed",
        2 => "PROGRESS (RUST MODE):\nXP: 1000\nLevel: 10",
        _ => "XPIZ Ready",
    };
    CString::new(content).unwrap().into_raw()
}
