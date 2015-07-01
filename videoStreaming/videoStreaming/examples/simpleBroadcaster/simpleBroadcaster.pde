import template.library.*;
import java.io.*;
import processing.video.*;

Capture cam;
VideoBroadcaster vb;

void setup()
{
  size(320, 240);
  vb = new VideoBroadcaster(this, 8008, "localhost");
  cam = new Capture(this, width, height, 30);
  cam.start();
}

void draw()
{
  image(cam, 0, 0);
}

void captureEvent( Capture c ) {
  c.read();
  // Whenever we get a new image, send it!
  vb.broadcast(c);
}