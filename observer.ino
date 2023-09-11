#include <ESP8266WiFi.h>
#include "time.h"
#include <WiFiUdp.h>
#include <NTPClient.h>
#include <ESP8266HTTPClient.h>
#include <stdio.h>
#include <stdlib.h>
#include <ArduinoJson.h>
#include "DHT.h"

const char* ssid     = "WIFI_SSID";
const char* password = "WIFI_PASSWORD";
const char* thingsBoardUrl = "http://thingsBoardBasePath/api/plugins/telemetry/DEVICE/e855e190-4baa-11ee-a12a-3da78bb70fe2/values/timeseries?keys=lightStatus,control_key,state,humidity,temperature,id,type";
const char* authUrl = "http://thingsBoardBasePath/api/auth/login";
const char* sendDataPath = "http://thingsBoardBasePath/api/v1/observer/telemetry";

// HyperLedger
const char* hyperLedgerAuthUrl = "http://HyperLedgerPath/users";
const char* hyperLedgerSetAlert = "http://HyperLedgerPath/channels/mychannel/chaincodes/chaincode/alert/set";

// Backend
const char* backendLogin = "http://BackendPath/api/auth/login?password=SKvYRPewXrDF&username=admin";
const char* backendLogs = "http://BackednPath/api/logs";

String assetID = "ASSET_ID";

String token = " ";
String hyperToken = " ";
String backendToken = " ";
String chainType;

#define CONTROL_PIN 5
#define ALERT_PIN 4
#define DHTPIN 12

#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);


void sendToIoTServer() {

  DynamicJsonDocument doc_auth(2048);
  doc_auth["username"] = "tenant@thingsboard.org";
  doc_auth["password"] = "tenant";

  String json_auth;
  serializeJson(doc_auth, json_auth);

  WiFiClient client_auth;  // or WiFiClientSecure for HTTPS
  HTTPClient http_auth;

  http_auth.begin(client_auth, authUrl);
  http_auth.POST(json_auth);
  
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
  DynamicJsonDocument jsonDoc(2048);
  error = deserializeJson(jsonDoc, http.getString());
  
  if (error) {
    Serial.print(F("deserializeJson() failed with code "));
    Serial.println(error.f_str());
  }

  http.end();

  String id = jsonDoc["id"][0]["value"];
  int alert = jsonDoc["lightStatus"][0]["value"];
  int control_key = jsonDoc["control_key"][0]["value"];
  float humidity = jsonDoc["humidity"][0]["value"];
  float temperature = jsonDoc["temperature"][0]["value"];
  String state = jsonDoc["state"][0]["value"];
  String type = jsonDoc["type"][0]["value"];
  chainType = type;

  bool control = control_key == 1;

  sendDataToHyper(alert);
  sendDataToBackend(alert == 1 ? true : false, temperature, humidity, state, id);

  delay(10);
  digitalWrite(CONTROL_PIN, alert == 1);
  digitalWrite(ALERT_PIN, control);
}

String getTelemetryData() {
  // Reading temperature or humidity takes about 250 milliseconds!
  // Sensor readings may also be up to 2 seconds 'old' (its a very slow sensor)
  float h = dht.readHumidity();
  // Read temperature as Celsius (the default)
  float t = dht.readTemperature();

  if (isnan(h) || isnan(t)) {
    Serial.println(F("Failed to read from DHT sensor!"));
    return "failure";
  }

  DynamicJsonDocument timeseries(2048);
  timeseries["temperature"] = t;
  timeseries["humidity"] = h;
  timeseries["lightStatus"] = 0;
  timeseries["type"] = chainType;

  String json_str;
  serializeJson(timeseries, json_str);

  return json_str;
}

void sendTelemtryDataToIoTServer() {
  WiFiClient client;
  HTTPClient http;
  
  String json_str = getTelemetryData();
  http.begin(client, sendDataPath);
  http.POST(json_str);
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

void sendDataToHyper(int alert) {
  WiFiClient client;
  HTTPClient http;

  DynamicJsonDocument timeseries(2048);
  timeseries["id"] = assetID;
  timeseries["alert"] = alert == 1 ? true : false;

  String json_str;
  serializeJson(timeseries, json_str);

  http.begin(client, hyperLedgerSetAlert);
  http.addHeader("Content-Type", "application/json");
  http.addHeader("Authorization", String("Bearer ") + hyperToken);  
  http.POST(json_str);
  String response = http.getString();
  Serial.print("hyper response: ");
  Serial.println(response);
  http.end();
}

void loginToBackendServer() {
  WiFiClient client;  // or WiFiClientSecure for HTTPS
  HTTPClient http;

  http.begin(client, backendLogin);
  http.GET();
  DynamicJsonDocument jsonDoc(2048);
  DeserializationError error = deserializeJson(jsonDoc, http.getString());
  
  if (error) {
    Serial.print(F("deserializeJson() failed with code "));
    Serial.println(error.f_str());
  }

  http.end();

  String token = jsonDoc["result"]["accessToken"];
  backendToken = token;
}

void sendDataToBackend(bool alert, float temperature, float humidity, String state, String cargoId) {
  WiFiClient client;
  HTTPClient http;

  DynamicJsonDocument timeseries(2048);
  timeseries["alert"] = alert;
  timeseries["humidity"] = humidity;
  timeseries["temperature"] = temperature;
  timeseries["cargoState"] = state;
  timeseries["cargoId"] = cargoId;

  String json_str;
  serializeJson(timeseries, json_str);
  Serial.print("telemetry data: ");
  Serial.println(json_str);

  http.begin(client, backendLogs);
  http.addHeader("Content-Type", "application/json");
  http.addHeader("Authorization", String("Bearer ") + backendToken);  
  http.POST(json_str);
  String response = http.getString();
  http.end();

  Serial.print("backend response: ");
  Serial.println(response);
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  dht.begin();

  pinMode(CONTROL_PIN, OUTPUT);
  digitalWrite(CONTROL_PIN, 0); 

  pinMode(ALERT_PIN, OUTPUT);
  digitalWrite(ALERT_PIN, 0); 
  /* Initialize MFRC522 Module */


  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected.");
}

void loop() {
  // put your main code here, to run repeatedly:
  loginToBackendServer();
  sendToHyperServer();
  sendToIoTServer();
  sendTelemtryDataToIoTServer();
  delay(3000);

}
