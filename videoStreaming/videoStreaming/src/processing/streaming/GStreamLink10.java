package processing.streaming;

public class GStreamLink10 {
	
	//Native methods from C (JNI wrapped)
	private native int pipelineLaunch(String msg);
	private static native boolean gstreamer_init();
	
	private static boolean loaded = false;
	private static boolean error = false;
	
	public void init()
	{
		if(!loaded)
		{
			System.out.println(System.getProperty("java.library.path"));
			System.loadLibrary("gstJNI");
			loaded = true;
			
			if (gstreamer_init() == false) {
				error = true;
			}
		}
	}
	
	public void launch(String args)
	{
		System.out.println(args);
		if(!loaded)
		{
			throw new RuntimeException("GStreamer not loaded");
		}
		else
		{
			pipelineLaunch(args);
		}
	}
}
