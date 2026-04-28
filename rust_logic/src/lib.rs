use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jobject;
use std::ptr;
use rand::Rng;

struct Organism {
    x: f32, y: f32, vx: f32, vy: f32,
    energy: f32,
    size: f32,
    color: u32,
    species_type: u8, // 0: Damai, 1: Agresif
}

struct Food { x: f32, y: f32 }

static mut CREATURES: Vec<Organism> = Vec::new();
static mut FOOD_POOL: Vec<Food> = Vec::new();

#[no_mangle]
pub extern "C" fn Java_co_xpiz_engine_MainActivity_renderToCanvas(
    mut env: JNIEnv,
    _class: JClass,
    surface: jobject,
    input: JString,
) {
    if surface.is_null() { return; }
    let window = unsafe { ndk_sys::ANativeWindow_fromSurface(env.get_native_interface(), surface) };
    if window.is_null() { return; }

    unsafe {
        ndk_sys::ANativeWindow_setBuffersGeometry(window, 0, 0, 1);
        let w = ndk_sys::ANativeWindow_getWidth(window) as f32;
        let h = ndk_sys::ANativeWindow_getHeight(window) as f32;
        let mut rng = rand::thread_rng();

        // INISIALISASI AWAL
        if CREATURES.is_empty() {
            for _ in 0..20 {
                CREATURES.push(Organism {
                    x: rng.gen_range(10.0..w-10.0), y: rng.gen_range(10.0..h-10.0),
                    vx: rng.gen_range(-3.0..3.0), vy: rng.gen_range(-3.0..3.0),
                    energy: 100.0, size: 8.0,
                    color: 0xFF00BFFF, // Biru (Damai)
                    species_type: 0,
                });
            }
        }

        // TAMBAH MAKANAN SECARA ACAK (Partikel Hijau)
        if FOOD_POOL.len() < 30 && rng.gen_bool(0.1) {
            FOOD_POOL.push(Food { x: rng.gen_range(10.0..w-10.0), y: rng.gen_range(10.0..h-10.0) });
        }

        let mut buffer = ndk_sys::ANativeWindow_Buffer {
            width: 0, height: 0, stride: 0, format: 0, bits: ptr::null_mut(), reserved: [0; 6],
        };
        
        if ndk_sys::ANativeWindow_lock(window, &mut buffer, ptr::null_mut()) == 0 {
            let stride = buffer.stride as usize;
            let pixels = std::slice::from_raw_parts_mut(buffer.bits as *mut u32, stride * buffer.height as usize);
            pixels.fill(0xFF050505); // Background hampir hitam

            // 1. DRAW FOOD (Kecil & Hijau)
            for f in &FOOD_POOL {
                let fx = f.x as usize; let fy = f.y as usize;
                if fx < buffer.width as usize && fy < buffer.height as usize {
                    pixels[fy * stride + fx] = 0xFF00FF00;
                }
            }

            // 2. LOGIKA MAKHLUK HIDUP
            let mut offspring = Vec::new();
            CREATURES.retain_mut(|c| {
                c.energy -= 0.2; // Energi berkurang seiring waktu
                c.x += c.vx; c.y += c.vy;

                // Pantulan Dinding
                if c.x <= 0.0 || c.x >= w { c.vx *= -1.0; }
                if c.y <= 0.0 || c.y >= h { c.vy *= -1.0; }

                // MAKAN: Cek jarak ke makanan
                FOOD_POOL.retain(|f| {
                    let dist = ((c.x - f.x).powi(2) + (c.y - f.y).powi(2)).sqrt();
                    if dist < c.size {
                        c.energy += 30.0;
                        c.size += 0.5;
                        return false;
                    }
                    true
                });

                // BERANAK: Jika energi > 180
                if c.energy > 180.0 {
                    c.energy = 90.0;
                    c.size -= 2.0;
                    offspring.push(Organism {
                        x: c.x, y: c.y, vx: -c.vx, vy: -c.vy,
                        energy: 80.0, size: 8.0,
                        color: if rng.gen_bool(0.05) { 0xFFFF4500 } else { c.color }, // Mutasi warna
                        species_type: if rng.gen_bool(0.05) { 1 } else { c.species_type },
                    });
                }

                // GAMBAR MAKHLUK (Bentuk Random: Kotak/Bulat sesuai Species)
                let sz = c.size as i32;
                for dy in -sz..sz {
                    for dx in -sz..sz {
                        let is_inside = if c.species_type == 0 {
                            dx*dx + dy*dy <= sz*sz // Bulat
                        } else {
                            true // Kotak (Mutan Agresif)
                        };

                        if is_inside {
                            let draw_x = (c.x as i32 + dx) as usize;
                            let draw_y = (c.y as i32 + dy) as usize;
                            if draw_x < buffer.width as usize && draw_y < buffer.height as usize {
                                pixels[draw_y * stride + draw_x] = c.color;
                            }
                        }
                    }
                }
                c.energy > 0.0 // Jika energi 0, mati (dihapus dari Vec)
            });

            CREATURES.append(&mut offspring);
            ndk_sys::ANativeWindow_unlockAndPost(window);
        }
        ndk_sys::ANativeWindow_release(window);
    }
}
