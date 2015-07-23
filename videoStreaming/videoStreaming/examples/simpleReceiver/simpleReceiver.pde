import template.library.*;

VideoReceiver vc;
PImage img;

void setup()
{
  size(320, 240);
  img = createImage(320, 240, RGB);
  vc = new VideoReceiver(this, 8008, 320, 240, img);
  //vc.StreamReceive("https://youtu.be/mN0TvP2ZgIg");
}

void draw()
{
  img = vc.receive();
  background(0);
  imageMode(CENTER);
  if(img != null)
    image(img, width/2, height/2);
}