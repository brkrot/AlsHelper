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
