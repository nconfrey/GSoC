import processing.streaming.*;

Streaming video;
void setup()
{
    video = new Streaming(this);
}

void draw()
{
    video.streamAudio("/home/nc/Desktop/GSoC/GSoC/videoStreaming/videoStreaming/data/groove.mp3");
}
