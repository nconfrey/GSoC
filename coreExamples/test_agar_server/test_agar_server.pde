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
    println("between ball " + index + " and ball " + i + " the distance is " + betweenCenters);
    float betweenEdges = betweenCenters - coordList[index][2]/2 - coordList[i][2]/2;
    println("distance between edges is " + betweenEdges);
    if(betweenEdges < (width * -1))
      continue;
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

void updateClientCoordinates()
{
  //get most recent number of clients
  numClients = myServer.numClients();
  
  //update all our client coordinates
  for(int i = 0; i < numClients; i++)
  {
    input = myServer.getClient(i).readString();
    if(input != null)
    {
      data = int(split(input, ','));
      coordList[i][0] = data[0];
      coordList[i][1] = data[1];
      coordList[i][2] = data[2];
    }
  }
}

void draw() 
{
  background(255);
  
  //find out where all the fish are, and how many clients we have
  updateClientCoordinates();
  
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
        getBigger(myServer.getClient(i), size2/2);
        youLose(myServer.getClient(collide));
      }
      else
      {
        getBigger(myServer.getClient(collide), size1/2);
        youLose(myServer.getClient(i));
      }
    }
  }
  
  //done receiving new information from clients, time to draw
  for(int i = 0; i < numClients; i++)
  {
    ellipse(coordList[i][0], coordList[i][1], coordList[i][2], coordList[i][2]);  
  }
}

void serverDisconnectEvent(Server s, Client c) {
  //Since a client disconnected, we need to refresh our coordinate list
  println("Client disconnected");
  coordList = new int[10][3];
}

void serverEvent(Server someServer, Client someClient) {
  println("We have a new client: " + someClient.ip());
}