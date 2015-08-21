import processing.streaming.*;
Streaming video;

void setup() {
  size(640, 360);
  video = new Streaming(this, 640, 360);
  //This will accept and render any video coming in on port 5555
  video.videoReceive(5555);
  video.loop();
  frameRate(30);
}

void draw() {
  println(video.time());
  PImage frame = video.getFrame();
  if (frame != null) {
    image(frame, 0, 0);
  }
}
