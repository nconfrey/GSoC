import template.library.*;

SimpleVideo video;
void setup()
{
    video = new SimpleVideo(this);
}

void draw()
{
    video.streamAudio("192.168.1.1", 6969, "/home/nc/Desktop/GSoC/GSoC/videoStreaming/videoStreaming/data/fair.wav");
}
