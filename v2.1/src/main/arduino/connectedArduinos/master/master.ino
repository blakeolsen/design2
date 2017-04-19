
// LIBRARIES
#include <SoftwareSerial.h>
#include <HX711.h>

// CONSTANTS
#define SERIAL_RATE 9600
#define BLUETOOTH_RATE 9600
#define FEEDBACK_INTENSITY 145
#define CALIBRATION_FACTOR 4800
#define OFFSET 29709
#define FORCE_READINGS 20
#define VIBRATE_ON 'I'
#define VIBRATE_OFF 'O'

// PINS
#define CLOCK_PIN 2
#define SENSOR_PIN 3
#define BLUETOOTH_RX 10
#define BLUETOOTH_TX 11
#define FEEDBACK_PIN 4

// GLOBALS
boolean VIBRATING;
SoftwareSerial BLUETOOTH(BLUETOOTH_RX, BLUETOOTH_TX);
HX711 SCALE(SENSOR_PIN, CLOCK_PIN);
double FORCE_MASTER;
int FORCE_SLAVE;

void setup() {
  // START BLUETOOTH
  BLUETOOTH.begin(BLUETOOTH_RATE);
  
  // SET FORCE SENSOR
  SCALE.set_scale(CALIBRATION_FACTOR);
  SCALE.set_offset(OFFSET);
  
  // SET HAPTIC FEEDBACK
  pinMode(FEEDBACK_PIN, OUTPUT);
  digitalWrite(FEEDBACK_PIN, LOW);

  // SET GLOBAL VARIABLES
  VIBRATING = false;
  FORCE_MASTER = 0;
  FORCE_SLAVE = 0;
  
  Serial.begin(SERIAL_RATE); // Default communication rate of the Bluetooth module
  Serial.println("Begin Reading");
}

void loop() {
  FORCE_MASTER = SCALE.get_units(FORCE_READINGS);
  if (BLUETOOTH.available()) {
    char c = BLUETOOTH.read();
    FORCE_SLAVE = (int) c;
  }
  
  if (excessiveLoad()) {
    byte vibrate = BLUETOOTH.read();
    if (vibrate == VIBRATE_ON && !VIBRATING) {
      analogWrite(FEEDBACK_PIN, FEEDBACK_INTENSITY);
      VIBRATING = true;
      Serial.println("FEEDBACK ON");
    } else if (vibrate == VIBRATE_OFF && VIBRATING) {
      digitalWrite(FEEDBACK_PIN, LOW);
      VIBRATING = false;
      Serial.println("FEEDBACK OFF");
    }
  }
}

boolean excessiveLoad() {
  return (USER_WEIGHT-FORCE_MASTER-FORCE_SLAVE)/2 > MAX_WEIGHT;
}

