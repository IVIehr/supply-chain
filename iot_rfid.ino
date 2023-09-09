#include <SPI.h>
#include <MFRC522.h>
#include <WiFi.h>
#include "time.h"
#include <WiFiUdp.h>
#include <NTPClient.h>
#include <HTTPClient.h>
#include <stdio.h>
#include <stdlib.h>
#include <sqlite3.h>
#include <FS.h>
#include "SPIFFS.h"
#include <ArduinoJson.h>

const char* ssid     = "WIFI_SSID";
const char* password = "WIFI_PASSWORD";
const char* thingsBoardUrl = "http://thingsBoardBasePath/api/plugins/telemetry/DEVICE/{DEVICE_ID}/values/timeseries";
const char* authUrl = "http://thingsBoardBasePath/api/auth/login";
const char* sendDataPath = "http://thingsBoardBasePath/api/v1/observer/telemetry";

// HyperLedger
const char* hyperLedgerAuthUrl = "http://HyperLedgerBasePath/users";
const char* hyperLedgerChangeState = "http://HyperLedgerBasePath/channels/mychannel/chaincodes/chaincode/asset/status/change";

String assetID = "ASSET_ID";

String token = " ";
String stateString = "";
String hyperToken = " ";
String iotId = "";
String chainType = "";

#define RST_PIN 22  // Configurable, see typical pin layout above
#define SS_PIN 21

int block_address_state = 8;
int block_address_id_1 = 9;
int block_address_id_2 = 10;
int block_address_type = 12;

MFRC522 mfrc522(SS_PIN, RST_PIN);  // Create MFRC522 instance.

MFRC522::MIFARE_Key key;


#define NUMBER_OF_STATES 6
typedef enum {
  INIT,
  PROCESS, 
  WAREHOUSE, 
  LOCAL_DELIVERY, 
  GLOBAL_DELIVERY, 
  SHOP, 
  CUSTOMER,
  END,
  UNKNOWN,
} State;

// Define a struct to represent a location
struct Location {
    double latitude;
    double longitude;
};

// Define a struct to represent a state with a location
struct Status {
    const char* name;
    struct Location location;
    byte identifier[1];
    State state;
};

struct Status process = {"PROCESS", {32.4279, 53.6880}, {1}, PROCESS };
struct Status warehouse = {"WAREHOUSE", {35.7050, 51.3917}, {2}, WAREHOUSE };
struct Status localDelivery = {"LOCAL_DELIVERY", {30.2672, 57.6742}, {3}, LOCAL_DELIVERY};
struct Status globalDelivery = {"GLOBAL_DELIVERY", {35.6993, 51.3375}, {4}, GLOBAL_DELIVERY};
struct Status shop = {"SHOP", {29.6139, 52.5388}, {5}, SHOP};
struct Status customer = {"CUSTOMER", {32.6546, 51.6676}, {6}, CUSTOMER};
byte end[1] = {9};

struct Location location;

State state;

void sendToIotServer() {

  DynamicJsonDocument doc_auth(2048);
  doc_auth["username"] = "tenant@thingsboard.org";
  doc_auth["password"] = "tenant";

  String json_auth;
  serializeJson(doc_auth, json_auth);

  WiFiClient client_auth;  // or WiFiClientSecure for HTTPS
  HTTPClient http_auth;

  http_auth.begin(client_auth, authUrl);
  http_auth.POST(json_auth);
  Serial.println("auth token from server...");
  
  DynamicJsonDocument auth_result(10096);
  DeserializationError error = deserializeJson(auth_result, http_auth.getString());

  // String token = http_auth.getString().substring(10, 582);
  String token = auth_result["token"];
  http_auth.end();


  WiFiClient client;  // or WiFiClientSecure for HTTPS
  HTTPClient http;

  http.begin(client, thingsBoardUrl);
  http.addHeader("X-Authorization", String("Bearer ") + token);
  http.GET();
  Serial.println("get state...");
  DynamicJsonDocument jsonDoc(2048);
  error = deserializeJson(jsonDoc, http.getString());
  
  if (error) {
    Serial.print(F("deserializeJson() failed with code "));
    Serial.println(error.f_str());
  }

  http.end();

}

void sendToHyperServer() {

  DynamicJsonDocument doc_auth(2048);
  doc_auth["username"] = "HL_ORG_USERNAME";
  doc_auth["password"] = "HL_ORG_PASSWORD";

  String json_auth;
  serializeJson(doc_auth, json_auth);

  WiFiClient client_auth;
  HTTPClient http_auth;

  http_auth.begin(client_auth, hyperLedgerAuthUrl);
  http_auth.addHeader("Content-Type", "application/json");

  int httpResponseCode = http_auth.POST(json_auth);

  if (httpResponseCode > 0) {
    String response = http_auth.getString();
    // Parse the JSON response
    DynamicJsonDocument doc(2048);
    DeserializationError error = deserializeJson(doc, response);

    if (error) {
    Serial.print("JSON parsing failed: ");
    Serial.println(error.c_str());
    } else {
      // Extract the "token" attribute
      const char* token = doc["token"];
      hyperToken = token;
    }
  } else {
    Serial.print("HTTP POST request failed, error: ");
    Serial.println(httpResponseCode);
  }

  http_auth.end();
}

void sendTelemtryDataToIot() {
  WiFiClient client;
  HTTPClient http;

  DynamicJsonDocument timeseries(2048);
  timeseries["id"] = iotId;
  timeseries["state"] = stateString;
  timeseries["type"] = chainType;
  timeseries["latitude"] = location.latitude;
  timeseries["longitude"] = location.longitude;

  String json_str;
  serializeJson(timeseries, json_str);

  http.begin(client, sendDataPath);
  http.POST(json_str);
  http.end();
}

void sendDataToHyper() {
  WiFiClient client;
  HTTPClient http;

  DynamicJsonDocument timeseries(2048);
  timeseries["id"] = assetID;
  timeseries["status"] = stateString;

  String json_str;
  serializeJson(timeseries, json_str);

  http.begin(client, hyperLedgerChangeState);
  http.addHeader("Content-Type", "application/json");
  http.addHeader("Authorization", String("Bearer ") + hyperToken);  
  http.POST(json_str);
  String response = http.getString();
  Serial.println(response);
  http.end();
}

State get_next_state() {
  switch(state) {
    case INIT:
    return PROCESS;
    case PROCESS:
    return WAREHOUSE;
    case WAREHOUSE:
    return LOCAL_DELIVERY;
    case LOCAL_DELIVERY:
    return GLOBAL_DELIVERY;
    case GLOBAL_DELIVERY:
    return SHOP;
    case SHOP:
    return CUSTOMER;
    case CUSTOMER:
    return END;
    
  }
}

String generate_uuid(){
  char uuid[33]; // UUID string format without hyphens is 32 characters + null terminator
  randomSeed(analogRead(0)); // Seed the random number generator

  for (int i = 0; i < 32; i++) {
    int randomValue = random(16);
    char hexDigit = (randomValue < 10) ? ('0' + randomValue) : ('a' + randomValue - 10);
    uuid[i] = hexDigit;
  }
  uuid[32] = '\0'; // Null-terminate the string

  return uuid;
}

void start_process(byte type_buffr[1]){
  byte buffr_1[16] = {0};
  byte buffr_2[16] = {0};
  byte newChain[1] = {0};
  String uuid = generate_uuid();

  Serial.println("The new chain assigned to tag successfully");
  Serial.print("related ID: ");
  Serial.println(uuid);

  for (int i = 0 ; i < 16 ; i++) {
    buffr_1[i] = (byte)uuid[i];
  }
  for (int i = 0 ; i < 16 ; i++) {
    buffr_2[i] = (byte)uuid[i + 16];
  }
  WriteDataToBlock(block_address_id_1, buffr_1);
  WriteDataToBlock(block_address_id_2, buffr_2); 
  WriteDataToBlock(block_address_state, newChain);
  WriteDataToBlock(block_address_type, type_buffr);
  Serial.println("to start the process enter \"start\"");
}

bool print_state(){
  Serial.print("*************************");
  switch(state) {
    case PROCESS:
      Serial.print("in Process");
      Serial.println("*************************");
      stateString = process.name;
      location = process.location;
      WriteDataToBlock(block_address_state, process.identifier);
      return true;
      case WAREHOUSE:
      Serial.print("stored in warehouse");
      Serial.println("*************************");
      stateString = warehouse.name;
      location = warehouse.location;
      WriteDataToBlock(block_address_state, warehouse.identifier); 
      return true;
      case LOCAL_DELIVERY:
      Serial.print("sending by local delivery");
      Serial.println("*************************");
      stateString = localDelivery.name;
      location = localDelivery.location;
      WriteDataToBlock(block_address_state, localDelivery.identifier); 
      return true;
      case GLOBAL_DELIVERY:
      Serial.print("sending by global delivery");
      Serial.println("*************************");
      stateString = globalDelivery.name;
      location = globalDelivery.location;
      WriteDataToBlock(block_address_state, globalDelivery.identifier); 
      return true;
      case SHOP:
      Serial.print("register in the shop");
      Serial.println("*************************");
      stateString = shop.name;
      location = shop.location;
      WriteDataToBlock(block_address_state, shop.identifier); 
      return true;
      case CUSTOMER:
      Serial.print("received by customer");
      Serial.println("*************************");
      stateString = customer.name;
      location = customer.location;
      WriteDataToBlock(block_address_state, customer.identifier); 
      return true;
      case END:
      Serial.print("end of chain");
      Serial.println("*************************");
      WriteDataToBlock(block_address_state, end);
      Serial.println("The chain is ended. To start a new chain, select the product type from the list below");
      Serial.println("1-dairy 2-meat&marine 3-vegtables&fruits 4-grain 5-ice cream");
      Serial.println("(enter the number in input after command \"new\" e.g: new 1)");
      return false;
      default:
      Serial.print("Unkown");
      return false;
  }
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);  // Initialize serial communications with the PC
  while (!Serial);     // Do nothing if no serial port is opened (added for Arduinos based on ATMEGA32U4)
  SPI.begin();         // Init SPI bus
  mfrc522.PCD_Init();  // Init MFRC522 card

  // Prepare the key (used both as key A and as key B)
  // using FFFFFFFFFFFFh which is the default at chip delivery from the factory
  for (byte i = 0; i < 6; i++) {
    key.keyByte[i] = 0xFF;
  }

  // Serial.println(F("Scan a MIFARE Classic PICC to demonstrate read and write."));
  // Serial.print(F("Using key (for A and B):"));
  // dump_byte_array(key.keyByte, MFRC522::MF_KEY_SIZE);
  // Serial.println();

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected.");
  Serial.println("To status check, please enter the \"start\" command in input above and scan the card");
}

void loop() {
  // Reset the loop if no new card present on the sensor/reader. This saves the entire process when idle.
    
  if ( ! mfrc522.PICC_IsNewCardPresent())
      return;

  // Select one of the cards
  // Serial.print("card received\n");
  if ( ! mfrc522.PICC_ReadCardSerial())
      return;
  // Serial.print(F("Card UID:"));
  // dump_byte_array(mfrc522.uid.uidByte, mfrc522.uid.size);
  // Serial.println();
  // Serial.print(F("PICC type: "));
  MFRC522::PICC_Type piccType = mfrc522.PICC_GetType(mfrc522.uid.sak);
  // Serial.println(mfrc522.PICC_GetTypeName(piccType));

  // Check for compatibility
  if (    piccType != MFRC522::PICC_TYPE_MIFARE_MINI
      &&  piccType != MFRC522::PICC_TYPE_MIFARE_1K
      &&  piccType != MFRC522::PICC_TYPE_MIFARE_4K) {
      Serial.println(F("This sample only works with MIFARE Classic cards."));
      return;
  }
  byte trailerBlock   = 7;
  MFRC522::StatusCode status;
  byte buffer[18];
  byte size = sizeof(buffer);
  // Serial.println(F("Authenticating using key A..."));
  status = (MFRC522::StatusCode) mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, trailerBlock, &key, &(mfrc522.uid));
  if (status != MFRC522::STATUS_OK) {
      Serial.print(F("PCD_Authenticate() failed: "));
      Serial.println(mfrc522.GetStatusCodeName(status));
      return;
  }
  // Serial.println("card authenticated successfully");

    if (Serial.available() > 0) {
      String command = Serial.readStringUntil('\n');
      if(command == "start"){
        byte readIdData_1[18];
        byte readIdData_2[18];
        byte readStateData[18];
        byte readTypeData[18];
        ReadDataFromBlock(block_address_id_1, readIdData_1);
        ReadDataFromBlock(block_address_id_2, readIdData_2);
        ReadDataFromBlock(block_address_state, readStateData);
        ReadDataFromBlock(block_address_type, readTypeData);
        String strData1 = "";
        for (int i = 0; i < 16; i++) {
          strData1 += char(readIdData_1[i]);
        }
        String strData2 = "";
        for (int i = 0; i < 16; i++) {
          strData2 += char(readIdData_2[i]);
        }
        // Concatenate the two strings
        String chainId = strData1 + strData2;
        iotId = chainId;
        Serial.print("curren chainId: ");
        Serial.println(iotId);
        int stateIdentifier = int(readStateData[0]);
        int typeIdentifier = int(readTypeData[0]);
        Serial.print("current state: ");
        switch(stateIdentifier) {
          case 0:
            state = INIT;
            Serial.println("The chain is not started yet");
            break;
          case 1:
            state = process.state;
            Serial.println(process.name);
            break;
          case 2:
            state = warehouse.state;
            Serial.println(warehouse.name);
            break;
          case 3:
            state = localDelivery.state;
            Serial.println(localDelivery.name);
            break;
          case 4:
            state = globalDelivery.state;
            Serial.println(globalDelivery.name);
            break;
          case 5:
            state = shop.state;
            Serial.println(shop.name);
            break;
          case 6:
            state = customer.state;
            Serial.println(customer.name);
            break;
          case 9:
            state = END;
            Serial.println("The chain is ended");
            break;
        }
        switch (typeIdentifier) {
          case 1:
            chainType = "dairy";
            break;
          case 2:
            chainType = "meat";
            break;
          case 3:
            chainType = "vegetable";
            break;
          case 4:
            chainType = "grain";
            break;
          case 5:
            chainType = "icecream";
            break;
          default:
            chainType = "unknown";
            break;
        }
        Serial.print("Chain product type: ");
        Serial.println(chainType);
        Serial.println("Scan the card to update states");
      } 
      else if(command.startsWith("new")){
        String newType = command.substring(4);
        byte readStateData[18];
        ReadDataFromBlock(block_address_state, readStateData);
        int stateIdentifier = int(readStateData[0]);
        State tagState;
        switch(stateIdentifier) {
          case 1:
            tagState = process.state;
            break;
          case 2:
            tagState = warehouse.state;
            break;
          case 3:
            tagState = localDelivery.state;
            break;
          case 4:
            tagState = globalDelivery.state;
            break;
          case 5:
            tagState = shop.state;
            break;
          case 6:
            tagState = customer.state;
            break;
          case 9:
            tagState = END;
            break;
          default:
            tagState = UNKNOWN;
            break;
        }
        if((tagState == END || tagState == UNKNOWN) && newType){
          int intType = newType.toInt();
          byte type_buffr[1] = {0};
          switch(intType) {
          case 1:
            type_buffr[0] = {1};
            break;
          case 2:
            type_buffr[0] = {2};
            break;
          case 3:
            type_buffr[0] = {3};
            break;
          case 4:
            type_buffr[0] = {4};
            break;
          case 5:
            type_buffr[0] = {5};
            break;
        }
          start_process(type_buffr);
        } else{
          // Prevent chain tampering
          Serial.println("The previous chain associated with this tag has not yet been completed. To create a new chain, you must attach a new tag.");
          Serial.println("To see the current chain info, enter \"start\"");        
        }
      }

  } else {
    state = get_next_state();
    bool success = print_state();
    if(success){
      sendTelemtryDataToIot();
      sendToIotServer();
      sendToHyperServer();
      sendDataToHyper();
    }
  }

    mfrc522.PICC_HaltA();
    // Stop encryption on PCD
    mfrc522.PCD_StopCrypto1();
}

void dump_byte_array(byte *buffer, byte bufferSize) {
    for (byte i = 0; i < bufferSize; i++) {
        Serial.print(buffer[i] < 0x10 ? " 0" : " ");
        Serial.print(buffer[i], HEX);
    }
}

MFRC522::StatusCode status;
byte bufferLen = 18;

void WriteDataToBlock(int blockNum, byte blockData[]) {
  status = mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, blockNum, &key, &(mfrc522.uid));
  if (status != MFRC522::STATUS_OK){
    Serial.println(String("Authentication failed for Write: ") + String(mfrc522.GetStatusCodeName(status)));
    return;
  }

  status = mfrc522.MIFARE_Write(blockNum, blockData, 16);
  if (status != MFRC522::STATUS_OK){
    Serial.println(String("Writing to Block failed: ") + String(mfrc522.GetStatusCodeName(status)));
    return;
  } else {
    Serial.println("Data was written into Block successfully");
  }
  
}

void ReadDataFromBlock(int blockNum, byte readBlockData[]) {
  /* Authenticating the desired data block for Read access using Key A */
  byte status = mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, blockNum, &key, &(mfrc522.uid));

  if (status != MFRC522::STATUS_OK){
    Serial.println(String("Authentication failed for Read: "));
    return;
  }

  /* Reading data from the Block */
  status = mfrc522.MIFARE_Read(blockNum, readBlockData, &bufferLen);
  if (status != MFRC522::STATUS_OK){
    Serial.println(String("Reading failed: "));
    return;
  }
  //  else {
  //   Serial.println("Block was read successfully");
  // }
}