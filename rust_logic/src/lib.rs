use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jobject;
use std::ptr;

#[no_mangle]
pub extern "C" fn Java_co_xpiz_MainActivity_renderToCanvas(
    mut env: JNIEnv,
    _class: JClass,
    surface: jobject,
    input: JString,
) {
    let text: String = env.get_string(&input).map(|s| s.into()).unwrap_or_default();
    
    // Ambil NativeWindow dari Surface Java menggunakan ndk_sys
    let window = unsafe { 
        ndk_sys::ANativeWindow_fromSurface(env.get_native_interface(), surface) 
    };
    
    if !window.is_null() {
        unsafe {
            let mut buffer = ndk_sys::ANativeWindow_Buffer {
                width: 0,
                height: 0,
                stride: 0,
                format: 0,
                bits: ptr::null_mut(),
                reserved: [0; 6],
            };
            
            // Lock buffer layar
            if ndk_sys::ANativeWindow_lock(window, &mut buffer, ptr::null_mut()) == 0 {
                let pixels = std::slice::from_raw_parts_mut(
                    buffer.bits as *mut u32,
                    (buffer.stride * buffer.height) as usize
                );

                // Logika warna: Biru kalau ada teks, Hitam kalau kosong
                let color = if text.trim().is_empty() {
                    0xFF000000 // Black
                } else if text.len() % 2 == 0 {
                    0xFF2E7D32 // Green (ARGB)
                } else {
                    0xFFC62828 // Red (ARGB)
                };

                // Rust menggambar langsung ke pixel buffer
                for pixel in pixels.iter_mut() {
                    *pixel = color;
                }

                ndk_sys::ANativeWindow_unlockAndPost(window);
            }
            ndk_sys::ANativeWindow_release(window);
        }
    }
}
