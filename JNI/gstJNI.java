public class gstJNI {
	static {
		//System.load("/usr/lib/x86_64-linux-gnu/libgstreamer-1.0.so");
		System.loadLibrary("gstJNI"); //loading c wrapper 'library'
		//loading libgstJNI.so Unixes, gstJNI.dll on Windows
	}

	private native int pipelineLaunch(String msg);

	public static void main(String[] args) {
		//System.setProperty(“java.library.path”, “/path/to/library”);
		System.out.println(args[0]);
		new gstJNI().pipelineLaunch(args[0]);
	}
}