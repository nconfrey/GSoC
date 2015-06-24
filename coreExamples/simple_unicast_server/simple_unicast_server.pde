import processing.net.*;

Server myServer;

void setup()
{
  size(500, 500);
  
  // Starts a myServer on port 5204
  myServer = new Server(this, 5204);
}

void draw()
{
  background(255);
  
  int numClients = myServer.getClientListLength();
  String users = "We have " + numClients + " users connected";
  fill(0);
  text(users, 0, 0, width, height);
  
  fill(255,0,0);
  textSize(42);
  text("Press a # Key to send that Client a color", 0, 200, width, height);
  
  
}

void keyPressed()
{
  int clientNum = key;
  int totalClients = myServer.getClientListLength();
  println("Client requested is " + clientNum + " and total clients are " totalClients);
  
  if(clientNum == totalClients) //Check to see if that client exists
  {
    Client c = myServer.getClientAt(clientNum);
    //Send three random numbers that represent rgb color values
    c.write(random(255) + "," + random(255) + "," + random(255));
  }
  else
  {
    println("Error! No such client connected.");
  }
}

void serverEvent(Server someServer, Client someClient, String eventType)
{
  
}