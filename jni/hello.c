#include <jni.h>

extern jstring Java_com_invuzt_xpiz_MainActivity_analyzeFrame(
    JNIEnv *env, jobject thiz, jbyteArray data, jint w, jint h);

JNIEXPORT jstring JNICALL
Java_com_invuzt_xpiz_MainActivity_analyzeFrame(JNIEnv *env, jobject thiz, jbyteArray data, jint w, jint h) {
    return Java_com_invuzt_xpiz_MainActivity_analyzeFrame(env, thiz, data, w, h);
}
