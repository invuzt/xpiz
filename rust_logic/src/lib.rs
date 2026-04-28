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
    
    let window = unsafe { 
        ndk_sys::ANativeWindow_fromSurface(env.get_native_interface(), surface) 
    };
    
    if window.is_null() { return; }

    unsafe {
        let mut buffer = ndk_sys::ANativeWindow_Buffer {
            width: 0, height: 0, stride: 0, format: 0, bits: ptr::null_mut(), reserved: [0; 6],
        };
        
        // Lock buffer dengan pengaman
        if ndk_sys::ANativeWindow_lock(window, &mut buffer, ptr::null_mut()) == 0 {
            if !buffer.bits.is_null() {
                // Gunakan stride untuk menghitung kapasitas sebenarnya agar tidak overflow
                let total_pixels = (buffer.stride * buffer.height) as usize;
                let pixels = std::slice::from_raw_parts_mut(buffer.bits as *mut u32, total_pixels);

                let color = if text.is_empty() { 
                    0xFF121212 
                } else if text.len() % 2 == 0 { 
                    0xFF2E7D32 
                } else { 
                    0xFFC62828 
                };

                // Menggunakan iterasi cepat
                for pixel in pixels.iter_mut() {
                    *pixel = color;
                }
            }
            ndk_sys::ANativeWindow_unlockAndPost(window);
        }
        // Wajib dilepas agar tidak Force Close setelah beberapa kali klik
        ndk_sys::ANativeWindow_release(window);
    }
}
