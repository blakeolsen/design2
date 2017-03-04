/**
   iCrutch Product Code

   this code uses data from the the force sensor to model the amount of
   force applied to the user of the crutch

   The Circuit:
    - force sensor - digital pin 1
    - led light - digital pin 4
*/

//import processing.serial.*;

// Sensor Pins
const int SENSOR_PIN = 1;
const int LED_PIN = 4;

// Constants
const int MAX_WEIGHT = 0;

// Tools
boolean LED_STATE;
double INITIAL_FORCE;

/**
 * setup
 * def: prepare the circuit for running by setting the:\
 *   - sensor pin
 */
void setup() {
  // Zero Out the LED
  LED_STATE = false;
  digitalWrite(LED_PIN, LOW);

  // Find the Steady-state force applied
  INITIAL_FORCE = (double) analogRead(SENSOR_PIN);

  Serial.begin(9600); // bits per second, ranges from 300 to 115200
  pinMode(LED_PIN, OUTPUT);
  Serial.print("Initital Force: ");
  Serial.print(INITIAL_FORCE);
}

void loop() {
  
  
  delay(100);
  double force = getForce();
  Serial.println(force);
  if ((force > MAX_WEIGHT) && !LED_STATE) {
    Serial.println("Exceeded Suggested Weight");
    LED_STATE = true;
    digitalWrite(LED_PIN, HIGH);
  } else if ((force < MAX_WEIGHT) && LED_STATE) {
    Serial.println("Within Correct Weight Range");
    LED_STATE = false;
    digitalWrite(LED_PIN, LOW);
  }
}

/**
 * getForce
 * def: determines the amount of force applied by the user
 */
double getForce() {
  return (double) analogRead(SENSOR_PIN) - INITIAL_FORCE;
}

