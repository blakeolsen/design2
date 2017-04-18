import processing.serial.*;
Serial myPort;
String ledStatus="LED: OFF";


void setup(){
  for (String port : Serial.list()) {
    println(port);
  }
  String BLUETOOTH_PORT = Serial.list()[2];
  myPort = new Serial(this, BLUETOOTH_PORT, 38400); // Starts the serial communication
  myPort.bufferUntil('\n'); // Defines up to which character the data from the serial port will be read. The character '\n' or 'New Line'
}

void serialEvent (Serial myPort){ // Checks for available data in the Serial Port
  ledStatus = myPort.readStringUntil('\n'); //Reads the data sent from the Arduino (the String "LED: OFF/ON) and it puts into the "ledStatus" variable
}

void draw(){
  String val = "";
  if ( myPort.available() > 0) 
  {  // If data is available,
    val = myPort.readStringUntil('\n');         // read it and store it in val
  } 
  println(val); //print it out in the console
}