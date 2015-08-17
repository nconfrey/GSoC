import template.library.*;

SimpleVideo video;
void setup()
{
    video = new SimpleVideo(this);
}

void draw()
{
    video.receiveAudio();
}
