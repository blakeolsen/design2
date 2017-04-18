import processing.serial.*;
Serial ARDUINO;
String ledStatus="LED: OFF";

int in;

void setup(){
  size(200,200);
  printArray(Serial.list());
  String BLUETOOTH_PORT = Serial.list()[5];
  println(BLUETOOTH_PORT);
  ARDUINO = new Serial(this, BLUETOOTH_PORT, 38400);
  print("ARDUINO available");
   print(ARDUINO.available());
  in = 0;

  ARDUINO.bufferUntil('\n'); // Defines up to which character the data from the serial port will be read. The character '\n' or 'New Line'
}

void serialEvent (Serial ARDUINO){ // Checks for available data in the Serial Port
  in = ARDUINO.read(); 
  println(in);
}

void draw(){
  background(0);
  text("READ: "+in,10,10);
   while (ARDUINO.available() > 0) {
    int inByte = ARDUINO.read();
    println(inByte);
  }
}