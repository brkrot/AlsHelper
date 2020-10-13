/*This code is written especially for #PATIENT_NAME*/

/********************* Includes ***************************/
#include <IRremote.h>
#include <EEPROM.h>

/*********************Constants*********************************/

#define DEBUG 1
#define FUNC_TAGS 1


#define LCD_DELAY_TIME 5000
#define JOYSTICK_RIGHT_THRSHOLD 800
#define JOYSTICK_LEFT_THRSHOLD 200
#define JOYSTICK_UP_THRSHOLD 800
#define JOYSTICK_DOWN_THRSHOLD 200
/*********************Ports*********************************/
/*X Y are oposite than what written on the joystick because that is the only way it makes any sense in comparison to math axis*/
#define X_PIN A1                //Naming all the used PINs
#define Y_PIN A0
#define BUZZER 7
#define PUSH_BUTTON 12



/****************Variables*********************************/
unsigned long lastTime = 0;
unsigned long currentTime = 0;
int xData = 0;
int yData = 0;
char currentDirection = 'N';
boolean setupMode = false;


/**
	The function beeps
	@param times is the number off beeps we hear.
*/
void beep(int times) {
	for (int i = 0; i < times; i++) {
		digitalWrite(BUZZER, HIGH);
		delay(50);
		digitalWrite(BUZZER, LOW);
		delay(50);
	}
}

/**
	The function setup all the basic needs for our programm
*/
void setup() {

#if DEBUG==1 && FUNC_TAGS==1
	Serial.begin(115200);
	Serial.println(F("***ComplexArduinoCode.setup - begin"));
#endif
	beep(2);    //A double beep to let the user know the system is up

	pinMode(PUSH_BUTTON, INPUT);
	pinMode(BUZZER, OUTPUT);
	/*Setting up some of the peripheral files*/
	setupEEPROM();
	setupLcd();
	setupAirCondition();
#if DEBUG==1 && FUNC_TAGS==1
	Serial.println(F("***ComplexArduinoCode.setup - end"));
#endif
}
/****************Main Code ********************************
The next functions are the four functions coordinats with the Joystick
*/

void up() {
#if DEBUG==1 && FUNC_TAGS==1
	Serial.println(F("***ComplexArduinoCode.up - begin"));
#endif
//$1
#if DEBUG==1 && FUNC_TAGS==1
	Serial.println(F("***ComplexArduinoCode.up - end"));
#endif
}

void right() {
#if DEBUG==1 && FUNC_TAGS==1
	Serial.println(F("***ComplexArduinoCode.right - begin"));
#endif
	//$2
#if DEBUG==1 && FUNC_TAGS==1
	Serial.println(F("***ComplexArduinoCode.right - end"));
#endif
}

void down() {
#if DEBUG==1 && FUNC_TAGS==1
	Serial.println(F("***ComplexArduinoCode.down - begin"));
#endif
	//$3
#if DEBUG==1 && FUNC_TAGS==1
	Serial.println(F("***ComplexArduinoCode.down - end"));
#endif
}

void left() {
#if DEBUG==1 && FUNC_TAGS==1
	Serial.println(F("***ComplexArduinoCode.left - begin"));
#endif
	//$4
#if DEBUG==1 && FUNC_TAGS==1
	Serial.println(F("***ComplexArduinoCode.left - end"));
#endif
}

void loop()
{
	/*Here we deal with the setup button:
	this is the main idea:
	When the button val is high, start a loop (busy wait) -
	(we are assuming that if the user wants to get edit mode,
	he doesnt care about the programm not doing other stuff right now)
	you keep the while going if the following hppans:
	button is still pressed (HIGH) && there were less then 3 sec from the initial button pressed.

	*/
	if (digitalRead(PUSH_BUTTON) == 1) {
		lastTime = currentTime;
		while (millis() - lastTime < 3000 && digitalRead(PUSH_BUTTON) == 1) {
		}
		//What is the reason we left the loop? if button is still pressed so its good, lets go into edit mode
		if (digitalRead(PUSH_BUTTON) == 1) {
			writeLcd("SETUP MODE");
			beep(6);		//Beep 6 times to let the user know we are in edit mode
			setupMode = true;	//Setting the setMode to true so the screen won't turn off during edit mode
			editCommand();
			writeLcd("SETUP End");
			setupMode = false;
		}
	}

	/*This code is turning off the screen if there was no action for more than @parameter LCD_DELAY_TIME*/
	currentTime = millis();
	if (currentTime - lastTime >= LCD_DELAY_TIME && !setupMode) {
		sleepLcd();
		currentDirection = 'N';
	}


	/*Joystick deal with*/
	xData = analogRead(X_PIN);
	yData = analogRead(Y_PIN);
/*#if DEBUG==1
	Serial.print(F("("));
	  Serial.print(xData);
	  Serial.print(F(","));
	  Serial.print(yData);
	  Serial.println(F(")"));
#endif*/


					/*todo - change the way the joystick is diff from each other
					in our way now we have overlaps!!

						|         |
				   L/U  |    U    |    U/R
				________|_________|_________
						|         |
						|    X    |      R
						|         |
				________|_________|__________
						|         |
				  L/D	|    D    |     D/R
						|         |
			PROPORTIONS ARE NOT EXACT because /,\ are not 45 deg
              _______________________________
			  |  	\                /      |
			  |	     \      U       /       |
			  |  	  \            /        |
			  | 	   \          /         |
			  | 		\________/          |
			  |			|        |          |
			  |	 L      |   X    |      R   |
			  |		    |        |          |
			  |	     	|________|          |
			  |	       /          \         |
			  |	      /      D     \        |
			  |	     /	            \       |
			  |_____/________________\______|
						  */
	if (!setupMode) {     //Check the Joystick status only if you are not in setup mode
		if (xData > JOYSTICK_RIGHT_THRSHOLD /*&& xData > yData*/ && currentDirection != 'R') {
			lastTime = currentTime;
			beep(2);
			currentDirection = 'R';
			writeLcd("Right");
			right();
		}
		else if (xData < JOYSTICK_LEFT_THRSHOLD /*&& xData < yData*/ && currentDirection != 'L') {
			lastTime = currentTime;
			beep(2);
			currentDirection = 'L';
			writeLcd("Left");
			left();
		}
		else if (yData > JOYSTICK_UP_THRSHOLD /*&& yData > xData*/ && currentDirection != 'U') {
			lastTime = currentTime;
			beep(2);
			currentDirection = 'U';
			writeLcd("Up");
			up();
		}
		else if (yData < JOYSTICK_DOWN_THRSHOLD /*&& yData < xData*/ && currentDirection != 'D') {
			lastTime = currentTime;
			beep(2);
			currentDirection = 'D';
			writeLcd("Down");
			down();
		}
	}
}