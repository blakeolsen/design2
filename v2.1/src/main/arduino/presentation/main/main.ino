
// LIBRARIES
#include <HX711.h>

// CONSTANTS
#define SERIAL_RATE 9600
#define FEEDBACK_INTENSITY 145
#define CALIBRATION_FACTOR 4800
#define OFFSET 29709
#define FORCE_READINGS 5
#define VIBRATE_ON 'I'
#define VIBRATE_OFF 'O'
#define MAX_LOAD 30
#define MIN_FORCE 5
#define PRINT_FREQUENCY 10

// PINS
#define CLOCK_PIN 2
#define SENSOR_PIN 3
#define FEEDBACK_PIN 8

// GLOBALS
boolean VIBRATING;
HX711 SCALE(SENSOR_PIN, CLOCK_PIN);
double FORCE;

void setup() {
  // SET FORCE SENSOR
  SCALE.set_scale(CALIBRATION_FACTOR);
  SCALE.set_offset(OFFSET);
  
  // SET HAPTIC FEEDBACK
  pinMode(FEEDBACK_PIN, OUTPUT);
  digitalWrite(FEEDBACK_PIN, LOW);

  // SET GLOBAL VARIABLES
  VIBRATING = false;
  FORCE = 0;
  
  Serial.begin(SERIAL_RATE); // Default communication rate of the Bluetooth module
}

void loop() {
  FORCE = SCALE.get_units(FORCE_READINGS);
  if (FORCE <= MIN_FORCE) {
    FORCE = 0;
  }
  Serial.println(FORCE);
  analogWrite(FEEDBACK_PIN, FEEDBACK_INTENSITY);
/*
  if (FORCE > MAX_LOAD && !VIBRATING) {
    analogWrite(FEEDBACK_PIN, FEEDBACK_INTENSITY);
    VIBRATING = true;
  } else if (FORCE < MAX_LOAD && VIBRATING) {
    digitalWrite(FEEDBACK_PIN, LOW);
    VIBRATING = false;
  }
  */
}


