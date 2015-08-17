import template.library.*;

SimpleVideo video;
void setup()
{
    video = new SimpleVideo(this);
}

void draw()
{
    video.streamAudio("/home/nc/Desktop/GSoC/GSoC/videoStreaming/videoStreaming/data/groove.mp3");
}
