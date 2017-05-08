// LIBRARIES
#include <SoftwareSerial.h>
#include <HX711.h>

// VARIABLES
#define NUMBER_READINGS 5
#define FEEDBACK_INTENSITY 145
#define CALIBRATION_FACTOR 4793
#define OFFSET 43137

// GLOBALS
#define CLOCK_PIN 2
#define SENSOR_PIN 3
#define FEEDBACK_PIN 8
#define BLUETOOTH_RX 10
#define BLUETOOTH_TX 11
#define SERIAL_RATE 9600
#define BLUETOOTH_RATE 38400
boolean VIBRATING;
SoftwareSerial CONN(BLUETOOTH_RX, BLUETOOTH_TX);
HX711 SCALE(SENSOR_PIN, CLOCK_PIN);

void setup() {
  // INITIATE VALUES
  VIBRATING = false;
  
  // SET FEEDBACK
  pinMode(FEEDBACK_PIN, OUTPUT);
  digitalWrite(FEEDBACK_PIN, LOW);

  // SET FORCE SENSOR
  SCALE.set_scale(CALIBRATION_FACTOR);
  SCALE.set_offset(OFFSET);
  
  // BEGIN SERIAL OUTPUT
  Serial.begin(SERIAL_RATE);

  // BEGIN BLUETOOTH OUTPUT
  CONN.begin(BLUETOOTH_RATE);
  connect();
}

void loop() {
  float self = SCALE.get_value(NUMBER_READINGS);
  CONN.println(self);
  if (CONN.available() > 0) {
    feedback(CONN.read());
  }
}

/* toggles the feedback on or off
 *  true - if the feedback is to be turned on
 *  false - if the feedback is to be turned off
 */
void feedback(boolean on) {
  if (on && !VIBRATING) {
    analogWrite(FEEDBACK_PIN, FEEDBACK_INTENSITY);
    Serial.println("ON");
    VIBRATING = true;
  } else if (!on && VIBRATING) {
    digitalWrite(FEEDBACK_PIN, LOW);
    Serial.println("OFF");
    VIBRATING = false;
  }
}

/* connects to the master module
 */
void connect() {
  while (true) {
    if (CONN.available() > 0 && CONN.read()) {
      CONN.write(1);
      CONN.flush();
      return;
    }
    delay(10);
  }
}

