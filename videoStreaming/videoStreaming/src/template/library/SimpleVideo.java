package template.library;

import java.io.File;
import processing.core.*;
import java.lang.reflect.*;

public class SimpleVideo {

	private static boolean loaded = false;
	private static boolean error = false;

	private long handle = 0;
	private PApplet parent;
    private Method movieEventMethod;

	public SimpleVideo(PApplet parent, String fn) {
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

        if (gstreamer_register()) {
          System.out.println("Registered callback go pull frames from gstreamer!");
        } else {
          throw new RuntimeException("Could not register callback");
        }

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

		handle = gstreamer_loadFile(fn);
		if (handle == 0) {
			throw new RuntimeException("Could not load video");
		}
		
        try {
          movieEventMethod = parent.getClass().getMethod("movieEvent", int[].class);
          return;
        } catch (Exception e) {
          // no such method, or an error... which is fine, just ignore
        }		
	}

	//A simpler constructor for use without loading a movie
	public SimpleVideo(PApplet parent)
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

    public void readFrame(int[] pixels) {
      // do stuff
      System.out.println("data from gstreamer: " + pixels.length);
      
      if (movieEventMethod != null) {
        try {
          movieEventMethod.invoke(parent, pixels);
        } catch (Exception e) {
          e.printStackTrace();
          movieEventMethod = null;
        }   
      }   
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
    
    //Takes an audio file and streams it via local host and port 6969
    //Use the corresponding method receiveAudio() to easily receive the stream
    //TODO: Threading!
    public void streamAudio(String filepath)
    {
    	//Validate Filepath
    	File f = new File(filepath);
    	if(f.exists()) 
    	{ 
    		String pipe = "filesrc location=" + filepath + " ! decodebin ! audioconvert ! rtpL16pay ! udpsink port=6969 host=127.0.0.1";
    		gstreamer_pipeline_launch(pipe);
    	}
    	else
    	{
    		System.err.println("Cannot find file! Cannot stream");
    	}
    }
    
    //Assumes there is already data coming in from streamAudio(), receives from localhost and post 6969
    public void receiveAudio()
    {
    	gstreamer_pipeline_launch("udpsrc port=6969 caps=\"application/x-rtp, media=(string)audio, format=(string)S32LE, layout=(string)interleaved, clock-rate=(int)44100, channels=(int)2, payload=(int)0\" ! rtpL16depay ! playsink");
    }

	private static native boolean gstreamer_init();
	private native boolean gstreamer_register();	
	private native long gstreamer_loadFile(String fn);
	private native void gstreamer_play(long handle, boolean play);
	private native void gstreamer_seek(long handle, float sec);
	private native void gstreamer_set_loop(long handle, boolean loop);
	private native float gstreamer_get_duration(long handle);
	private native float gstreamer_get_time(long handle);
	
	private static native void gstreamer_pipeline_launch(String pipe);

}
