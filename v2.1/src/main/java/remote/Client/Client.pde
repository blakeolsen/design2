// LIBRARIES
import processing.serial.*;

// CONSTANTS
private static int BLUETOOTH_RATE = 9600;
private static int FPS = 10;
private static int BACKGROUND = 255;
private static float GRAPH_SCALAR = 2.5;
private static int BAR_HEIGHT = 900;
private static int LABEL_HEIGHT = 70;
private static int NUMBER_HEIGHT = 30;

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
  /*
  ARDUINO_TWO = new Serial(this, PORT_TWO, BLUETOOTH_RATE);
  ARDUINO_TWO.bufferUntil('\n');
  */
  
  // SET CONSTANTS
  LABELS = createFont ("Serif",30);
  NUMBERS = createFont ("Serif",45);
  TITLE_FONT = createFont ("Serif",80);
  SCALE_FONT = createFont ("Serif",18);
  FPS_FONT = createFont ("Serif",20);
  FORCE_ONE = 10;
  FORCE_TWO = 20;
  USER_WEIGHT = 150;
  MAX_WEIGHT = 30;
  ESTIMATE = getEstimate();
  println("Begin iCrutch");
}

/**
 * loops with updating bar graphs
 */
void draw(){
  float barWidth = width/6;
  float x;
  boolean ONE = ARDUINO_ONE.available() > 0;
  //boolean TWO = ARDUINO_TWO.available() > 0;
  
  // CLEAR BACKGROUND
  background(BACKGROUND);
  
  // Draw Title
  textFont (TITLE_FONT);
  fill(0);
  textAlign(BOTTOM,RIGHT);
  text("iCrutch",60,120);
  
  // Draw FPS
  textFont (FPS_FONT);
  fill(0);
  textAlign(BOTTOM,LEFT);
  text("FPS: "+(int)frameRate,width-140,30);
  
  println(ONE);
  // GATHER DATA
  x = barWidth/2;
  if (ONE) {
    FORCE_ONE = float(ARDUINO_ONE.readString());
    println(FORCE_ONE);
    
    // DRAW LABELS
    textFont (LABELS);
    fill(0);
    textAlign(CENTER,CENTER);
    text("CRUTCH ONE", x+barWidth/2, 
    BAR_HEIGHT-FORCE_ONE*GRAPH_SCALAR-LABEL_HEIGHT);
    
    // DRAW NUMBERS
    textFont (NUMBERS);
    fill(0);
    textAlign(CENTER,CENTER);
    text(FORCE_ONE, x+barWidth/2, 
    BAR_HEIGHT-FORCE_ONE*GRAPH_SCALAR-NUMBER_HEIGHT);
  } else {
    // DRAW LABELS
    textFont (LABELS);
    fill(0);
    textAlign(CENTER,CENTER);
    text("DISCONNECTED", x+barWidth/2, 
    BAR_HEIGHT-FORCE_ONE*GRAPH_SCALAR-LABEL_HEIGHT);
  }
  
  // DRAW BAR GRAPH #1
  x = barWidth/2;
  rect(x, BAR_HEIGHT, barWidth, -FORCE_ONE*GRAPH_SCALAR);
  
  // DRAW BAR GRAPH #2
  x = barWidth/2*5;
  rect(x, BAR_HEIGHT, barWidth, -FORCE_TWO*GRAPH_SCALAR);
  
  // DRAW BAR GRAPH #3
  x = barWidth/2*9;
  ESTIMATE = getEstimate();
  rect(x, BAR_HEIGHT, barWidth, -ESTIMATE*GRAPH_SCALAR);
  
  // DRAW LABELS
  textFont (LABELS);
  fill(0);
  textAlign(CENTER,CENTER);
  text("ESTIMATED", x+barWidth/2, 
  BAR_HEIGHT-ESTIMATE*GRAPH_SCALAR-LABEL_HEIGHT);
  
  // DRAW NUMBERS
  textFont (NUMBERS);
  fill(0);
  textAlign(CENTER,CENTER);
  text(ESTIMATE, x+barWidth/2, 
  BAR_HEIGHT-ESTIMATE*GRAPH_SCALAR-NUMBER_HEIGHT);
  
  // DRAW SCALE + LINES
  textFont (SCALE_FONT);
  fill(0);
  textAlign(BOTTOM,LEFT);
  for (int i = 0; i < 300; i+=5) {
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
  
  if (ONE) {
    // SEND TO ARDUINO
    if (ESTIMATE > MAX_WEIGHT) {
      ARDUINO_ONE.write(VIBRATE_ON);
    } else {
      ARDUINO_ONE.write(VIBRATE_OFF);
    }
  }
}

/**
 * @return the estimated amount of force on the injured limb
 */
float getEstimate() {
  return (USER_WEIGHT - FORCE_ONE - FORCE_TWO)/2;
}