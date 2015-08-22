package processing.streaming;

/**
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * Copyright ##copyright## ##author##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 * 
 * HUGE Thanks to Andres Colubri and Gottfried Haider, without their help this library wouldn't be possible
 */

import java.io.File;
import processing.core.*;
import java.lang.reflect.*;

public class Streaming {

	private static boolean loaded = false;
	private static boolean error = false;
	
	//Dimensions of the Processing screen
	int height;
	int width;

	// capabilities passed to GStreamer
	protected static String caps; //"video/x-raw,format=RGB,width=640,height=360,pixel-aspect-ratio=1/1";
	private static String pipeline;
	
	private long handle = 0;
	private PApplet parent;
    private Method movieEventMethod;

    //Use this constructor when just playing a movie (no streaming)
	public Streaming(PApplet parent, String fn, int width, int height) {
		super();
        this.parent = parent;
        this.width = width;
        this.height = height;
        
        loadGstreamer(height, width);
		

		// get absolute path for filename
		if (fn.indexOf("://") != -1) {
			// got URI, use as is
		} else {
			// first, check Processing's dataPath
			File file = new File(parent.dataPath(fn));
			if (file.exists() == false) {
				// next, the current directory
				file = new File(fn);
			}
			if (file.exists()) {
				fn = file.getAbsolutePath();
			}
		}

		/*
		 * This is a simple pipeline to launch a video
		 * Videoconvert handles conversion between many different common video types
		 * The format string will be filled in in the native code
		 * appsink must be named "sink"
		 */		
		pipeline = "uridecodebin uri=%s ! videoconvert ! videoscale ! appsink name=sink caps=\"" + caps + "\"";
		
		//false because this pipeline is not streaming and needs to be loaded from the local disk
		handle = gstreamer_loadFile(fn, pipeline, false);
		if (handle == 0) {
			throw new RuntimeException("Could not load video");
		}	
	}
	
	//Simpler constructor when not loading a video (audio streaming or videostreaming, for example)
	public Streaming(PApplet parent, int width, int height) {
		super();
        this.parent = parent;
        this.width = width;
        this.height = height;
        
		loadGstreamer(height, width);	
	}
	
	//Links against libstreamvideo.so, the JNI binding to the native c code in impl.c
	private void loadGstreamer(int height, int width)
	{
		if (!loaded) {
			System.out.println(System.getProperty("java.library.path"));
			System.loadLibrary("streamvideo");
			loaded = true;
			if (gstreamer_init() == false) {
				error = true;
			}
			
	        //Give the dimensions to GStreamer, and tell it the format of the video
			if(width > 0 && height > 0){
				caps = "video/x-raw,format=RGB,"
	        	 + "width=" + Integer.toString(width)
	        	 + ",height=" + Integer.toString(height)
	        	 + ",pixel-aspect-ratio=1/1";
			}
		}
		if (error) {
			throw new RuntimeException("Could not load gstreamer");
		}
		
        try {
          movieEventMethod = parent.getClass().getMethod("movieEvent", int[].class);
        } catch (Exception e) {
          // no such method, or an error... which is fine, just ignore
        }
	}
	
	public void videoReceive(int port)
	{
		// appsink must be named "sink"
		pipeline = "udpsrc port=" + Integer.toString(port)
				+ " caps=\"application/x-rtp, payload=127\" ! rtph264depay ! avdec_h264 ! videoconvert ! videoscale ! appsink name=sink caps=\"" 
				+ caps + "\"";
		//filename is set to null because we are streaming from network and don't need to load file
		//streaming is set to true
		handle = gstreamer_loadFile(null, pipeline, true);
		if (handle == 0) {
			throw new RuntimeException("Could not launch stream receive pipeline");
		}
	}
	
	public void videoBroadcast(String ip, int port, String filename)
	{
		pipeline = "filesrc location=" + filename
				+ " ! decodebin name=dec ! queue ! videoconvert ! x264enc pass=qual quantizer=20 tune=zerolatency "
				+ "! rtph264pay config-interval=1 ! udpsink host=" + ip
				+ " port=" + Integer.toString(port)
				+ " sync=true dec. ! queue ! audioconvert ! audioresample ! autoaudiosink";
		//System.out.println("Running: " + pipeline);
		gstreamer_pipeline_launch(pipeline);
	}
	
	public void dispose() {
		System.out.println("called dispose");
	}

	public void play() {
		gstreamer_play(handle, true);
	}

	public void pause() {
		gstreamer_play(handle, false);
	}

	public void stop() {
		gstreamer_play(handle, false);
		gstreamer_seek(handle, 0.0f);
	}

	public void loop() {
		gstreamer_set_loop(handle, true);
		gstreamer_play(handle, true);
	}

	public void noLoop() {
		gstreamer_set_loop(handle, false);
	}

	public void jump(float sec) {
		gstreamer_seek(handle, sec);
	}

	public float duration() {
		return gstreamer_get_duration(handle);
	}

	public float time() {
		return gstreamer_get_time(handle);
	}

	public PImage getFrame() {
	    byte[] buffer = gstreamer_get_frame(handle);
	    if (buffer == null) {
	      return null;
	    }

	    PImage frame = parent.createImage(width, height, PConstants.RGB);

	    // TODO: Colors are slightly off, need a better way to 
	    // TODO: we also need to handle the audio somehow
	    int idx = 0;
	    frame.loadPixels();
	    for (int i = 0; i < buffer.length/3; i++) {
	      int ri = 3 * i + 0;
	      int gi = 3 * i + 1;
	      int bi = 3 * i + 2;
	      int r = buffer[ri] & 0xff;
	      int g = buffer[gi] & 0xff;
	      int b = buffer[bi] & 0xff;
	      frame.pixels[idx++] = 0xFF000000 | (r << 16) | (g << 8) | b;
	    }
	    frame.updatePixels();

	    /*
	    if (movieEventMethod != null) {
	      try {
	        movieEventMethod.invoke(parent, pixels);
	      } catch (Exception e) {
	        e.printStackTrace();
	        movieEventMethod = null;
	      }
	    }
	    */
	    
	    return frame;
	  }
    
	//Statically launch a pipeline - just a wrapper for native GStreamer functions
    public static void pipelineLaunch(String pipe)
    {
    	if (!loaded) {
			System.out.println(System.getProperty("java.library.path"));
			System.loadLibrary("streamvideo");
			loaded = true;
			if (gstreamer_init() == false) {
				error = true;
			}
		}
		if (error) {
			throw new RuntimeException("Could not load gstreamer");
		}
    	gstreamer_pipeline_launch(pipe);
    }
    
    //Takes an audio file and streams it via local host and port 6969, no argument default
    //Use the corresponding method receiveAudio() to easily receive the stream
    public void streamAudio(String filepath)
    {
    	streamAudio(filepath, 8008, "127.0.0.1");
    }
    
    //TODO: Threading!
    public void streamAudio(String filepath, int port, String host)
    {
    	//Validate Filepath
    	File f = new File(filepath);
    	if(f.exists()) 
    	{
    		String portt = Integer.toString(port);
    		String pipe = "filesrc location=" + filepath + " ! decodebin ! audioconvert ! rtpL16pay ! udpsink port=" + portt + " host=" + host;
    		gstreamer_pipeline_launch(pipe);
    	}
    	else
    	{
    		System.err.println("Cannot find file! Cannot stream");
    	}
    }
    
    //Assumes there is already data coming in from streamAudio(), receives from localhost and port 6969 (no argument default)
    public void receiveAudio()
    {
    	receiveAudio(8008);
    }
    
    public void receiveAudio(int port)
    {
    	String portt = Integer.toString(port);
    	gstreamer_pipeline_launch("udpsrc port=" + portt + " caps=\"application/x-rtp, media=(string)audio, format=(string)S32LE, layout=(string)interleaved, clock-rate=(int)44100, channels=(int)2, payload=(int)0\" ! rtpL16depay ! playsink");
    }

    //Native methods are implemented in impl.c in the native folder
    private static native boolean gstreamer_init();
    private native long gstreamer_loadFile(String fn, String pipeline, boolean live);
    private native void gstreamer_play(long handle, boolean play);
    private native void gstreamer_seek(long handle, float sec);
    private native void gstreamer_set_loop(long handle, boolean loop);
    private native float gstreamer_get_duration(long handle);
    private native float gstreamer_get_time(long handle);
    private native byte[] gstreamer_get_frame(long handle);
	
	private static native void gstreamer_pipeline_launch(String pipe);

}
