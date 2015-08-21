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
 * HUGE Thanks to Andres Colubri, without his help this library wouldn't be possible
 */

import java.io.File;
import processing.core.*;
import java.lang.reflect.*;

public class Streaming {

	private static boolean loaded = false;
	private static boolean error = false;

	// capabilities passed to GStreamer
	protected static String caps = "video/x-raw,format=RGB,width=640,height=360,pixel-aspect-ratio=1/1";
	// pipeline description passed to GStreamer
	// first %s is the uri (filled in by native code)
	// appsink must be named "sink" (XXX: change)
	private static String pipeline = "uridecodebin uri=%s ! videoconvert ! videoscale ! appsink name=sink caps=\"" + caps + "\"";
	//private static String pipeline = "udpsrc port=5555 caps=\"application/x-rtp, payload=127\" ! rtph264depay ! avdec_h264 ! videoconvert ! videoscale ! appsink name=sink caps=\"" CAPS "\"");
	//private static String pipeline = "uridecodebin uri=%s ! decodebin name=dec ! queue ! videoconvert ! videoscale ! appsink name=sink caps=\"" + caps + "\" dec. ! queue ! audioconvert ! audioresample ! autoaudiosink";

	
	private long handle = 0;
	private PApplet parent;
    private Method movieEventMethod;

	public Streaming(PApplet parent, String fn) {
		super();
        this.parent = parent; 

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
		
		//Once again, do we need it to be a URI?
		//Think about getting rid of this for streaming sources

		// get absolute path for fn
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

		handle = gstreamer_loadFile(fn, pipeline);
		if (handle == 0) {
			throw new RuntimeException("Could not load video");
		}
		
        try {
          movieEventMethod = parent.getClass().getMethod("movieEvent", int[].class);
        } catch (Exception e) {
          // no such method, or an error... which is fine, just ignore
        }		
	}

	//A simpler constructor for use without loading a movie
	public Streaming(PApplet parent)
	{
		super();
        this.parent = parent; 

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

	    PImage frame = parent.createImage(640, 360, PConstants.RGB);

	    // XXX: not working quite right
	    // XXX: we also need to handle the audio somehow
	    int idx = 0;
	    frame.loadPixels();
	    for (int i = 0; i < buffer.length/3; i++) {
	      int ri = 3 * i + 0;
	      int gi = 3 * i + 1;
	      int bi = 3 * i + 2;
	      int r = buffer[ri];
	      int g = buffer[gi];
	      int b = buffer[bi];
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
		
		System.out.println("Looking like gstreamer has been loaded");
    	gstreamer_pipeline_launch(pipe);
    }
    
    //Takes an audio file and streams it via local host and port 6969, no argument default
    //Use the corresponding method receiveAudio() to easily receive the stream
    public void streamAudio(String filepath)
    {
    	streamAudio(filepath, 6969, "127.0.0.1");
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
    	receiveAudio(6969);
    }
    
    public void receiveAudio(int port)
    {
    	String portt = Integer.toString(port);
    	gstreamer_pipeline_launch("udpsrc port=" + portt + " caps=\"application/x-rtp, media=(string)audio, format=(string)S32LE, layout=(string)interleaved, clock-rate=(int)44100, channels=(int)2, payload=(int)0\" ! rtpL16depay ! playsink");
    }

    private static native boolean gstreamer_init();
    private native long gstreamer_loadFile(String fn, String pipeline);
    private native void gstreamer_play(long handle, boolean play);
    private native void gstreamer_seek(long handle, float sec);
    private native void gstreamer_set_loop(long handle, boolean loop);
    private native float gstreamer_get_duration(long handle);
    private native float gstreamer_get_time(long handle);
    private native byte[] gstreamer_get_frame(long handle);
	
	private static native void gstreamer_pipeline_launch(String pipe);

}
