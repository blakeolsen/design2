// LIBRARIES
#include <SoftwareSerial.h>
#include <HX711.h>

// VARIABLES
#define USER_WEIGHT 150
#define MAX_LOAD 50
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
#define DATA_SIZE 4;
#define DELIMITER '\n'
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
  float other = readFloat();
  float self = SCALE.get_value(NUMBER_READINGS);
  float estimate = getEstimate(other, self);
  Serial.println(estimate);
  sendBoolean(estimate > MAX_LOAD);
  feedback(estimate > MAX_LOAD);
}

/* estimates the load on the injured limb of the individual
 */
float getEstimate(float crutchOne, float crutchTwo) {
  return USER_WEIGHT - crutchOne - crutchTwo;
}

/* sends a byte signaling whether the user is exceeding the weight
 */
void sendBoolean(boolean b) {
  CONN.write(b);
}

/* read the bluetooth buffer until a 'float' can be read
 */
float readFloat() {
  String msg = "";
  int count = 0;
  while (true) {
    if (!(CONN.available() > 0)) { // wait for more data to come in
      count = 0;
      delay(10);
      continue;
    }

    char recieved = CONN.read();
    if (count >= 4) { // end of the msg
      if (recieved == DELIMITER) {
        Serial.println(msg.toFloat());
        return msg.toFloat();
      } else { // failed to get end delimiter
        msg = "";
        count = 0;
      }
    } else {
      msg += recieved;
      count++;
    }
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

/* connects to the slave module
 */
void connect() {
  while (true) {
    CONN.write(1);
    if (CONN.available() > 0 && CONN.read()) {
      CONN.flush();
      return;
    }
    delay(10);
  }
}

