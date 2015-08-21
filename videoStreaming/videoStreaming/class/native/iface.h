/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class processing_streaming_Streaming */

#ifndef _Included_processing_streaming_Streaming
#define _Included_processing_streaming_Streaming
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     processing_streaming_Streaming
 * Method:    gstreamer_init
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_processing_streaming_Streaming_gstreamer_1init
  (JNIEnv *, jclass);

/*
 * Class:     processing_streaming_Streaming
 * Method:    gstreamer_loadFile
 * Signature: (Ljava/lang/String;Ljava/lang/String;Z)J
 */
JNIEXPORT jlong JNICALL Java_processing_streaming_Streaming_gstreamer_1loadFile
  (JNIEnv *, jobject, jstring, jstring, jboolean);

/*
 * Class:     processing_streaming_Streaming
 * Method:    gstreamer_play
 * Signature: (JZ)V
 */
JNIEXPORT void JNICALL Java_processing_streaming_Streaming_gstreamer_1play
  (JNIEnv *, jobject, jlong, jboolean);

/*
 * Class:     processing_streaming_Streaming
 * Method:    gstreamer_seek
 * Signature: (JF)V
 */
JNIEXPORT void JNICALL Java_processing_streaming_Streaming_gstreamer_1seek
  (JNIEnv *, jobject, jlong, jfloat);

/*
 * Class:     processing_streaming_Streaming
 * Method:    gstreamer_set_loop
 * Signature: (JZ)V
 */
JNIEXPORT void JNICALL Java_processing_streaming_Streaming_gstreamer_1set_1loop
  (JNIEnv *, jobject, jlong, jboolean);

/*
 * Class:     processing_streaming_Streaming
 * Method:    gstreamer_get_duration
 * Signature: (J)F
 */
JNIEXPORT jfloat JNICALL Java_processing_streaming_Streaming_gstreamer_1get_1duration
  (JNIEnv *, jobject, jlong);

/*
 * Class:     processing_streaming_Streaming
 * Method:    gstreamer_get_time
 * Signature: (J)F
 */
JNIEXPORT jfloat JNICALL Java_processing_streaming_Streaming_gstreamer_1get_1time
  (JNIEnv *, jobject, jlong);

/*
 * Class:     processing_streaming_Streaming
 * Method:    gstreamer_get_frame
 * Signature: (J)[B
 */
JNIEXPORT jbyteArray JNICALL Java_processing_streaming_Streaming_gstreamer_1get_1frame
  (JNIEnv *, jobject, jlong);

/*
 * Class:     processing_streaming_Streaming
 * Method:    gstreamer_pipeline_launch
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_processing_streaming_Streaming_gstreamer_1pipeline_1launch
  (JNIEnv *, jclass, jstring);

#ifdef __cplusplus
}
#endif
#endif
