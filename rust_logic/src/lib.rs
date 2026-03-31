use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;
use std::io::{BufRead, BufReader};
use std::fs::File;
use std::time::Instant;

#[no_mangle]
pub extern "system" fn Java_com_invuzt_xpiz_MainActivity_processHugeFile(
    mut env: JNIEnv,
    _class: JClass,
    path: JString,
) -> jstring {
    let input: String = env.get_string(&path).expect("Path invalid").into();
    let start = Instant::now();

    let file = match File::open(&input) {
        Ok(f) => f,
        Err(_) => {
            let res = env.new_string("File data.txt belum ada di folder files!").unwrap();
            return res.into_raw();
        }
    };
    
    let reader = BufReader::new(file);
    let mut count: u64 = 0;
    let mut total_sum: f64 = 0.0;

    for line in reader.lines() {
        if let Ok(num_str) = line {
            if let Ok(num) = num_str.parse::<f64>() {
                total_sum += num;
                count += 1;
            }
        }
    }

    let duration = start.elapsed();
    let hasil = format!(
        "🚀 Rust Power!\nData: {} baris\nWaktu: {:.2?}\nTotal: {:.2}",
        count, duration, total_sum
    );

    let output = env.new_string(hasil).expect("Gagal buat string");
    output.into_raw()
}
