// LIBRARIES
import processing.serial.*;

// CONSTANTS
private static int FPS = 10;
private static int BACKGROUND = 255;
private static float GRAPH_SCALAR = 1.0;
private static int LABEL_HEIGHT = 420;
private static int NUMBER_HEIGHT = 440;
private static int SEPPERATE = 10;

// GLOBALS
Serial ARDUINO_ONE;
Serial ARDUINO_TWO;
String PORT_ONE = "COM5";
String PORT_TWO = "COM6";
PFont font;
float FORCE_ONE;
float FORCE_TWO;
float USER_WEIGHT;
float MAX_WEIGHT;

void setup(){
  // PREPARE THE DISPLAY
  size(500,500);
  smooth();
  frameRate(FPS);
  
  // PREPARE THE BLUETOOTH
  /*
  ARDUINO_ONE = new Serial(this, PORT_ONE, 38400);
  ARDUINO_ONE.bufferUntil('\n');
  ARDUINO_TWO = new Serial(this, PORT_TWO, 38400);
  ARDUINO_TWO.bufferUntil('\n');
  */
  
  // SET CONSTANTS
  FORCE_ONE = 10;
  FORCE_TWO = 20;
  USER_WEIGHT = 150;
  MAX_WEIGHT = 30;
}

void serialEvent(Serial CON) {
  if (CON == ARDUINO_ONE) {
    
  } else if (CON == ARDUINO_TWO) {
    
  }
}

/**
 * loops with updating bar graphs
 */
void draw(){
  // CLEAR BACKGROUND
  background(BACKGROUND);
  
  // Draw Title
  font = createFont ("Serif",50);
  textFont (font);
  fill(0);
  textAlign(BOTTOM,RIGHT);
  text("iCrutch",60,60);
  
  // Draw FPS
  font = createFont ("Serif",10);
  textFont (font);
  fill(0);
  textAlign(BOTTOM,LEFT);
  text("FPS: "+(int)frameRate,430,30);
  
  // DRAW BAR GRAPH #1
  float barWidth = width/6;
  float x = barWidth/2;
  rect(x, 400, barWidth, -FORCE_ONE*GRAPH_SCALAR);
  font = createFont ("Serif",14);
  textFont (font);
  fill(0);
  textAlign(CENTER,CENTER);
  text("CRUTCH ONE", x+barWidth/2, LABEL_HEIGHT);
  text(FORCE_ONE, x+barWidth/2, NUMBER_HEIGHT);
  
  // DRAW BAR GRAPH #2
  x = barWidth/2*5;
  rect(x, 400, barWidth, -FORCE_TWO*GRAPH_SCALAR);
  font = createFont ("Serif",14);
  textFont (font);
  fill(0);
  textAlign(CENTER,CENTER);
  text("CRUTCH TWO", x+barWidth/2, LABEL_HEIGHT);
  text(FORCE_TWO, x+barWidth/2, NUMBER_HEIGHT);
  
  // DRAW BAR GRAPH #3
  x = barWidth/2*9;
  float estimate = getEstimate();
  rect(x, 400, barWidth, -estimate*GRAPH_SCALAR);
  font = createFont ("Serif",14);
  textFont (font);
  fill(0);
  textAlign(CENTER,CENTER);
  text("ESTIMATED", x+barWidth/2, LABEL_HEIGHT);
  text(estimate, x+barWidth/2, NUMBER_HEIGHT);
  
  // DRAW SCALE + LINES
  font = createFont ("Serif",10);
  textFont (font);
  fill(0);
  textAlign(BOTTOM,LEFT);
  for (int i = 0; i < 300; i+=5) {
    float y = 400-i*GRAPH_SCALAR;
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
float getEstimate() {
  return (USER_WEIGHT - FORCE_ONE - FORCE_TWO)/2;
}