#include <jni.h>
#include <stdio.h>
#include "gstJNI.h"
#include <gst/gst.h>


JNIEXPORT jint JNICALL Java_gstJNI_pipelineLaunch(JNIEnv *env, jobject thisObj, jstring launch) {
    //First, we need to convert the JNI string to a char*
    const char *inCStr = (*env)->GetStringUTFChars(env, launch, NULL);
    if(inCStr == NULL) return -1; //error check
    //printf("In C, the received string is: %s\n", inCStr);

    GstElement *pipeline;
    GstBus *bus;
    GstMessage *msg;
    GError *e;

    e = NULL;
    //Initialize GStreamer
    gst_init (0, NULL); //still not sure what kind of arguments go here

    pipeline = gst_parse_launch(inCStr, &e);
    /* Start playing */
    gst_element_set_state (pipeline, GST_STATE_PLAYING);

    /* Wait until error or EOS */
    bus = gst_element_get_bus (pipeline);
    msg = gst_bus_timed_pop_filtered (bus, GST_CLOCK_TIME_NONE, GST_MESSAGE_ERROR | GST_MESSAGE_EOS);

    /* Free resources */
    if (msg != NULL)
        gst_message_unref (msg);
    gst_object_unref (bus);
    gst_element_set_state (pipeline, GST_STATE_NULL);
    gst_object_unref (pipeline);

    (*env)->ReleaseStringUTFChars(env, launch, inCStr);

    return 0;
}

int main()
{
    return 0;
}
