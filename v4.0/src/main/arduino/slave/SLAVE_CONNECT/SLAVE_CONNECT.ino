// LIBRARIES
#include <SoftwareSerial.h>

// VARIABLES
#define FEEDBACK_INTENSITY 145

// GLOBALS
#define FEEDBACK_PIN 8
#define BLUETOOTH_RX 10
#define BLUETOOTH_TX 11
#define SERIAL_RATE 9600
#define BLUETOOTH_RATE 38400
int COUNT;
boolean VIBRATING;
SoftwareSerial CONN(BLUETOOTH_RX, BLUETOOTH_TX);

void setup() {
  // INITIATE VALUES
  COUNT = 0;
  VIBRATING = false;
  
  // SET FEEDBACK PIN
  pinMode(FEEDBACK_PIN, OUTPUT);
  digitalWrite(FEEDBACK_PIN, LOW);
  
  // BEGIN SERIAL OUTPUT
  Serial.begin(SERIAL_RATE);

  // BEGIN BLUETOOTH OUTPUT
  CONN.begin(BLUETOOTH_RATE);
}

void loop() {
  if (CONN.available() > 0) {
    byte state = CONN.read();
    feedback(state > 0);
  }

  COUNT++;
  if (COUNT % 100 == 0) {
    CONN.write(1);
  }
}

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

