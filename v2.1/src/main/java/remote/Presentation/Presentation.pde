// LIBRARIES
import processing.serial.*;

// CONSTANTS
private static int BLUETOOTH_RATE = 9600;
private static int FPS = 20;
private static int BACKGROUND = 255;
private static float GRAPH_SCALAR = 2.5;
private static int BAR_HEIGHT = 400;
private static int LABEL_HEIGHT = 10;
private static int NUMBER_HEIGHT = 30;
private static int HORIZONTAL_LOC = 200;

private static byte VIBRATE_ON = 'I';
private static byte VIBRATE_OFF = 'O';

private PFont LABELS;
private PFont NUMBERS;
private PFont TITLE_FONT;
private PFont FPS_FONT;
private PFont SCALE_FONT;

// GLOBALS
Serial ARDUINO_ONE;
Serial ARDUINO_TWO;
String PORT_ONE = "/dev/tty.usbmodem1411";
String PORT_TWO = "COM6";
float FORCE_ONE;
float FORCE_TWO;
float ESTIMATE;
float USER_WEIGHT;
float MAX_WEIGHT;

void setup(){
  // PREPARE THE DISPLAY
  size(500,500);
  smooth();
  frameRate(FPS);
  
  printArray(Serial.list());
  // PREPARE THE BLUETOOTH
  ARDUINO_ONE = new Serial(this, PORT_ONE, BLUETOOTH_RATE);
  ARDUINO_ONE.bufferUntil('\n');
  
  // SET CONSTANTS
  LABELS = createFont ("Serif",15);
  NUMBERS = createFont ("Serif",20);
  TITLE_FONT = createFont ("Serif",40);
  SCALE_FONT = createFont ("Serif",10);
  FPS_FONT = createFont ("Serif",10);
  FORCE_ONE = 10;
  FORCE_TWO = 20;
  MAX_WEIGHT = 20;
  println("Begin iCrutch");
}

/**
 * loops with updating bar graphs
 */
void draw(){
  float barWidth = width/6;
  
  // CLEAR BACKGROUND
  background(BACKGROUND);
  
  // Draw Title
  textFont (TITLE_FONT);
  fill(0);
  textAlign(BOTTOM,RIGHT);
  text("iCrutch",60,60);
  
  // Draw FPS
  textFont (FPS_FONT);
  fill(0);
  textAlign(BOTTOM,LEFT);
  text("FPS: "+(int)frameRate,width-140,30);
  
  if (ARDUINO_ONE.available() > 0) {
    FORCE_ONE = float(ARDUINO_ONE.readString());
    if (overMax()) {
      ARDUINO_ONE.write(VIBRATE_ON);
    } else {
      ARDUINO_ONE.write(VIBRATE_OFF);
    }
    ARDUINO_ONE.clear();
  }
  
  // DRAW LABELS
  textFont (LABELS);
  fill(0);
  textAlign(CENTER,CENTER);
  text("CRUTCH ONE", HORIZONTAL_LOC+barWidth/2, 
  BAR_HEIGHT-FORCE_ONE*GRAPH_SCALAR-LABEL_HEIGHT);
  
  // DRAW NUMBERS
  textFont (NUMBERS);
  fill(0);
  textAlign(CENTER,CENTER);
  text(FORCE_ONE, HORIZONTAL_LOC+barWidth/2, 
  BAR_HEIGHT-FORCE_ONE*GRAPH_SCALAR-NUMBER_HEIGHT);
  
  // DRAW BAR GRAPH #1
  rect(HORIZONTAL_LOC, BAR_HEIGHT, barWidth, -FORCE_ONE*GRAPH_SCALAR);
  
  // DRAW SCALE + LINES
  textFont (SCALE_FONT);
  fill(0);
  textAlign(BOTTOM,LEFT);
  for (int i = 0; i < 100; i+=5) {
    float y = BAR_HEIGHT-i*GRAPH_SCALAR;
    if (i%10 == 0) {
      line(10,y,20,y);
      if (i % 20 == 0) {
        text(i, 25, y);
      }
    } else if (i % 5 == 0) {
      line(10,y,15,y);
    }
  }
}

/**
 * @return the estimated amount of force on the injured limb
 */
boolean overMax() {
  return FORCE_ONE > MAX_WEIGHT;
}