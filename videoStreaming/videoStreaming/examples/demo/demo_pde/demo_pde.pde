import processing.streaming.*;
Streaming video;

void setup() {
  size(640, 360);
  //Assuming stream is coming in on port 5555
  video = new Streaming(this, 5555, 640, 360);
  video.loop();
  frameRate(30);
}

void draw() {
  println(video.time() + " of " + video.duration());
  PImage frame = video.getFrame();
  if (frame != null) {
    image(frame, 0, 0);
  }
}

void keyPressed() {
  video.jump(random(0, video.duration()));
}

void movieEvent(int[] pixels) {
  println("movie event");
}
