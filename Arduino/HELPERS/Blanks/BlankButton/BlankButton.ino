/*This code is written especially for #PATIENT_NAME*/

/********************* Includes ***************************/
#include <IRremote.h>
#include <EEPROM.h>
#include <AceButton.h>
using namespace ace_button;
/*********************Constants*********************************/

#define DEBUG 1
#define FUNC_TAGS 1


#define LCD_DELAY_TIME 5000

/*********************Ports*********************************/

#define BUZZER 7
#define LEFT_PUSH_BUTTON 12
#define RIGHT_PUSH_BUTTON 9
#define RELAY_LIGHT 8

// The pin number attached to the button.
const int BUTTON_PIN = A1;


/****************Variables*********************************/

AceButton button(BUTTON_PIN);
unsigned long lastTime = 0;
unsigned long currentTime = 0;
int xData = 0;
int yData = 0;
char currentDirection = 'N';
boolean setupMode = false;
int leftButtonVal = 0;
int rightButtonVal = 0;

void handleEvent(AceButton*, uint8_t, uint8_t);

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
  The function beeps
  @param times is the number off beeps we hear.
*/
void beepSOS() {
  for (int i = 0; i < 20; i++) {
    digitalWrite(BUZZER, HIGH);
    delay(200);
    digitalWrite(BUZZER, LOW);
    delay(200);
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

  pinMode(LEFT_PUSH_BUTTON, INPUT);
  pinMode(BUZZER, OUTPUT);
  /*Setting up some of the peripheral files*/
  setupEEPROM();
  setupLcd();
  setupAirCondition();
  setupRelayLight();


  // Button uses the built-in pull up register.
  pinMode(BUTTON_PIN, INPUT_PULLUP);

  ButtonConfig* buttonConfig = button.getButtonConfig();
  buttonConfig->setEventHandler(handleEvent);
  buttonConfig->setFeature(ButtonConfig::kFeatureDoubleClick);
  buttonConfig->setFeature(
    ButtonConfig::kFeatureSuppressClickBeforeDoubleClick);
  buttonConfig->setFeature(ButtonConfig::kFeatureSuppressAfterClick);
  buttonConfig->setFeature(ButtonConfig::kFeatureSuppressAfterDoubleClick);


#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***ComplexArduinoCode.setup - end"));
#endif
}
/****************Main Code ********************************
  The next functions are the four functions coordinats with the Joystick
*/

void oneClick() {
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***ComplexArduinoCode.up - begin"));
#endif
  //$1
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***ComplexArduinoCode.up - end"));
#endif
}

void doubleClick() {
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***ComplexArduinoCode.right - begin"));
#endif
  //$2
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***ComplexArduinoCode.right - end"));
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
  if (digitalRead(LEFT_PUSH_BUTTON) == 1) {
    lastTime = currentTime;
    while (millis() - lastTime < 3000 && digitalRead(LEFT_PUSH_BUTTON) == 1) {
    }
    //What is the reason we left the loop? if button is still pressed so its good, lets go into edit mode
    if (digitalRead(LEFT_PUSH_BUTTON) == 1) {
      writeLcd("SETUP MODE");
      beep(6);    //Beep 6 times to let the user know we are in edit mode
      setupMode = true; //Setting the setMode to true so the screen won't turn off during edit mode
      editCommand();
      writeLcd("SETUP End");
      setupMode = false;
    }
  }

  /*This code is turning off the screen if there was no action for more than @parameter LCD_DELAY_TIME*/
  currentTime = millis();
  if (currentTime - lastTime >= LCD_DELAY_TIME && !setupMode) {
    sleepLcd();
    //currentDirection = 'N';
  }

  // Should be called every 20ms or faster for the default debouncing time
  // of ~50ms.
  button.check();

}

// The event handler for the button.
void handleEvent(AceButton* /* button */, uint8_t eventType,
                 uint8_t /* buttonState */) {
  switch (eventType) {
    case AceButton::kEventClicked:
    case AceButton::kEventReleased:
      beep(2);
      writeLcd("Click");
      oneClick();

      break;
    case AceButton::kEventDoubleClicked:
      beep(4);
      writeLcd("Double Click");
      doubleClick();
      break;
  }
}




/*********************Constants*********************************/
#define IR_ON_SIZE 0
#define IR_ON_ADDRESS 2
#define IR_OFF_SIZE 512
#define IR_OFF_ADDRESS 514

/*********************Ports*********************************/

IRsend irsend;
int khz = 38; // 38kHz carrier frequency for the NEC protocol
int restoredIR_ArrayLength;
int savedIrONLength;
int savedIrOFFLength;
unsigned int* savedIrON;
unsigned int* savedIrOFF;

bool airConditionIsOn = false;
/**
    Setup all the needs
*/
void setupAirCondition() {
#if DEBUG==1
  Serial.println(F("Debugging mode in AirCondition:"));
#endif
  savedIrON = NULL;
  savedIrOFF = NULL;
  savedIrONLength = EEPROM.read(IR_ON_SIZE);
  savedIrOFFLength = EEPROM.read(IR_OFF_ADDRESS);
}


/**
    Print the result (second argument) as Pronto Hex on the Stream supplied as argument.
    @param state tells us whether we wnat to turn ON (state=1) or OFF (state=2)
*/
void turnAC() {
  int state = -1;
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***AirConditionHelper.turnAC - begin"));
#endif
#if DEBUG==1
  printEeprom();
#endif
  /*this part is for 2 actions in one click turn ac on if now its on and opposite*/
  if (airConditionIsOn) {
    state = 0;
    airConditionIsOn = false;
  } else {
    state = 1;
    airConditionIsOn = true;
  }

  //temp variables
  unsigned int * arrPointer ;
  int arrSize;
  int dataAddressInMemory;

  if (state == 1) { //ON
    arrPointer = savedIrON;
    arrSize = savedIrONLength;
    dataAddressInMemory = IR_ON_ADDRESS;
  } else {   //OFF
    arrPointer = savedIrOFF;
    arrSize = savedIrOFFLength;
    dataAddressInMemory = IR_OFF_ADDRESS;
  }


  //In case of a new ir Code we need to get its length and to read the memory with the data
  if (arrPointer == NULL) {
    arrPointer = new unsigned int[arrSize];


    //Now we need the original pointers to hold the right adress
    if (state == 1) { //ON
      savedIrON = arrPointer ;
    } else {   //OFF
      savedIrOFF = arrPointer;
    }

    //Reading the data into our allocated array - arrPointer
    readIntArray(dataAddressInMemory, arrPointer, arrSize);
  }
#if DEBUG==1
  if (state == 1) {
    Serial.print(F("savedIrON ["));
  } else {
    Serial.print(F("savedIrOFF ["));
  }
  Serial.print(arrSize);
  Serial.println(F("] = {"));
  for (int i = 0; i < arrSize; i++) {
    Serial.print(arrPointer[i]);
    Serial.print(",");
  }
  Serial.println("}");
#endif
  irsend.sendRaw(arrPointer, arrSize, khz); // Note the approach used to automatically calculate the size of the array.


  arrPointer = NULL;
  arrSize = -1;
  dataAddressInMemory = -1;

#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***AirConditionHelper.turnAC - end"));
#endif
}

void editCommand() {
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***AirConditionHelper.editCommand - begin"));
#endif


  writeLcd(F("R-on L - off"));

  do {
    leftButtonVal = digitalRead(LEFT_PUSH_BUTTON);
    rightButtonVal = digitalRead(RIGHT_PUSH_BUTTON);
  }
  while (leftButtonVal == 0 && rightButtonVal == 0);
  beep(2);
  if (rightButtonVal == 1) {
    writeLcd("Right - on");
    delete savedIrON;
    savedIrON = NULL;
  } else {
    writeLcd("Left - off");
    delete savedIrOFF;
    savedIrOFF = NULL;
  }


#if DEBUG==1
  Serial.println(F("The chozen dir to edit is:"));
  if (rightButtonVal == 1) {
    Serial.println(F("Right side - On - command"));
  } else {
    Serial.println(F("Left - Off - command"));
  }
#endif

  delay(2000);
  setupRec();
  if (rightButtonVal == 1) {
    recordIr(1);
  } else {
    recordIr(0);
  }
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***AirConditionHelper.editCommand - end"));
#endif
}




#include <EEPROM.h>


#define EEPROM_SIZE 1024


void setupEEPROM() {
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***EEPROMHelper.setup - begin"));
#endif

  if (EEPROM.read(IR_ON_SIZE) == 0 && EEPROM.read(IR_OFF_SIZE) == 0) {
    setAllMemTo(0);
  }

#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***EEPROMHelper.setup - end"));
#endif
}
/**
    Read data from EEprom to a given array a.k.a. numbers
    @param address is the adress to begin reading from
    @numbers is the given array we are filling
    @param arraySize is the length od the array
*/
void readIntArray(int address, unsigned int numbers[], int arraySize)
{
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***EEPROMHelper.readIntArray - begin"));
#endif
  int addressIndex = address;
  for (int i = 0; i < arraySize; i++)
  {
    (EEPROM.get(addressIndex, numbers[i]));
    addressIndex += sizeof(int);
  }
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***EEPROMHelper.readIntArray - end"));
#endif
}

/**
    Wrote data to EEprom from a given array a.k.a. numbers
    @param address is the adress to begin write to
    @numbers is the given array to write the disk from it
    @param arraySize is the length od the array
*/
void writeIntArray(int address, unsigned int numbers[], int arraySize) {
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***EEPROMHelper.writeIntArray - begin"));
#endif
  int addressIndex = address;
  for (int i = 0; i < arraySize; i++)
  {
    EEPROM.put(addressIndex, numbers[i]);
    addressIndex += sizeof(int);
  }
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***EEPROMHelper.writeIntArray - end"));
#endif
}

/**
  Prints The EEPROM content in aorgenized way in the serial
*/
void printEeprom() {

#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***EEPROMHelper.printEeprom - begin"));
#endif
  int bytesPerRow = 32;
  int tempNum;
  Serial.print(F("\n -----------------------------------------------------"));
  Serial.println(F("EEPROM content:-------------------------------------------------------"));

  for (int i = 0; i < EEPROM_SIZE; i++) {

    //Printing the rows numbers
    if (i % bytesPerRow == 0) {
      Serial.print(F("\n  ["));
      printNumbersCenterd((i / bytesPerRow), 3);
      Serial.print(F("]:     "));
    }

    EEPROM.get(i, tempNum);
    printNumbersCenterd(tempNum, 4);
    Serial.print(F(" | "));
    i++;

  }
  Serial.print(F("\n\n"));

#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***EEPROMHelper.printEeprom - end"));
#endif
}

/**
    Setting all the EEPROM memory to be zero (This is for Debugging uses)

*/
void setAllMemTo(int num) {
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***EEPROMHelper.setMemToZero - begin"));
#endif
  for (int i = 0; i < 1024; i++) {
    EEPROM.write(i, num);
  }
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***EEPROMHelper.setMemToZero - end"));
#endif
}

/*this 2 function is only for printing the EEPROM nicely*/
void printNumbersCenterd(int number, int len) {

  int spaces = len - countNumberLength(number);
  for (int i = 0; i < spaces / 2; i++) {
    Serial.print(F(" "));
  }

  Serial.print(number);

  for (int i = 0; i < spaces / 2; i++) {
    Serial.print(F(" "));
  }
  if (spaces % 2 != 0) {
    Serial.print(F(" "));
  }
}
/*this function is only for printing the EEPROM nicely*/
int countNumberLength(int number) {
  int temp, counter = 0;
  number = number / 10;
  counter++;
  while (number > 0) {
    number = number / 10;
    counter++;
  }
  return counter;
}





bool lightIsOn = false;
/**
    Setup all the needs
*/
void setupRelayLight() {
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***RelayLightHelper.setupRelayLight - begin"));
#endif

  pinMode(RELAY_LIGHT, OUTPUT);
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***RelayLightHelper.setupRelayLight - end"));
#endif
}


/**

*/
void turnRelayLight() {
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***RelayLightHelper.turnRelayLight - begin"));
#endif
  if (lightIsOn) {
    digitalWrite(RELAY_LIGHT, LOW);
    lightIsOn = false;
  } else {
    digitalWrite(RELAY_LIGHT, HIGH);
    lightIsOn = true;
  }


  pinMode(RELAY_LIGHT, OUTPUT);
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***RelayLightHelper.turnRelayLight - end"));
#endif
}





#include <IRremote.h>

#define IR_RECEIVE_PIN 11

IRrecv irrecv(IR_RECEIVE_PIN);

//+=============================================================================
// Configure the Arduino
//
void setupRec() {
#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***acReciverHelper.setupRec - begin"));
#endif


  //#if DEBUG==1
  //  // Just to know which program is running on my Arduino
  //  Serial.println(F("START " __FILE__ " from " __DATE__));
  //#endif

  irrecv.enableIRIn();  // Start the receiver

#if DEBUG==1
  Serial.print(F("Ready to receive IR signals at pin "));
  Serial.println(IR_RECEIVE_PIN);
#endif

#if DEBUG==1 && FUNC_TAGS==1
  Serial.println(F("***acReciverHelper.setupRec - end"));
#endif
}

//+=============================================================================
// Dump out the decode_results structure.
//
void dumpCode(decode_results *results) {
  // Start declaration

#if DEBUG==1
  Serial.print("unsigned int  ");          // variable type
  Serial.print("rawData[");                // array name
  Serial.print(results->rawlen - 1, DEC);  // array size
  Serial.print("] = {");                   // Start declaration


  // Dump data
  for (unsigned int i = 1; i < results->rawlen; i++) {
    Serial.print(results->rawbuf[i] * MICROS_PER_TICK, DEC);
    if (i < results->rawlen - 1)
      Serial.print(","); // ',' not needed on last one
    if (!(i & 1))
      Serial.print(" ");
  }

  // End declaration
  Serial.println("};");  //
#endif
}

//+=============================================================================
// The repeating section of the code
//
void recordIr(int state) {    //state is the ir command we shopuld change 1 on 0 off
#if DEBUG==1
  Serial.println(F("***acReciveHelper.recordIr-start"));
#endif

  writeLcd("click the remote");
  decode_results results;        // Somewhere to store the results
  boolean isRecieving = true;
  while (isRecieving) {
    if (irrecv.decode(&results)) { // Grab an IR code `
      dumpCode(&results);           // Output the results as source code
      beep(8);
      writeLcd(F("code was recieved"));
      delay(4000);
      irrecv.disableIRIn();
      isRecieving = false;
    }
  }
#if DEBUG==1
  printEeprom();
#endif
  /*TODO:put this at the EEPROM helper*/
  int addressIndex;
  if (state == 1) {
    savedIrONLength = results.rawlen - 1; //Weird stuff in the first cell
    addressIndex = IR_ON_ADDRESS;
    EEPROM.write(IR_ON_SIZE, savedIrONLength);
  } else {
    savedIrOFFLength = results.rawlen - 1; //Weird stuff in the first cell
    addressIndex = IR_OFF_ADDRESS;
    EEPROM.write(IR_OFF_SIZE, savedIrOFFLength);
  }

  unsigned int temp;
  for (unsigned int i = 1; i < results.rawlen; i++)
  {
    temp = results.rawbuf[i] * MICROS_PER_TICK;
    EEPROM.put(addressIndex, temp);
    addressIndex += sizeof(int);
  }

#if DEBUG==1
  printEeprom();
#endif
#if DEBUG==1
  Serial.println(F("***acReciveHelper.recordIr-end"));
#endif
}




#include <Wire.h>
#include <LiquidCrystal_I2C.h>
LiquidCrystal_I2C lcd(0x27, 20, 4);


void setupLcd() {
  lcd.init();       // initialize the lcd
  lcd.backlight();  //Turn the lcd light - ON
  lcd.clear();      //Clear The text on the lcd
  lcd.print(F("Welcome to"));
  lcd.setCursor(0, 1);
  lcd.print(F("Milbat #PATIENT_NAME"));
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
