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
    
    // Ambil window
    let window = unsafe { ndk_sys::ANativeWindow_fromSurface(env.get_native_interface(), surface) };
    if window.is_null() { return; }

    unsafe {
        // Set buffer format secara eksplisit ke RGBA_8888 agar sinkron dengan u32 di Rust
        ndk_sys::ANativeWindow_setBuffersGeometry(window, 0, 0, 1); // 1 = WINDOW_FORMAT_RGBA_8888

        let mut buffer = ndk_sys::ANativeWindow_Buffer {
            width: 0, height: 0, stride: 0, format: 0, bits: ptr::null_mut(), reserved: [0; 6],
        };
        
        if ndk_sys::ANativeWindow_lock(window, &mut buffer, ptr::null_mut()) == 0 {
            if !buffer.bits.is_null() {
                // Gunakan stride untuk keamanan. Stride adalah lebar baris dalam pixel termasuk padding.
                let height = buffer.height as usize;
                let stride = buffer.stride as usize;
                let pixels = std::slice::from_raw_parts_mut(buffer.bits as *mut u32, stride * height);

                let color = if text.len() % 2 == 0 { 0xFF2E7D32 } else { 0xFFC62828 };

                // Isi memori dengan sangat hati-hati baris per baris
                for y in 0..height {
                    for x in 0..buffer.width as usize {
                        pixels[y * stride + x] = color;
                    }
                }
            }
            ndk_sys::ANativeWindow_unlockAndPost(window);
        }
        ndk_sys::ANativeWindow_release(window);
    }
}
