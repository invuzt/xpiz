use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;
use petgraph::graph::DiGraph;
use std::sync::Mutex;

// Struktur Data Node Odfiz
struct Node {
    name: String,
    val: i32,
}

lazy_static::lazy_static! {
    static ref POS_GRAPH: Mutex<DiGraph<Node, i32>> = Mutex::new(DiGraph::new());
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_invuzt_logic_CanvasActivity_connectNodes(
    mut env: JNIEnv,
    _class: JClass,
    from_id: jstring,
    to_id: jstring,
) -> jstring {
    // Di sini Rust nyatet: Node A nyambung ke Node B
    // Logika graph ditaruh di sini agar eksekusi harga "ngalir" lewat kabel
    let msg = "Kabel Terpasang!";
    env.new_string(msg).expect("Err").into_raw()
}
