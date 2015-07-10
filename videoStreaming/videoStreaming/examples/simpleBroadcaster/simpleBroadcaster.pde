import template.library.*;
import java.io.*;
import processing.video.*;

Capture cam;
VideoBroadcaster vb;

String wav = "C:\\Users\\Nick\\Music\\Youtubles\\RoadTrip\\Lift.wav";
String mp3 = "C:\\Users\\Nick\\Music\\1997.mp3";

void setup()
{
  size(320, 240);
  vb = new VideoBroadcaster(this, 8008, "localhost");
  //vb.test("test", "dontcare", "filesrc location=C:\\Users\\Nick\\Music\\Youtubles\\RoadTrip\\Lift.wav ! autoaudiosink");
  vb.test("test", "dontcare");
  //vb.testPipeline("test", "dontcare", "videotestsrc ! udpsink host=127.0.0.1 port=5000");
  //vb.testPipeline("test", "dk", "udpsrc port=5000 ! autovideosink");
  //vb.test("AudioThing", "C:\\Users\\Nick\\Music\\1997.mp3");
  //cam = new Capture(this, width, height, 30);
  //cam.start();
}

void draw()
{
  //image(cam, 0, 0);
  //fill(random(255), random(255), 255);
  //ellipse(width/2, height/2, 25, 25);
}

void captureEvent( Capture c ) {
  c.read();
  // Whenever we get a new image, send it!
  
  //Can either send just the camera frame
  //vb.broadcast(c);
  //Or we can send the entire processing screen!
  vb.screenBroadcast();
}