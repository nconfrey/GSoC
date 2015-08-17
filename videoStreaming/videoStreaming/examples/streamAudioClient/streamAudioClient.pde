import processing.streaming.*;

SimpleVideo video;
void setup()
{
    video = new SimpleVideo(this);
}

void draw()
{
    video.receiveAudio();
}
