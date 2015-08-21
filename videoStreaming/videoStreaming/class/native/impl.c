#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include <gst/gst.h>
#include <gst/app/app.h>
#include "impl.h"
#include "iface.h"

GThread *thread;
GMainLoop *loop;
gboolean live = FALSE;

#define MAX_VIDEOS 10
video videos[MAX_VIDEOS];

JNIEXPORT void JNICALL Java_processing_streaming_Streaming_gstreamer_1pipeline_1launch(JNIEnv *env, jobject thisObj, jstring launch)
{
	//First, we need to convert the JNI string to a char*
    const char *inCStr = (*env)->GetStringUTFChars(env, launch, NULL);
    //if(inCStr == NULL) return -1; //error check
    //printf("In C, the received string is: %s\n", inCStr);

    GstElement *pipeline;
    GstBus *bus;
    GstMessage *msg;
    GError *e;

    e = NULL;
    //Initialize GStreamer
    gst_init (0, NULL);

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
}

static void* streaming_mainloop(void *data) {
	loop = g_main_loop_new(NULL, FALSE);
	g_main_loop_run(loop);
}

JNIEXPORT jboolean JNICALL Java_processing_streaming_Streaming_gstreamer_1init (JNIEnv *env, jclass cls)
{
	GError *err = NULL;

	gst_init_check(NULL, NULL, &err);
	if (err != NULL) {
		g_print("Could not initialize library: %s\n", err->message);
		g_error_free(err);
		return FALSE;
	}
	
	thread = g_thread_new("streaming-mainloop", streaming_mainloop, NULL);

	return TRUE;
}

//Handles messages from the bus on the pipeline
static gboolean streaming_bus_callback(GstBus *bus, GstMessage *message, gpointer data)
{
	g_print("Got %s message\n", GST_MESSAGE_TYPE_NAME (message));

	switch (GST_MESSAGE_TYPE (message)) {
		case GST_MESSAGE_ERROR: {
			GError *err;
			gchar *debug;
			gst_message_parse_error(message, &err, &debug);
			g_print("Error: %s\n", err->message);
			g_error_free(err);
			g_free(debug);
			g_main_loop_quit(loop);
			break;
		}
		case GST_MESSAGE_EOS: {
			video *v = NULL;
			for (int i=0; i < MAX_VIDEOS; i++) {
				if (bus == videos[i].bus) {
					v = &videos[i];
					break;
				}
			}
			if (v && v->loop) {
				gst_element_seek(v->play, 1.0, GST_FORMAT_TIME, 
					GST_SEEK_FLAG_FLUSH, GST_SEEK_TYPE_SET, 0,
					GST_SEEK_TYPE_NONE, GST_CLOCK_TIME_NONE);
			}
			//g_main_loop_quit(loop);
			break;
		}
		case GST_MESSAGE_CLOCK_LOST:
      		/* Get a new clock */
			g_print("Need to get a new clock\n");
      		//gst_element_set_state (data->pipeline, GST_STATE_PAUSED);
      		//gst_element_set_state (data->pipeline, GST_STATE_PLAYING);
      	case GST_MESSAGE_STREAM_STATUS:{
      		GstStreamStatusType type;
      		GstElement *owner;
      		gst_message_parse_stream_status(message, &type, &owner);
      		switch(type)
      		{
      			case GST_STREAM_STATUS_TYPE_CREATE:
      				g_print("New Thread needs to be created");
      				break;
      			case GST_STREAM_STATUS_TYPE_START:
      				g_print("A new thread started");
      				break;
      			default:
      				g_print("idk man i'm tired");
      		}      
      	}
      	case GST_MESSAGE_STATE_CHANGED:{
      		GstState old_state, new_state;
      		gst_message_parse_state_changed (message, &old_state, &new_state, NULL);
    		g_print ("Element %s changed state from %s to %s.\n",
        	GST_OBJECT_NAME (message->src),
       		gst_element_state_get_name (old_state),
        	gst_element_state_get_name (new_state));
    		break;
		}
		default:
			break;
	}

	return TRUE;
}

static video* new_video()
{
	for (int i=0; i < MAX_VIDEOS; i++) {
		if (videos[i].obj == NULL) {
			return &videos[i];
		}
	}
	return NULL;
}

static video* get_video(long handle)
{
	for (int i=0; i < MAX_VIDEOS; i++) {
		if ((long)&videos[i] == handle) {
			return &videos[i];
		}
	}
	return NULL;
}

// http://stackoverflow.com/questions/28040857/gstreamer-write-appsink-to-filesink
// http://stackoverflow.com/questions/24142381/probleme-with-the-pull-sample-signal-using-appsink
// http://gstreamer.freedesktop.org/data/doc/gstreamer/head/gst-plugins-base-libs/html/gst-plugins-base-libs-appsink.html

//http://docs.gstreamer.com/display/GstSDK/Basic+tutorial+8%3A+Short-cutting+the+pipeline
static GstFlowReturn appsink_new_sample(GstAppSink *sink, gpointer user_data) {
//   prog_data* pd = (prog_data*)user_data;

  GstSample* sample = gst_app_sink_pull_sample(sink);
  
  if(sample == NULL) {
  	g_print("failed to read data!\n");
    return GST_FLOW_ERROR;
  }

  GstBuffer* buffer = gst_sample_get_buffer(sample);

  GstMemory* memory = gst_buffer_get_all_memory(buffer);
  GstMapInfo* map_info = malloc(sizeof(GstMapInfo));

  if(! gst_memory_map(memory, map_info, GST_MAP_READ)) {
    gst_memory_unref(memory);
    gst_sample_unref(sample);
    return GST_FLOW_ERROR;
  }

  video *v = (video*)user_data;
  if (v->buf[1] != NULL) {
    // free previous sample
    GstMapInfo *old_map_info = v->buf[1];
    GstMemory *old_memory = old_map_info->memory;
    gst_memory_unmap(old_memory, old_map_info);
    gst_memory_unref(old_memory);
    free(old_map_info);
    v->buf[1] = NULL;
  }
  // LOCK
  v->buf[1] = v->buf[0];
  v->buf[0] = map_info;
  // UNLOCK

  //gst_memory_unmap(memory, &map_info);
  //gst_memory_unref(memory);
  gst_sample_unref(sample);

  return GST_FLOW_OK;
}


// Some general JNI references:
// http://docs.oracle.com/javase/7/docs/technotes/guides/jni/
// http://www.math.uni-hamburg.de/doc/java/tutorial/native1.1/implementing/method.html
// http://www.ibm.com/developerworks/library/j-jni/

// Idea: Use gst-gl element?
// https://coaxion.net/blog/2014/04/opengl-support-in-gstreamer/
// http://cgit.freedesktop.org/gstreamer/gst-plugins-bad/tree/tests/examples/gl/generic/doublecube/main.cpp

//Frames are stored in the video struct, just need to retrieve the previously rendered frames
JNIEXPORT jbyteArray JNICALL Java_processing_streaming_Streaming_gstreamer_1get_1frame
  (JNIEnv *env, jobject obj, jlong handle)
{
  video *v = get_video(handle);
  if (v == NULL) {
    return 0L;
  }

  if (v->buf[0] == NULL) {
    return 0L;
  }

  // LOCK
  jbyteArray ret = (*env)->NewByteArray(env, v->buf[0]->size);
  (*env)->SetByteArrayRegion(env, ret, 0, v->buf[0]->size, (const jbyte*)v->buf[0]->data);
  // UNLOCK
  return ret;
}

void setupAppsink(video *v)
{
  // get sink
  // set http://gstreamer.freedesktop.org/data/doc/gstreamer/head/gst-plugins-base-libs/html/gst-plugins-base-libs-appsink.html
  // XXX: use gst_bin_get_by_interface
  v->sink = gst_bin_get_by_name(GST_BIN (v->play), "sink");
  gst_app_sink_set_max_buffers(GST_APP_SINK(v->sink), 2); // limit number of buffers queued
  gst_app_sink_set_drop(GST_APP_SINK(v->sink), TRUE );    // drop old buffers in queue when full

  // setup callbacks (faster than signals)
  GstAppSinkCallbacks* appsink_callbacks = (GstAppSinkCallbacks*)malloc(sizeof(GstAppSinkCallbacks));
  appsink_callbacks->eos = NULL;
  appsink_callbacks->new_preroll = NULL;
  appsink_callbacks->new_sample = appsink_new_sample;
  gst_app_sink_set_callbacks(GST_APP_SINK(v->sink), appsink_callbacks, v, NULL);
  free(appsink_callbacks);
}

JNIEXPORT jlong JNICALL Java_processing_streaming_Streaming_gstreamer_1loadFile(JNIEnv *env, jobject obj, jstring _fn, jstring _pipeline, jboolean live)
{
    GError *error = NULL;

	video *v = new_video();
	if (v == NULL) {
		return 0L;
	}

	//This encodes the file name are a uri - do we need it?
	//What about my streaming sources, probably shouldn't have this?

	//Only need to resolve URI for non-live sources
	gchar *uri;
	if(live == JNI_FALSE){
		const char *fn = (*env)->GetStringUTFChars(env, _fn, JNI_FALSE);
		if (strstr(fn, "://") == NULL) {
			uri = gst_filename_to_uri(fn, NULL);
		} else {
			uri = g_strdup(fn);
		}
		(*env)->ReleaseStringUTFChars(env, _fn, fn);
	}
	
    /* create a new pipeline */
    
    //gchar *netpipe = g_strdup_printf ("filesrc location=%s ! decodebin name=dec ! queue ! videoconvert ! x264enc pass=qual quantizer=20 tune=zerolatency ! rtph264pay ! udpsink host=127.0.0.1 port=5555 sync=true dec. ! queue ! audioconvert ! audioresample ! autoaudiosink", fn);
    //This is the original, which works. Going to try to hijack it, and play another video on a loop to capture the same things
    //gchar *descr = g_strdup_printf ("uridecodebin uri=%s ! videoconvert ! videoscale ! appsink name=sink caps=\"" CAPS "\"", uri);    
    //gchar *descr = g_strdup_printf ("udpsrc port=5555 caps=\"application/x-rtp, payload=127\" ! rtph264depay ! avdec_h264 ! videoconvert ! videoscale ! appsink name=sink caps=\"" CAPS "\"");                                       
    
	const char *pipeline = (*env)->GetStringUTFChars(env, _pipeline, JNI_FALSE);
	gchar *descr;
	if(live == JNI_FALSE)
  		descr = g_strdup_printf(pipeline, uri);
  	else
  		descr = g_strdup(pipeline);
    g_free(uri);

    //Free Strings
    (*env)->ReleaseStringUTFChars(env, _pipeline, pipeline);
    
    
    v->play = gst_parse_launch (descr, &error);

    if (error != NULL) {
      g_print ("could not construct pipeline: %s\n", error->message);
      g_error_free (error);
      exit (-1);
    }

    // setup appsink if the pipeline  is using it
  	if (strstr(descr, "appsink")) {
    	setupAppsink(v);
  	}
  	g_free(descr);

   	//enables bus message callbacks
	v->bus = gst_pipeline_get_bus(GST_PIPELINE (v->play));
	gst_bus_add_watch(v->bus, streaming_bus_callback, loop);
	
	//NC: Why was this here? it frees it right after being created...
	//gst_object_unref(v->bus);
	
	v->obj = obj;

	// XXX: vs jlong?
	return (long)v;
}

JNIEXPORT void JNICALL Java_processing_streaming_Streaming_gstreamer_1play(JNIEnv *env, jobject obj, jlong handle, jboolean _play)
{
	video *v = get_video(handle);
	if (v == NULL) {
		return;
	}

	if (_play == JNI_TRUE) {
		gst_element_set_state (v->play, GST_STATE_PLAYING);
	} else {
		gst_element_set_state (v->play, GST_STATE_PAUSED);
	}
}

JNIEXPORT void JNICALL Java_processing_streaming_Streaming_gstreamer_1seek(JNIEnv *env, jobject obj, jlong handle, jfloat sec)
{
	video *v = get_video(handle);
	if (v == NULL) {
		return;
	}

	gst_element_seek (v->play, 1.0, GST_FORMAT_TIME, GST_SEEK_FLAG_FLUSH|GST_SEEK_FLAG_KEY_UNIT,
		GST_SEEK_TYPE_SET, (gint64)(sec * 1000000000),
		GST_SEEK_TYPE_NONE, GST_CLOCK_TIME_NONE);
}

JNIEXPORT void JNICALL Java_processing_streaming_Streaming_gstreamer_1set_1loop(JNIEnv *env, jobject obj, jlong handle, jboolean loop)
{
	video *v = get_video(handle);
	if (v == NULL) {
		return;
	}

	v->loop = loop;
}

JNIEXPORT jfloat JNICALL Java_processing_streaming_Streaming_gstreamer_1get_1duration(JNIEnv *env, jobject obj, jlong handle)
{
	video *v = get_video(handle);
	if (v == NULL) {
		return -1.0f;
	}

	gint64 len;
	if (gst_element_query_duration(v->play, GST_FORMAT_TIME, &len)) {
		return len/1000000000.0f;
	} else {
		return -1.0f;
	}
}

JNIEXPORT jfloat JNICALL Java_processing_streaming_Streaming_gstreamer_1get_1time(JNIEnv *env, jobject obj, jlong handle)
{
	video *v = get_video(handle);
	if (v == NULL) {
		return -1.0f;
	}

	gint64 pos;
	if (gst_element_query_position(v->play, GST_FORMAT_TIME, &pos)) {
		return pos/1000000000.0f;
	} else {
		return -1.0f;
	}
}
