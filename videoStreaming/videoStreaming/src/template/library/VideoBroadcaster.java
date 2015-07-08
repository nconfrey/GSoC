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
	public void test(String name){
		String[] arg = { "idk" }; //this is where the Gstreamer options would go
		
		Video.init(); //Does all the linking of the GStreamer library files
		arg = Gst.init(name,  arg); //Then get the framework ready
		Pipeline pipe = new Pipeline(name);
		Element src = ElementFactory.make("fakesrc", "Source");
		Element sink = ElementFactory.make("fakesink", "Destination");
		pipe.addMany(src, sink);
		src.link(sink);
		pipe.setState(State.PLAYING);
		Gst.main(); //actually runs the thing
		pipe.setState(State.NULL); //cleanup
		
		/*
		arg = Gst.init("AudioPlayer", arg);
        PlayBin2 playbin = new PlayBin2("AudioPlayer");
        playbin.setInputFile(new File(arg[0]));
        playbin.setVideoSink(ElementFactory.make("fakesink", "videosink"));
        playbin.setState(State.PLAYING);
        Gst.main();
        playbin.setState(State.NULL);
        */
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
	
	public void screenBroadcast()
	{
		broadcast(myParent.get());
	}
}

