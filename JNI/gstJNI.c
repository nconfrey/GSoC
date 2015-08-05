#include <jni.h>
#include <stdio.h>
#include "gstJNI.h"

JNIEXPORT jint JNICALL Java_gstJNI_pipelineLaunch(JNIEnv *env, jobject thisObj, jstring launch) {
	//First, we need to convert the JNI string to a char*
	const char *inCStr = (*env)->GetStringUTFChars(env, launch, NULL);
	if(inCStr == NULL) return -1; //error check

	printf("In C, the received string is: %s\n", inCStr);
	(*env)->ReleaseStringUTFChars(env, launch, inCStr);

	return 0;
}
