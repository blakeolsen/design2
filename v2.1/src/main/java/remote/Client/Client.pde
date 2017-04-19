// LIBRARIES
import processing.serial.*;

// CONSTANTS
private static int FPS = 30;
private static int BACKGROUND = 255;

// GLOBALS
Serial ARDUINO_ONE;
Serial ARDUINO_TWO;
String PORT_ONE = "COM4";
String PORT_TWO = "COM6";
PFont font;

void setup(){
  // PREPARE THE DISPLAY
  size(500,500);
  smooth();
  frameRate(FPS);
  
  printArray(Serial.list());
  // PREPARE THE BLUETOOTH
  ARDUINO_ONE = new Serial(this, Serial.list()[0], 38400);
  ARDUINO_ONE.bufferUntil('\n');
  /*
  ARDUINO_TWO = new Serial(this, PORT_TWO, 38400);
  ARDUINO_TWO.bufferUntil('\n');
  */
  println("Beginning Crutch Readings");
}

void serialEvent(Serial CON) {
  println("FOUND");
  if (CON == ARDUINO_ONE) {
    
  } else if (CON == ARDUINO_TWO) {
    
  }
}

void draw(){
  // CLEAR BACKGROUND
  background(BACKGROUND);
  
  // GATHER DATA
  if (ARDUINO_ONE.available() > 0) {
    
  
  // Draw Title
  font = createFont ("Serif",30);
  textFont (font);
  fill(0);
  textAlign(TOP,LEFT);
  text("iCrutch",60,60);
  
  // Draw 
}