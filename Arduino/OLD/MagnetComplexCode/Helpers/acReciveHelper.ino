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
    if (irrecv.decode(&results)) {  // Grab an IR code
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
    EEPROM.write(IR_ON_SIZE,savedIrONLength);
  } else {
    savedIrOFFLength = results.rawlen - 1; //Weird stuff in the first cell
    addressIndex = IR_OFF_ADDRESS;
    EEPROM.write(IR_OFF_SIZE,savedIrOFFLength);
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
