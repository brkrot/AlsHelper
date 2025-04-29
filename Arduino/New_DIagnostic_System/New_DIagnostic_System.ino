#include <SoftwareSerial.h>

#define BAUDRATE 9600
#define DEBUG 1
SoftwareSerial blue(2, 3); // bluetooth module connected here (3 is RX and 2 is TX)

#define X_PIN A1                   //Naming all the used PINs
#define Y_PIN A0
#define DUPS 5   //Data units per second
#define POTENTIOMETER_PIN A2
#define KNOCK_PIN 10
#define LED_PIN 13
#define DELAY 300

//Declaring all the vars
int xData = 0;
int yData = 0;
int knockData = 0;
int resistanceData = 0;
char menu = 0;
String input = "";


void setup() {
  pinMode(KNOCK_PIN, INPUT);
  pinMode(LED_PIN, OUTPUT);         //Determining the PINs way of work
  pinMode(X_PIN, INPUT);
  pinMode(Y_PIN, INPUT);
  pinMode(POTENTIOMETER_PIN, INPUT);

  //For debugging modes only
#if DEBUG==1
  Serial.begin(9600);                //Beginning the connection with the BT and the serial
#endif
  blue.begin(BAUDRATE);                  //TODO - We need to check if it is possible to change the baudrate to something faster
}

//Actual loop running
void loop() {
  if (blue.available() > 0) {
    input = blue.readString();       // reading the data received from the bluetooth module

    /*This is if is for checking that the command from BT was to switch sensor*/
    if (input[0] == 'S') {
      menu = input[1];
    }
#if DEBUG==1
    Serial.println("The incoming input is: " + input);
    Serial.print("The chosen Sensor is: ");
    Serial.println(menu);
#endif
  }
  switch (menu) {
    case '1'://joystick sensor
      //Serial.print("Joystick");
      // read the value at analog input
      xData = analogRead(X_PIN);   //TODO  - mapping the analog value to 8/16/32 bit https://www.arduino.cc/reference/en/language/functions/math/map/
      yData = analogRead(Y_PIN);

      //This part should take care of the data rate DUPS is the "Data Unit Per Second"
      delay(DELAY);// / DUPS ); // todo - substract the time it takes from last print until here




      /*an old version just for checking*/

      blue.print("");
      blue.print(xData);
      blue.print("#");
      blue.print(yData);
      blue.print("#");

      //      blue.print(millis() / 1000);
      //      blue.print("(");
      //      blue.print(xData);
      //      blue.print(",");
      //      blue.print(yData);
      //      blue.print(")\n");
#if DEBUG==1
      Serial.print("(");
      Serial.print(xData);
      Serial.print(",");
      Serial.print(yData);
      Serial.print(")\n");
#endif
      break;
    case '2'://binary sensor
      knockData = digitalRead(KNOCK_PIN);
      delay(DELAY);// / DUPS ); // todo - substract the time it takes from last print until here
      switch (knockData)
      {
        case HIGH: digitalWrite(LED_PIN, HIGH);   // Led\lamp is on
          break;
        case LOW: digitalWrite(LED_PIN, LOW);    // Led\lamp is off
          break;
        default : break;
      }
#if DEBUG==1
      Serial.print("Knock value is ");
      Serial.println(knockData);
#endif
      //blue.print("Knock value is ");
      blue.print("#");
      blue.print(knockData);
      break;
    case '3'://analog sensor
      resistanceData = analogRead(POTENTIOMETER_PIN);
#if DEBUG==1
      Serial.print("resistance value is ");
      Serial.println(resistanceData);
#endif
      blue.print("#");
      blue.print(resistanceData);
      delay(DELAY);// / DUPS ); // todo - substract the time it takes from last print until here
      break;
    case '9':
      //Serial.print("Joystick");
      // read the value at analog input
      xData = analogRead(X_PIN);   //TODO  - mapping the analog value to 8/16/32 bit https://www.arduino.cc/reference/en/language/functions/math/map/
      yData = analogRead(Y_PIN);

      //This part should take care of the data rate DUPS is the "Data Unit Per Second"
      delay(500);// 1000 / DUPS ); // todo - substract the time it takes from last print until here

      blue.print(millis() / 1000);
      blue.print("(");
      blue.print(xData);
      blue.print(",");
      blue.print(yData);
      blue.print(")\n");
#if DEBUG==1
      Serial.print("(");
      Serial.print(xData);
      Serial.print(",");
      Serial.print(yData);
      Serial.print(")\n");
#endif
      break;
    default:
      break;
  }

}
