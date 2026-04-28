use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jobject;
use std::ptr;
use rand::Rng;

// Struktur Monster 8-bit
struct Monster {
    x: f32, y: f32, vx: f32, vy: f32,
    energy: f32,
    color: u32,
    is_agressive: bool,
    sprite: [[u8; 5]; 5], // Grid 5x5 untuk bentuk monster
}

struct Food { x: f32, y: f32 }

static mut MONSTERS: Vec<Monster> = Vec::new();
static mut FOODS: Vec<Food> = Vec::new();

// Definisi Sprite Sederhana
const SPRITE_HERBIVORE: [[u8; 5]; 5] = [
    [0,1,1,1,0],
    [1,1,0,1,1],
    [1,1,1,1,1],
    [0,1,1,1,0],
    [1,0,1,0,1],
];

const SPRITE_PREDATOR: [[u8; 5]; 5] = [
    [1,0,1,0,1],
    [0,1,1,1,0],
    [1,1,0,1,1],
    [0,1,1,1,0],
    [1,0,1,0,1],
];

#[no_mangle]
pub extern "C" fn Java_co_xpiz_engine_MainActivity_renderToCanvas(
    mut env: JNIEnv, _class: JClass, surface: jobject, _input: JString,
) {
    if surface.is_null() { return; }
    let window = unsafe { ndk_sys::ANativeWindow_fromSurface(env.get_native_interface(), surface) };
    if window.is_null() { return; }

    unsafe {
        ndk_sys::ANativeWindow_setBuffersGeometry(window, 0, 0, 1);
        let w = ndk_sys::ANativeWindow_getWidth(window) as f32;
        let h = ndk_sys::ANativeWindow_getHeight(window) as f32;
        let mut rng = rand::thread_rng();

        // Init Ecosystem
        if MONSTERS.is_empty() {
            for _ in 0..15 {
                MONSTERS.push(Monster {
                    x: rng.gen_range(20.0..w-20.0), y: rng.gen_range(20.0..h-20.0),
                    vx: rng.gen_range(-4.0..4.0), vy: rng.gen_range(-4.0..4.0),
                    energy: 100.0, color: 0xFF00EEFF,
                    is_agressive: false, sprite: SPRITE_HERBIVORE,
                });
            }
        }

        // Spawn Makanan (Hijau)
        if FOODS.len() < 40 && rng.gen_bool(0.1) {
            FOODS.push(Food { x: rng.gen_range(10.0..w-10.0), y: rng.gen_range(10.0..h-10.0) });
        }

        let mut buffer = ndk_sys::ANativeWindow_Buffer {
            width: 0, height: 0, stride: 0, format: 0, bits: ptr::null_mut(), reserved: [0; 6],
        };

        if ndk_sys::ANativeWindow_lock(window, &mut buffer, ptr::null_mut()) == 0 {
            let stride = buffer.stride as usize;
            let pixels = std::slice::from_raw_parts_mut(buffer.bits as *mut u32, stride * buffer.height as usize);
            pixels.fill(0xFF080808);

            // Draw Foods
            for f in &FOODS {
                let fx = f.x as usize; let fy = f.y as usize;
                if fx < buffer.width as usize && fy < buffer.height as usize {
                    pixels[fy * stride + fx] = 0xFF55FF55;
                }
            }

            let mut offspring = Vec::new();
            MONSTERS.retain_mut(|m| {
                m.energy -= 0.15;
                m.x += m.vx; m.y += m.vy;

                // Pantulan layar
                if m.x <= 10.0 || m.x >= w-10.0 { m.vx *= -1.0; }
                if m.y <= 10.0 || m.y >= h-10.0 { m.vy *= -1.0; }

                // Makan logic
                FOODS.retain(|f| {
                    let d = ((m.x - f.x).powi(2) + (m.y - f.y).powi(2)).sqrt();
                    if d < 15.0 { m.energy += 40.0; return false; }
                    true
                });

                // Evolusi / Beranak
                if m.energy > 200.0 {
                    m.energy = 100.0;
                    let mutant = rng.gen_bool(0.1); // Peluang mutasi jadi predator
                    offspring.push(Monster {
                        x: m.x, y: m.y, vx: -m.vx, vy: -m.vy,
                        energy: 100.0,
                        color: if mutant { 0xFFFF3333 } else { m.color },
                        is_agressive: mutant,
                        sprite: if mutant { SPRITE_PREDATOR } else { SPRITE_HERBIVORE },
                    });
                }

                // Render Sprite 8-Bit (Scale 4x agar besar)
                let scale = 4;
                for row in 0..5 {
                    for col in 0..5 {
                        if m.sprite[row][col] == 1 {
                            let start_x = m.x as i32 + (col as i32 - 2) * scale;
                            let start_y = m.y as i32 + (row as i32 - 2) * scale;
                            
                            for py in 0..scale {
                                for px in 0..scale {
                                    let dx = (start_x + px) as usize;
                                    let dy = (start_y + py) as usize;
                                    if dx < buffer.width as usize && dy < buffer.height as usize {
                                        pixels[dy * stride + dx] = m.color;
                                    }
                                }
                            }
                        }
                    }
                }
                m.energy > 0.0
            });

            MONSTERS.append(&mut offspring);
            ndk_sys::ANativeWindow_unlockAndPost(window);
        }
        ndk_sys::ANativeWindow_release(window);
    }
}
