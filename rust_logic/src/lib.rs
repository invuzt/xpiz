use jni::JNIEnv;
use jni::objects::{JClass, JString};
use ndk::native_window::NativeWindow;
use jni::sys::jobject;

#[no_mangle]
pub extern "C" fn Java_co_xpiz_MainActivity_renderToCanvas(
    mut env: JNIEnv,
    _class: JClass,
    surface: jobject,
    input: JString,
) {
    // 1. Ambil teks dari Java
    let text: String = env.get_string(&input).map(|s| s.into()).unwrap_or_default();
    
    // 2. Ambil NativeWindow dari Surface Java
    let window = unsafe { 
        ndk_sys::ANativeWindow_fromSurface(env.get_native_interface(), surface) 
    };
    
    if !window.is_null() {
        unsafe {
            let mut buffer = ndk_sys::ANativeWindow_Buffer {
                width: 0, height: 0, stride: 0, format: 0, bits: std::ptr::null_mut(), reserved: [0; 6],
            };
            
            // Lock buffer untuk menggambar
            if ndk_sys::ANativeWindow_lock(window, &mut buffer, std::ptr::null_mut()) == 0 {
                let pixels = std::slice::from_raw_parts_mut(
                    buffer.bits as *mut u32,
                    (buffer.stride * buffer.height) as usize
                );

                // Buat warna berdasarkan panjang teks (simulasi proses)
                let color = if text.len() % 2 == 0 { 0xFF00FF00 } else { 0xFFFF0000 }; // Hijau atau Merah

                // Gambar latar belakang (Pixel by pixel di Rust)
                for pixel in pixels.iter_mut() {
                    *pixel = color;
                }

                ndk_sys::ANativeWindow_unlockAndPost(window);
            }
            ndk_sys::ANativeWindow_release(window);
        }
    }
}
