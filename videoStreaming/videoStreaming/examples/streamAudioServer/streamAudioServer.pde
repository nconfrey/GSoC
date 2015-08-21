import processing.streaming.*;

Streaming video;
void setup()
{
    video = new Streaming(this);
}

void draw()
{
    //video.streamAudio("/home/nc/Desktop/GSoC/GSoC/videoStreaming/videoStreaming/data/groove.mp3");
    Streaming.pipelineLaunch("filesrc location=/home/nc/sample.mp4 ! decodebin name=dec ! queue ! videoconvert ! x264enc pass=qual quantizer=20 tune=zerolatency ! rtph264pay config-interval=1 ! udpsink host=127.0.0.1 port=5555 sync=true dec. ! queue ! audioconvert ! audioresample ! autoaudiosink");
}
