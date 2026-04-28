use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jobject;
use std::ptr;

#[no_mangle]
pub extern "C" fn Java_co_xpiz_engine_MainActivity_renderToCanvas(
    mut env: JNIEnv,
    _class: JClass,
    surface: jobject,
    input: JString,
) {
    if surface.is_null() { return; }
    let text: String = env.get_string(&input).map(|s| s.into()).unwrap_or_default();
    
    // LOGIKA: Di sini nanti kamu bisa panggil fungsi Rust untuk 
    // scraping data website berdasarkan input URL.
    
    let window = unsafe { ndk_sys::ANativeWindow_fromSurface(env.get_native_interface(), surface) };
    if window.is_null() { return; }

    unsafe {
        let mut buffer = ndk_sys::ANativeWindow_Buffer {
            width: 0, height: 0, stride: 0, format: 0, bits: ptr::null_mut(), reserved: [0; 6],
        };
        
        if ndk_sys::ANativeWindow_lock(window, &mut buffer, ptr::null_mut()) == 0 {
            if !buffer.bits.is_null() {
                let total_pixels = (buffer.stride * buffer.height) as usize;
                let pixels = std::slice::from_raw_parts_mut(buffer.bits as *mut u32, total_pixels);

                // Warna berubah sesuai panjang data yang "diambil"
                let color = if text.contains("http") { 0xFF1976D2 } else { 0xFF388E3C };
                for pixel in pixels.iter_mut() { *pixel = color; }
            }
            ndk_sys::ANativeWindow_unlockAndPost(window);
        }
        ndk_sys::ANativeWindow_release(window);
    }
}
