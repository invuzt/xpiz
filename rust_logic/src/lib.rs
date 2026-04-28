use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jobject;
use std::ptr;
use rand::Rng; // Wajib tambah dependensi rand

// Struktur Partikel
struct Particle {
    x: f32,
    y: f32,
    vx: f32,
    vy: f32,
    color: u32,
}

// State simulasi (global agar persistent antar frame)
static mut PARTICLES: Vec<Particle> = Vec::new();
static PARTICLE_RADIUS: f32 = 4.0;

#[no_mangle]
pub extern "C" fn Java_co_xpiz_engine_MainActivity_renderToCanvas(
    mut env: JNIEnv,
    _class: JClass,
    surface: jobject,
    input: JString,
) {
    if surface.is_null() { return; }
    let count_str: String = env.get_string(&input).map(|s| s.into()).unwrap_or_default();
    // Parse input jadi angka, default 100 jika gagal
    let count = count_str.parse::<usize>().unwrap_or(100).min(5000); // Batasi max 5000 agar tidak lag

    let window = unsafe { ndk_sys::ANativeWindow_fromSurface(env.get_native_interface(), surface) };
    if window.is_null() { return; }

    unsafe {
        ndk_sys::ANativeWindow_setBuffersGeometry(window, 0, 0, 1); // RGBA_8888

        // INISIALISASI: Jika jumlah berubah, buat ulang partikel
        if PARTICLES.len() != count {
            let mut rng = rand::thread_rng();
            let w = ndk_sys::ANativeWindow_getWidth(window) as f32;
            let h = ndk_sys::ANativeWindow_getHeight(window) as f32;
            
            PARTICLES.clear();
            for _ in 0..count {
                PARTICLES.push(Particle {
                    x: rng.gen_range(PARTICLE_RADIUS..(w - PARTICLE_RADIUS)),
                    y: rng.gen_range(PARTICLE_RADIUS..(h - PARTICLE_RADIUS)),
                    vx: rng.gen_range(-5.0..5.0), // Kecepatan acak
                    vy: rng.gen_range(-5.0..5.0),
                    // Warna acak (lebih estetik)
                    color: rng.gen::<u32>() | 0xFF000000, 
                });
            }
        }

        let mut buffer = ndk_sys::ANativeWindow_Buffer {
            width: 0, height: 0, stride: 0, format: 0, bits: ptr::null_mut(), reserved: [0; 6],
        };
        
        if ndk_sys::ANativeWindow_lock(window, &mut buffer, ptr::null_mut()) == 0 {
            if !buffer.bits.is_null() {
                let width = buffer.width as f32;
                let height = buffer.height as f32;
                let stride = buffer.stride as usize;
                let pixels = std::slice::from_raw_parts_mut(buffer.bits as *mut u32, stride * buffer.height as usize);

                // 1. CLEAR SCREEN (Double Buffer) -> Isi background hitam
                pixels.fill(0xFF121212);

                // 2. LOGIKA: Update posisi & Tabrakan Dinding
                for p in PARTICLES.iter_mut() {
                    p.x += p.vx;
                    p.y += p.vy;

                    // Tabrakan Dinding X
                    if p.x <= PARTICLE_RADIUS || p.x >= (width - PARTICLE_RADIUS) {
                        p.vx = -p.vx; // Balikkan arah
                        p.x = p.x.clamp(PARTICLE_RADIUS, width - PARTICLE_RADIUS); // Cegah stuck
                    }
                    // Tabrakan Dinding Y
                    if p.y <= PARTICLE_RADIUS || p.y >= (height - PARTICLE_RADIUS) {
                        p.vy = -p.vy;
                        p.y = p.y.clamp(PARTICLE_RADIUS, height - PARTICLE_RADIUS);
                    }
                    
                    // TABRAKAN PARTIKEL-PARTIKEL (O(N^2) - Berat tapi Rust kuat)
                    // Nanti bisa dioptimasi dengan spatial hashing, tapi buat 1000 Rust kuat.

                    // 3. GAMBAR: Representasi partikel sebagai pixel tunggal (agar cepat)
                    let px = p.x as usize;
                    let py = p.y as usize;
                    // Pastikan koordinat dalam batas buffer
                    if px < buffer.width as usize && py < buffer.height as usize {
                        pixels[py * stride + px] = p.color;
                    }
                }
            }
            ndk_sys::ANativeWindow_unlockAndPost(window);
        }
        ndk_sys::ANativeWindow_release(window);
    }
}
