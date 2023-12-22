// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("surfaceproject");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("surfaceproject")
//      }
//    }

#include "android/native_window.h"
#include "android/native_window_jni.h"
#include "string.h"
#include <jni.h>


extern "C"
JNIEXPORT void JNICALL
Java_com_example_surfaceproject_window_NativeWindow_write(JNIEnv *env, jobject thiz, jobject surface) {
    ANativeWindow *window = ANativeWindow_fromSurface(env, surface);
    ANativeWindow_setBuffersGeometry(window, 200, 200, WINDOW_FORMAT_RGBA_8888);
    ANativeWindow_Buffer buffer;
    ANativeWindow_lock(window, &buffer, 0);
    uint8_t * area = static_cast<uint8_t *>(buffer.bits);
    //memcpy(area, src, count);
}