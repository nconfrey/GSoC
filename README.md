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

Section 3: Video and Audio Streaming Processing Library
------