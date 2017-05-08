
// LIBRARIES
#include <SoftwareSerial.h>
#include <HX711.h>

// CONSTANTS
#define ARDUINO_RATE 9600
#define BLUETOOTH_RATE 38400
#define FEEDBACK_INTENSITY 145
#define CALIBRATION_FACTOR 285
#define OFFSET 163549
#define FORCE_READINGS 20

// PINS
#define CLOCK_PIN 2
#define SENSOR_PIN 3
#define BLUETOOTH_RX 4
#define BLUETOOTH_TX 5
#define FEEDBACK_PIN 8

// GLOBALS
int FEEDBACK_STATE;
SoftwareSerial BLUETOOTH(BLUETOOTH_RX, BLUETOOTH_TX);
HX711 SCALE(SENSOR_PIN, CLOCK_PIN);

void setup() {
  // SET BLUETOOTH PINS
  pinMode(BLUETOOTH_RX, INPUT);
  pinMode(BLUETOOTH_TX, OUTPUT);
  BLUETOOTH.begin(BLUETOOTH_RATE);
  
  // SET FORCE SENSOR
  SCALE.set_scale(CALIBRATION_FACTOR);
  SCALE.set_offset(OFFSET);
  
  // SET HAPTIC FEEDBACK
  pinMode(FEEDBACK_PIN, OUTPUT);
  digitalWrite(FEEDBACK_PIN, LOW);

  Serial.println("TESTING");
  Serial.begin(ARDUINO_RATE); // Default communication rate of the Bluetooth module
}
void loop() {
  double force = SCALE.get_units(FORCE_READINGS);
  if (force > 0) {
    analogWrite(FEEDBACK_PIN, FEEDBACK_INTENSITY);
  }
  Serial.println(force);
  BLUETOOTH.println(force);

  delay(100);
}
