import processing.streaming.*;

Streaming video;
void setup()
{
    video = new Streaming(this);
}

void draw()
{
    video.receiveAudio();
}
