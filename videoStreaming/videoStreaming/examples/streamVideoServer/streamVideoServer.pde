import processing.streaming.*;
Streaming video;

void setup() {
  size(640, 360);
  video = new Streaming(this, 640, 360);
  frameRate(30);
}

void draw() {
  background(0);
  text("Streaming to port 5555", 100, 100);
  video.videoBroadcast("127.0.0.1", 5555, "/home/nc/sample.mp4");
}
