//CLIENT CODE

import processing.net.*; 
Client myClient; 
String dataIn;
String data[];

//Location variables
int xPos = width/2;
int yPos = height/2;
float size = random(5,25);

//Game variables
boolean lose = false;
 
void setup() { 
  size(200, 200); 
  // Connect to the local machine at port 5204.
  // This example will not run if you haven't
  // previously started a server on this port.
  myClient = new Client(this, "127.0.0.1", 5204); 
} 
 
void draw() { 
  background(255);
  if(lose)
  {
    fill(255,0,3);
    textSize(42);
    text("YOU LOSE", 0, 0, width, height);
  }
  else //if they haven't lost, keep playing the game!
  {
    ellipse(xPos, yPos, size, size);
    myClient.write(xPos + "," + yPos + "," + (int)size);
    
    if (myClient.available() > 0) { 
      dataIn = myClient.readString(); 
      data = split(dataIn, ',');
      if(data[0].equals("LOSE"))
      {
        println("You lose!");
        lose = true;
      }
      else if(data[0].equals("BIG"))
      {
        println("You eat!");
        size += Integer.parseInt(data[1]);
      }
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