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
 */

package template.library;

import javax.imageio.*;
import org.gstreamer.*;
import org.gstreamer.Buffer;
import org.gstreamer.elements.*;
import java.awt.image.*;
import java.net.*;
import java.io.*;
import processing.core.*;
import processing.opengl.PGraphicsOpenGL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

/**
 * This class should be set up and run first in a sketch, to send information to the receiver sketch.
 * This is the server.
 * @example Hello 
 */

public class VideoBroadcaster {
	
	int clientPort; //Port to send to
	DatagramSocket ds;
	InetAddress address;
	
	// myParent is a reference to the parent sketch
	PApplet myParent;
	PGraphicsOpenGL pg;
	
	public final static String VERSION = "##library.prettyVersion##";
	

	/**
	 * Usually called in the setup() method in your sketch to
	 * initialize and start the library.
	 * 
	 * @example Hello
	 * @param theParent
	 */
	public VideoBroadcaster(PApplet theParent, int client, String addy) {
		myParent = theParent;
		clientPort = client;
		
		try { //Create the socket to send out on
			ds = new DatagramSocket();
			address = InetAddress.getByName(addy);
		}
		catch (SocketException e) {
			e.printStackTrace();
		}
		catch (UnknownHostException uke){
			System.out.println("Unknown Host IP Address!");
			uke.printStackTrace();
		}
		
		welcome();
	}
	
	private void welcome() {
		System.out.println("##library.name## ##library.prettyVersion## by ##author##");
	}
	
	//Test out the gstreamer stuff
	public void test(String name, String fName){
		String[] arg = { "idk" }; //this is where the Gstreamer options would go
		
		GStreamLink.init(); //Does all the linking of the GStreamer library files
		arg = Gst.init(name,  arg); //Then get the framework ready
		
		//Pipeline pipe = Pipeline.launch("filesrc location=" + fName + " ! mad ! audioconvert ! audioresample ! osssink");
		Pipeline pipe = Pipeline.launch("videotestsrc ! udpsink name=udp");
		pipe.getElementByName("udp").set("host", "192.168.8.255");
		pipe.getElementByName("udp").set("port", "8888");
		pipe.play();
		
		Pipeline pipe2 = Pipeline.launch("udpsink name=udp ! autovideosink");
		pipe2.getElementByName("udp").set("port", "8888");
		pipe2.play();
		
		Gst.main();
		
		//Audio Player - tested working
		/*
		
		*/
		//Sample manual pipeline - tested working
		/*
		Pipeline pipe = new Pipeline(name);
		Element src = ElementFactory.make("fakesrc", "Source");
		Element sink = ElementFactory.make("fakesink", "Destination");
		pipe.addMany(src, sink);
		src.link(sink);
		pipe.setState(State.PLAYING);
		Gst.main(); //actually runs the thing
		pipe.setState(State.NULL); //cleanup
		*/
	}
	
	//Wraps the gst-launch utility so that it can be used in processing.
	//Can use any sample pipeline in the syntax of gst-launch
	//Ex: "videotestsrc ! autovideosink" or "audiotestsrc ! autoaudiosink"
	public void testPipeline(String piper)
	{
		String[] arg = { "idk" }; //this is where the Gstreamer options would go
		
		GStreamLink.init(); //Does all the linking of the GStreamer library files
		
		arg = Gst.init("Test Pipe",  arg); //Then get the framework ready
		
		Pipeline pipe = Pipeline.launch(piper);
		pipe.play();
		//Gst.main();
	}
	
	public void testAudio(String name, String fName)
	{
		String[] arg = { "idk" }; //this is where the Gstreamer options would go
		GStreamLink.init(); //Does all the linking of the GStreamer library files
		arg = Gst.init(name,  arg); //Then get the framework ready
		
		PlayBin2 playbin = new PlayBin2(name);
		playbin.setInputFile(new File(fName));
		playbin.setVideoSink(ElementFactory.make("fakesink", "videosink"));
		//Element udp = ElementFactory.make("udpsink", "udp");
		//playbin.setAudioSink(udp);
		playbin.setState(State.PLAYING);
		Gst.main(); //do the actual playing
		playbin.setState(State.NULL); //cleanup
	}
	
	public void testStreamCast()
	{
		String ip = "127.0.0.1";
		GStreamLink.init();
		Gst.init("Server", new String[0]);
		Pipeline pipe = new Pipeline();
		Element vid = ElementFactory.make("videotestsrc", "test");
		Element udpSink = ElementFactory.make("udpsink", "udpsink0");
		udpSink.set("host", ip);
		udpSink.set("port", 5000);
		pipe.addMany(vid, udpSink);
		Element.linkMany(vid, udpSink);
		pipe.play();
	}
	
	public void testStreamRecv()
	{
		GStreamLink.init();
		
		Pipeline pipeline0 = new Pipeline("pipeline0");
		pipeline0.setState(State.NULL); 
        pipeline0.setState(State.PAUSED);
        
		BaseSrc udpsrc0 = (BaseSrc) ElementFactory.make("udpsrc", "udpsrc0"); 
		udpsrc0.set("port", 5000);
		Element vidSink = ElementFactory.make("autovideosink", "vidSink");
		
		pipeline0.addMany(udpsrc0, vidSink);
		Element.linkMany(udpsrc0, vidSink);
		pipeline0.setState(State.PLAYING);
	}
	
	public void broadcast(PImage img){
		BufferedImage bi = new BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_RGB);
		
		//Go through all pixels and pack them into the buffer
		img.loadPixels();
		bi.setRGB(0, 0, img.width, img.height, img.pixels, 0, img.width);
		
		//Changing the image into streams of bytes
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(bStream);
		
		//transform our buffer into a jpg, put it into stream
		try{
			ImageIO.write(bi, "jpg", bos);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		//create the packets
		byte[] packet = bStream.toByteArray();
		DatagramPacket dPacket = new DatagramPacket(packet, packet.length, address, clientPort);
		
		//Send them out!
		System.out.println("Sending a packet with bytes: " + packet.length);
		try{
			ds.send(dPacket);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	//TODO: COMPRESSION!
	public void broadcastSound(String fName)
	{
		Path path = Paths.get(fName);
		byte[] data;
		DatagramPacket dPacket;
		try
		{
			data = Files.readAllBytes(path);
			dPacket = new DatagramPacket(data, data.length, address, clientPort);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
		//Send them out!
		System.out.println("Sending a packet with bytes: " + data.length);
		try{
			ds.send(dPacket);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void screenBroadcast()
	{
		broadcast(myParent.get());
	}
}

