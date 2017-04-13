// Libraries
#include <SoftwareSerial.h>

// SETTINGS
#define BLUETOOTH_BAUDRATE 38400

// PINS
#define SENSOR_PIN 2
#define CLOCK_PIN 3
#define BLUETOOTH_TX 1
#define BLUETOOTH_RX 0

// GLOBAL VARIABLES
SoftwareSerial bluetooth(BLUETOOTH_RX,BLUETOOTH_TX);

void setup() {
  pinMode(BLUETOOTH_RX, INPUT);
  pinMode(BLUETOOTH_TX, OUTPUT);

  Serial.begin(9600);
  Serial.println("Testing Bluetooth");
  bluetooth.begin(BLUETOOTH_BAUDRATE);
}

char c = ' ';

void loop() {
    // Keep reading from HC-05 and send to Arduino Serial Monitor
    if (bluetooth.available())
    {  
        c = bluetooth.read();
        Serial.write(c);
    }
 
    // Keep reading from Arduino Serial Monitor and send to HC-05
    if (Serial.available())
    {
        c =  Serial.read();
        bluetooth.write(c);  
    }
 
}
