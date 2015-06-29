import processing.net.*;

Client myClient;
String dataIn;
int data[];

int red = 255;
int green = 255;
int blue = 255;

void setup()
{
  size(500, 500);
  
  // Connect to the local machine at port 5204.
  // This example will not run if you haven't
  // previously started a server on this port.
  myClient = new Client(this, "127.0.0.1", 5204); 
}

void draw()
{
  if (myClient.available() > 0) { 
      dataIn = myClient.readString(); 
      data = int(split(dataIn, ','));
      red = data[0];
      green = data[1];
      blue = data[2];
  }
  background(red,green,blue);
}