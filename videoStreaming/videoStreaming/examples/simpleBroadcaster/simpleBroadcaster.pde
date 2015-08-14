import template.library.*;
import java.io.*;
import processing.video.*;

Capture cam;
VideoBroadcaster vb;

void setup()
{
  size(320, 240);
  vb = new VideoBroadcaster(this, 8008, "192.168.8.255");
  cam = new Capture(this, width, height, 30);
  cam.start();
}

void draw()
{
  image(cam, 0, 0);
  fill(random(255), random(255), 255);
  ellipse(width/2, height/2, 25, 25);
}

void captureEvent( Capture c ) {
  c.read();
  // Whenever we get a new image, send it!
  
  //Can either send just the camera frame
  //vb.broadcast(c);
  //Or we can send the entire processing screen!
  vb.screenBroadcast();
}
