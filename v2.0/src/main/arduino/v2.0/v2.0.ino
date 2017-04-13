// LIBRARIES

// CONSTANTS
#define BAUD_RATE 9600
#define FEEDBACK_INTENSITY 145

// PINS
#define FEEDBACK_PIN 8

// GLOBAL VARIABLES
bool ON;

void setup() {
  // put your setup code here, to run once:
  pinMode(FEEDBACK_PIN, OUTPUT);

  ON = false;
  Serial.begin(BAUD_RATE);
}

void loop() {
  if (ON) {
    ON = false;
    Serial.println("OFF");
    digitalWrite(FEEDBACK_PIN, LOW);
  } else {
    ON = true;
    Serial.println("ON");
    analogWrite(FEEDBACK_PIN, FEEDBACK_INTENSITY);
  }
  delay(10000);
}
