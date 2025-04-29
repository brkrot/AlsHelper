#include <Wire.h>
#include <LiquidCrystal_I2C.h>
LiquidCrystal_I2C lcd(0x27, 20, 4);


void setupLcd() {
	lcd.init();       // initialize the lcd
	lcd.backlight();  //Turn the lcd light - ON
	lcd.clear();      //Clear The text on the lcd
	lcd.print(F("Welcome to"));
	lcd.setCursor(0, 1);
	lcd.print(F("Milbat #Patient"));
}

void writeLcd(String text) {
	//todo - deal with strings longer than 16 chars
	lcd.backlight();
	lcd.setCursor(0, 0);
	lcd.clear();
	lcd.print(text);
}
void sleepLcd() {
	lcd.clear();
	lcd.noBacklight();
}
