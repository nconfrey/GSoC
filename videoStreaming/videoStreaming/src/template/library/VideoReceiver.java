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
import java.awt.image.*;
import java.net.*;
import java.io.*;
import processing.core.*;
import processing.opengl.PGraphicsOpenGL;

/**
 * This class should be set up and run after the broadcaster, to receive information.
 * This is the client.
 * 
 * Thanks to: Syphon, Dan Shiffman's UDP
 * @example Hello 
 */

public class VideoReceiver implements PConstants{
	
	// myParent is a reference to the parent sketch
	PApplet myParent;
	public final static String VERSION = "##library.prettyVersion##";
	
	int port;
	DatagramSocket ds;
	byte[] buff = new byte[65536]; //Set to maximum size
	
	PImage img;

	/**
	 * Usually called in the setup() method in your sketch to
	 * initialize and start the library.
	 * 
	 * @example Hello
	 * @param theParent
	 */
	
	//TODO: have a better way of getting dimensions into here
	//TODO: don't require the user to initiliaze the PImage.
	//But why is createImage not working???
	public VideoReceiver (PApplet theParent, int port, int w, int h, PImage img) {
		myParent = theParent;
		this.port = port;
		this.img = img;
		welcome();
		
		//img = createImage(w, h, RGB);
		
		try{
			ds = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	
	private void welcome() {
		System.out.println("##library.name## ##library.prettyVersion## by ##author##");
	}
	
	public PImage receive(){
		DatagramPacket p = new DatagramPacket(buff, buff.length);
		try {
			ds.receive(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] data = p.getData();
		
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		
		img.loadPixels();
		try {
			BufferedImage bimg = ImageIO.read(bais);
			bimg.getRGB(0, 0, img.width, img.height, img.pixels, 0, img.width);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		img.updatePixels();
		
		return img;
	}
}

