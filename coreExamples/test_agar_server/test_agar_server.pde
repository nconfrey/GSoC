//SERVER CODE

import processing.net.*;
Server myServer;
int numClients = 0;
String input;
int data[];
int coordList[][];

void setup() {
  size(200, 200);
  coordList = new int[10][3]; //capping at 10 users for now 
  // Starts a myServer on port 5204
  myServer = new Server(this, 5204); 
}

//from the index to the end of the clientsList, check intersection with all other balls
int checkCollision(int index)
{
  for(int i = index+1; i < numClients; i++) //start at the ball after index
  {
    float betweenCenters = dist(coordList[index][0], coordList[index][1], coordList[i][0], coordList[i][1]);
    //println("between ball " + index + " and ball " + i + " the distance is " + betweenCenters);
    float betweenEdges = betweenCenters - coordList[index][2]/2 - coordList[i][2]/2;
    //println("distance between edges is " + yeah);
    if(betweenEdges < 0)
    {
      //Now we know there has been a collision. Return the collision partner
      return i;
    }
  }
  return -1;
}

//Send this specific client a message to get bigger
void getBigger(Client client, int size)
{
  client.write("BIG," + size);
}

//Send this specific client that they lose
void youLose(Client client)
{
  client.write("LOSE");
  myServer.disconnect(client);
}

void draw() 
{
  //get most recent number of clients
  numClients = myServer.getClientListLength();

  background(255);
  
  //update all our client coordinates
  for(int i = 0; i < numClients; i++)
  {
    input = myServer.getClientAt(i).readString();
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
    int collide = checkCollision(i);
    if(collide > 0)
    {
      //Check which one is bigger to determine who loses and who gets bigger
      int size1 = coordList[i][2];
      int size2 = coordList[collide][2];
      if(size1 > size2)
      {
        getBigger(myServer.getClientAt(i), size2/2);
        youLose(myServer.getClientAt(collide));
      }
      else
      {
        getBigger(myServer.getClientAt(collide), size1/2);
        youLose(myServer.getClientAt(i));
      }
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
}