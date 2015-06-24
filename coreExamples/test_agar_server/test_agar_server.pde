//SERVER CODE

import processing.net.*;
Server myServer;
int numClients = 0;
String input;
int data[];
Client[] clientList;
int coordList[][];

void setup() {
  size(200, 200);
  frameRate(30);
  coordList = new int[10][3]; //capping at 10 users for now 
  // Starts a myServer on port 5204
  myServer = new Server(this, 5204); 
}

//from the index to the end of the clientsList, check intersection with all other balls
boolean checkCollision(int index)
{
  for(int i = index+1; i < numClients; i++) //start at the ball after index
  {
    float betweenCenters = dist(coordList[index][0], coordList[index][1], coordList[i][0], coordList[i][1]);
    println("between ball " + index + " and ball " + i + " the distance is " + betweenCenters);
    int yeah = betweenCenters - coordList[index][2]/2 - coordList[i][2]/2;
    println("distance between edges is " + yeah);
    if(yeah < 0)
      return true;
  }
  return false;
}

void draw() 
{
  //get most recent client list and number of clients
  numClients = myServer.getClientListLength();
  //println(numClients);
  clientList = myServer.getClientList();
  background(255);
  
  //update all our client coordinates
  for(int i = 0; i < numClients; i++)
  {
    input = clientList[i].readString();
    if(input != null)
    {
      data = int(split(input, ','));
      coordList[i][0] = data[0];
      coordList[i][1] = data[1];
      coordList[i][2] = data[2];
    }
  }
  //check collisions with other "fish"
  for(int i = 0; i < numClients; i++)
  {
    if(checkCollision(i))
    {
    }
  }
  
  //TODO: Problem! When the first user disconnects, we have junk data in the coordList.
  //How to monitor client connections and then remove their data when they go...
  //It seems like the problem is even more pervasive. The actual server source code doesn't know when a client disconnects
  //Or actually, it seems like available uses it
  
  //done receiving new information from clients, time to draw
  for(int i = 0; i < numClients; i++)
  {
    ellipse(coordList[i][0], coordList[i][1], coordList[i][2], coordList[i][2]);  
  }
}

void serverEvent(Server someServer, Client someClient, String eventType) {
  println("Event Type is " + eventType);
  
  println("We have a new client: " + someClient.ip());
  numClients++;
  clientList[numClients] = someClient;
}