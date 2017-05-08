
// LIBRARIES
#include <SoftwareSerial.h>
#include <HX711.h>

// VARIABLES
#define FEEDBACK_INTENSITY 145
#define FORCE_READINGS 5

// CONSTANTS
#define SERIAL_RATE 9600
#define BLUETOOTH_RATE 38400
#define CALIBRATION_FACTOR 290

// PINS
#define CLOCK_PIN 2
#define SENSOR_PIN 3
#define FEEDBACK_PIN 8
#define BLUETOOTH_POWER 13
#define BLUETOOTH_RX 10
#define BLUETOOTH_TX 11

// GLOBALS
boolean VIBRATING;
double FORCE;
HX711 SCALE(SENSOR_PIN, CLOCK_PIN);
SoftwareSerial COMM(BLUETOOTH_RX, BLUETOOTH_TX);

void setup() {
  // SET FORCE SENSOR
  SCALE.set_scale(CALIBRATION_FACTOR);
  SCALE.tare(FORCE_READINGS);
  
  // SET HAPTIC FEEDBACK
  pinMode(FEEDBACK_PIN, OUTPUT);
  digitalWrite(FEEDBACK_PIN, LOW);

  // SET GLOBAL VARIABLES
  VIBRATING = false;
  FORCE = 0;
  digitalWrite(BLUETOOTH_POWER, HIGH);

  // START READING
  COMM.begin(BLUETOOTH_RATE);
  Serial.begin(SERIAL_RATE);
}

int counter = 0;

void loop() {
  FORCE = SCALE.get_units(FORCE_READINGS);
  /**
  Serial.println(FORCE);
  if (COMM) {
    int sent = COMM.print(FORCE); // send it over the wire
    COMM.print("\n");
    if (COMM.available() > 0) {
      Serial.println("RECIEVED");
      byte reading = COMM.read();
      Serial.println(reading);
      if (reading > 0 && !VIBRATING) { // start vibrating
        analogWrite(FEEDBACK_PIN, FEEDBACK_INTENSITY);
        VIBRATING = true;
      } else if (reading == 0 && VIBRATING) {
        digitalWrite(FEEDBACK_PIN, LOW);
        VIBRATING = false;
      }
    }
  }
  */
  if (counter % 15 == 14) {
    delay(50000);
  }
  uint8_t sender= 15;
  COMM.write(3);
  delay(500);
  counter++;
}


