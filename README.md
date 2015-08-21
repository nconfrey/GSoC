Nick Confrey's Google Summer of Code Project 2015
======
###Google Summer of Code Project for [Processing.org](https://processing.org/)

This project is broken up into roughly three useful parts, focusing on streaming media over the network, primarily video and audio streams. 

The first part of the project was modifications and extensions to the existing Processing network library. 

The second part was upgrading Processing's video capability to use GStreamer 1.0 instead of the depreciated GStreamer 0.10 which Processing was previously relying. 

Finally, the third part of the project was creating a streaming library that allows Processing users to stream local videos and songs/audio files to other sketches and other computers.

Section 1: Modifications to the Core Processing Network Library
------
The [core network library](https://processing.org/reference/libraries/net/) for Processing was originally created to serve as a barebones way to exchange data across computers and sketches, but lacked several important features in its simplicity.

The [full change log](https://github.com/nconfrey/GSoC/blob/master/coreExamples/Network%20changes.md) details exactly what I changed, but the new functionality is:

* Ability for the server to address individual clients (instead of multicast support only)
* The server can now access the client list iterate over it
* Raising events for client disconnects, and being able to tell which client disconnected.

I think these changes are important for Processing users who want to make a multiplayer game which requires precise client handling, or any other networking application where the server needs to send messages to one client but not all of them.

My modifications are on a fork of the official Processing source code [here](https://github.com/nconfrey/processing/tree/master/java/libraries/net/src/processing/net), and examples of what is possible with the new changes are in the [coreExamples](https://github.com/nconfrey/GSoC/tree/master/coreExamples) folder. The source code can be built just as Processing is usually built from source, detailed [here](https://github.com/processing/processing/wiki/Build-Instructions), and the modified network code will be ready to go.

Section 2: Allowing Processing to use GStreamer 1.0
------
##Section Summary and Challenges
[GStreamer](http://gstreamer.freedesktop.org/) is a native C library for video and audio playback and analysis. Since GStreamer 0.10 serves as the backbone for the [Processing video library](https://www.processing.org/reference/libraries/video/), I thought it would be a good place to start developing a video streaming library. Originally, Processing was able to access the C functions in Java by custom built Java Native Access bindings as part of the [gstreamer-java project](https://code.google.com/p/gstreamer-java/). These java bindings were writen specifically for the 0.10 version of GStreamer. Unfortunately, GStreamer moved onto version 1.0 and left 0.10 undeveloped, meaning that Processing was no longer receiving the newest updates, which threatend to eventually break the video library as new hardware was no longer being supported. In order to create an up-to-date video streaming library, I needed to first build a new way for Processing to use GStreamer.

The conversion ended up not being an easy task, and the project tacked many times before succeeding. Last year for the Google Summer of Code, a student attempted to wrap GStreamer in Java in a [gir2java project](https://github.com/gstreamer-java/gir2java). As detailed in his [Google Summer of Code Report](https://github.com/gstreamer-java/gir2java/wiki/GSOC-2014-report), he attempted to automatically generate JNA bindings by parsing gir files. Despite a lot of promising work in that direction, the project never reached a state where it was immediately usable for Processing projects. After spending time working with his parser, I decided against the route of using JNA again, or similiar solutions like bridj or JNAerator due to its overwhelming complexity and brittle nature. After all, these bindings would have to be rebuilt every time a new version of GStreamer was released. This would be a drain on the Processing community.

Instead, we turned toward Java Native Interface (JNI). Rather than wrap all the necessary functions in Java, we decided to do the majority of the heavy lifting coding in C and use GStreamer natively. The workflow was to code in C, delare native methods in Java, and finally `System.loadLibrary()` in java to access the methods in the shared library. More information about JNI can be found in [this tutorial](https://www3.ntu.edu.sg/home/ehchua/programming/java/JavaNativeInterface.html) I consulted often.

Finally, the Processing community can access the most recent version of GStreamer, and with it, the most recent plugins. While the code itself is highly tailored towards videostreaming and video playback, the same JNI technique can be used to access the wealth of GStreamer resources, from CD ripping all the way to audio analysis. Processing users will now have much more power in the areas of video and audio. I have also left exposed a `public static void pipelineLaunch(String pipe)` function, which parses a GStreamer pipeline string and then launches and runs the pipeline.

##Use cases
Currently, the conversion is capable of:

* Running and launching GStreamer 1.0 pipelines from Strings passed in from Processing
* Using an appsink to convey frame data from a GStreamer pipeline to a native Processing window
* 

##Installation

##To Do

Section 3: Video and Audio Streaming Processing Library
------

##Use Cases
Currently, the videoStreaming Processing library can do the following:
* Stream audio from one sketch to another in any of the common formats (.mp3, .wav etc.)
* Stream video from one sketch to another in most of the common formats (.mp4 and .3gp work, .mov still needs more testing)
* Stream the webcam from one computer to another using Processing
* Stream the Processing sketch window to another Processing sketch. Anything that is drawn on one screen will be streamed to the other.
* Statically launching GStreamer pipelines that are fully functional and can do anything that GStreamer can do

##To Do and Known Issues