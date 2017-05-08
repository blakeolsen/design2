// LIBRARIES
#include <SoftwareSerial.h>
#include <HX711.h>

// VARIABLES
#define USER_WEIGHT 170
#define MAX_LOAD 70
#define MIN_THRESHOLD 10
#define CYCLES_UNTIL_NOTIFY 4
#define TRACKING 3
#define NUMBER_READINGS 3
#define FEEDBACK_INTENSITY 145
#define CALIBRATION_FACTOR 4793
#define OFFSET 43137

// GLOBALS
#define CLOCK_PIN 2
#define SENSOR_PIN 3
#define FEEDBACK_PIN 8
#define BLUETOOTH_RX 10
#define BLUETOOTH_TX 11
#define SERIAL_RATE 38400
#define BLUETOOTH_RATE 38400
#define DATA_SIZE 4;
#define DELIMITER '\n'
boolean SHOULD_VIBRATE;
boolean VIBRATING;
SoftwareSerial CONN(BLUETOOTH_RX, BLUETOOTH_TX);
HX711 SCALE(SENSOR_PIN, CLOCK_PIN);
int overWeightCycles;
int vibratedCycles;
float previous;
boolean down;

/* union to convert bytes to float
 */
typedef union {
  float number;
 uint8_t bytes[4];
} FLOATUNION_t;

void setup() {
  // INITIATE VALUES
  VIBRATING = false;
  SHOULD_VIBRATE = false;
  overWeightCycles = 0;
  vibratedCycles = 0;
  previous = 0;
  down = true;
  
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
  connectSlave();
}

void loop() {
  float other = readFloat();
  float self = SCALE.get_units(NUMBER_READINGS);
  float estimate = getEstimate(other, self);
  sendFloat(other, 'o');
  sendFloat(self, 'm');
  sendFloat(estimate, 'e');
  checkPattern(self, other, estimate);
  sendBoolean(SHOULD_VIBRATE);
  feedback(SHOULD_VIBRATE);
}

/* diagnoses whether to notify the user they are walking incorrectly
 */
void checkPattern(float slave, float master, float estimate) {
  if (estimate > previous && slave > MIN_THRESHOLD && master > MIN_THRESHOLD) { // user's maximum force was previous
    Serial.println("HERE");
    Serial.println(previous);
    if (previous > MAX_LOAD) { // overstressed foot
      Serial.println("OVERSTRESSED");
      overWeightCycles++;
      if (overWeightCycles >= CYCLES_UNTIL_NOTIFY) {
        SHOULD_VIBRATE = true;
        vibratedCycles = 0;
      }
    } else {
      if (SHOULD_VIBRATE) {
        Serial.println("UNDERSTRESSED");
        vibratedCycles++;
        overWeightCycles = 0;
        if (vibratedCycles >= CYCLES_UNTIL_NOTIFY) {
          SHOULD_VIBRATE = false;
        }
      }
    }
  }
  previous = estimate;
}

/* sends a float accross the wire
 */
void sendFloat(float value, byte identifier) {
  FLOATUNION_t myFloat;
  myFloat.number = value;
  Serial.write(DELIMITER);
  Serial.write(identifier);
  for (int i=0; i<4; i++) {
    Serial.write(myFloat.bytes[i]);
  }
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
  FLOATUNION_t myFloat;
  int count = 0;
  while (!(CONN.available() > 0 && CONN.read() == DELIMITER)) { // wait for delimiter
    delay(10);
  }

  while (count < 4) {
    if (CONN.available() > 0) {
      myFloat.bytes[count] = CONN.read();
      count++;
      continue;
    }
  }
  return myFloat.number;
}

/* toggles the feedback on or off
 *  true - if the feedback is to be turned on
 *  false - if the feedback is to be turned off
 */
void feedback(boolean on) {
  if (on && !VIBRATING) {
    analogWrite(FEEDBACK_PIN, FEEDBACK_INTENSITY);
    VIBRATING = true;
  } else if (!on && VIBRATING) {
    digitalWrite(FEEDBACK_PIN, LOW);
    VIBRATING = false;
  }
}

/* connects to the slave module
 */
void connectSlave() {
  CONN.listen();
  while (true) {
    CONN.write(1);
    if (CONN.available() > 0) {
      CONN.flush();
      return;
    }
    delay(100);
  }
}


