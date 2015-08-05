public class gstJNI {
	static {
		System.loadLibrary("gstJNI"); //loading c wrapper 'library'
		//loading libgstJNI.so Unixes, gstJNI.dll on Windows
	}

	private native int pipelineLaunch(String msg);

	public static void main(String[] args) {
		System.out.println(args[0]);
		new gstJNI().pipelineLaunch(args[0]);
	}
}