#include <gst/gst.h>
#include <stdio.h>
#include <stdlib.h>
  
int main(int argc, char *argv[]) {
  GstElement *pipeline;
  GstBus *bus;
  GstMessage *msg;
  GError *e;
  
  e = NULL;
  /* Initialize GStreamer */
  gst_init (&argc, &argv);

  /* Build the pipeline */
  //TESTED WORKING AUDIO TRANSMISSION
  //pipeline = gst_parse_launch("audiotestsrc freq=1000 ! mulawenc ! rtppcmupay ! udpsink host=127.0.0.1 port=5555", &e);
  
  //TESTING VIDEO TRANSMISSION
  pipeline = gst_parse_launch("videotestsrc ! jpegenc ! rtpjpegpay ! udpsink host=127.0.0.1 port=5200", &e);

  //pipeline = gst_parse_launch("videotestsrc ! autovideosink", &e);

  if(e != NULL)
  {
    printf(e->message);
  }
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
  return 0;
}