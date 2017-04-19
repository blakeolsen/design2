
// LIBRARIES
//#include <SoftwareSerial.h>
#include <HX711.h>

// CONSTANTS
#define SERIAL_RATE 9600
//#define BLUETOOTH_RATE 9600
#define FEEDBACK_INTENSITY 145
#define CALIBRATION_FACTOR 4800
#define OFFSET 29709
#define FORCE_READINGS 20
#define VIBRATE_ON 'I'
#define VIBRATE_OFF 'O'
#define MAX_LOAD 40
#define MIN_FORCE 5
#define PRINT_FREQUENCY 30

// PINS
#define CLOCK_PIN 2
#define SENSOR_PIN 3
//#define BLUETOOTH_RX 10
//#define BLUETOOTH_TX 11
#define FEEDBACK_PIN 4

// GLOBALS
boolean VIBRATING;
//SoftwareSerial BLUETOOTH(BLUETOOTH_RX, BLUETOOTH_TX);
HX711 SCALE(SENSOR_PIN, CLOCK_PIN);
double FORCE;
int COUNTER;

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
  COUNTER = 0;
  
  Serial.begin(SERIAL_RATE); // Default communication rate of the Bluetooth module
  Serial.println("Begin Reading");
}

void loop() {
  FORCE = SCALE.get_units(FORCE_READINGS);
  if (FORCE <= MIN_FORCE) {
    FORCE = 0;
  }

  if (COUNTER%PRINT_FREQUENCY == 0) {
    Serial.print("FORCE MEASURED: ");
    Serial.println(FORCE);
  }
  
  if (excessiveLoad() && !VIBRATING) {
      analogWrite(FEEDBACK_PIN, FEEDBACK_INTENSITY);
      VIBRATING = true;
      Serial.println("FEEDBACK ON");
  } else if (!excessiveLoad() && VIBRATING) {
      digitalWrite(FEEDBACK_PIN, LOW);
      VIBRATING = false;
      Serial.println("FEEDBACK OFF");
  }
  COUNTER++;
}

boolean excessiveLoad() {
  return FORCE > MAX_LOAD;
}


