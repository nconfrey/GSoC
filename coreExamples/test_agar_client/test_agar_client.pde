//CLIENT CODE

import processing.net.*; 
Client myClient; 
int dataIn;
String data[];

//Location variables
int xPos = width/2;
int yPos = height/2;
float size = random(5,25);
 
void setup() { 
  size(200, 200); 
  // Connect to the local machine at port 5204.
  // This example will not run if you haven't
  // previously started a server on this port.
  myClient = new Client(this, "127.0.0.1", 5204); 
} 
 
void draw() { 
  background(255);
  ellipse(xPos, yPos, size, size);
  myClient.write(xPos + "," + yPos + "," + (int)size);
  
  if (myClient.available() > 0) { 
    dataIn = myClient.read(); 
    data = split(dataIn, ',');
    if(data[0].equals("LOSE"))
    {
      println("You lose!");
    }
    else if(data[0].equals("BIG"))
    {
      println("You eat!");
    }
  } 
  
}

void keyPressed()
{
  switch (keyCode)
  {
    case UP: yPos--;
              break;
    case DOWN: yPos++;
              break;
    case LEFT: xPos--;
              break;
    case RIGHT: xPos++;
              break;
  } 
}