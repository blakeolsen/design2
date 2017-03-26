/**
   iCrutch Product Code

   this code uses data from the the force sensor to model the amount of
   force applied to the user of the crutch

   The Circuit:
    - force sensor - digital pin 3
    - led light - digital pin 4
*/

#include <HX711.h>

// Sensor Pins
const int CLOCK_PIN = 2;
const int SENSOR_PIN = 3;
const int LED_PIN = 4;

// Constants
const int MAX_WEIGHT = 500;
const float calibration_factor = -7050;

// Tools
boolean TOO_HEAVY;
double INITIAL_FORCE;
int CYCLE_COUNT;
HX711 scale(SENSOR_PIN, CLOCK_PIN);


/**
 * setup
 * def: prepare the circuit for running by setting the:\
 *   - sensor pin
 */
void setup() {
  // Find the Steady-state force applied
  scale.set_scale();
  scale.tare();

  TOO_HEAVY = false;

  Serial.begin(9600); // bits per second, ranges from 300 to 115200
  pinMode(LED_PIN, OUTPUT);
}

void loop() {
  delay(10000);
  if (scale.is_ready()) {
    scale.tare();
    Serial.print("Offset: ");
    Serial.print(scale.get_offset());
    Serial.print("\n");
    
    Serial.print("Initial Force: ");
    Serial.print(scale.get_units());
    Serial.print("\n");
    
    Serial.print("Begin Sensing");
    Serial.print("\n");
    begin_reading();
  }
}

void begin_reading() {
  while(1) {
    double force = scale.get_units();
    Serial.println(force);
    
    if ((force > MAX_WEIGHT) && !TOO_HEAVY) {
      Serial.println("Exceeded Suggested Weight");
      TOO_HEAVY = true;
    } else if ((force < MAX_WEIGHT) && TOO_HEAVY) {
      Serial.println("Within Correct Weight Range");
      TOO_HEAVY = false;
    }
  
    CYCLE_COUNT++;
    if (CYCLE_COUNT % 100 == 0) {
      Serial.println("Heartbeat");
    }
    
    delay(200);
  }
}

/**
 * getForce
 * def: determines the amount of force applied by the user
 */
double getForce() {
  return 0.0;
}

