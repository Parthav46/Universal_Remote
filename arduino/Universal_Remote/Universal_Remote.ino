//TODO pin d8 as data selector
// when data send low
// freq send then high
//TODO pin d6 ATMega reset pin high always
//TODO serial print freq received from query ,and serial print freq d8 high when signal and low afterwards
//TODO same for data , by default 0
#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>

const char *ssid = "rushi";
const char *password = "1122334455";

int reset_pin = D6;
int select  =  D8;

ESP8266WebServer server ( 80 );

void getfrequency(){
      String data="";
      for ( int i = 0; i < server.args(); i++ ) {
       if(server.argName ( i ) == "data")
       {
        data=server.arg(i);
       }
      }
      data = data.substring(1,data.length()-1);
      Serial.print("frequency is");
      Serial.println(data);
      digitalWrite(select,HIGH);
      server.send ( 200, "text/plain", "OK" );
}

void getdata(){
      String data="";
      for ( int i = 0; i < server.args(); i++ ) {
       if(server.argName ( i ) == "data")
       {
        data=server.arg(i);
       }
      }
      data = data.substring(1,data.length()-1);
      Serial.print("data is");
      Serial.println(data);
      digitalWrite(select,LOW);
      server.send ( 200, "text/plain", "OK" );
}

void getid(){
  String idname="node mcu";
  server.send(200,"text/plain",idname);
}

void getdevice() {
  String message = "https://sheets.googleapis.com/v4/spreadsheets/";
  String url = "";
  String key="";
  for ( int i = 0; i < server.args(); i++ ) {
       if(server.argName ( i ) == "url")
       {
        url=server.arg(i);
       }
       else if(server.argName ( i ) == "key")
       {
        key=server.arg(i);
       }
  }
    message+=url+"/values/B1%3AH1?key="+key+"&majorDimension=ROWS";
    if (WiFi.status() == WL_CONNECTED) { //Check WiFi connection status
 
    HTTPClient http;              //Declare an object of class HTTPClient
 
    http.begin("https://sheets.googleapis.com/v4/spreadsheets/15_CZXYhCdMXsj26qp584U_y1fBNpWNIITA6Tr7F3wzg/values/B1%3AH1?key=AIzaSyCY0x0DzYNwXg0feUqAPWhHs9WyzHmG0qI&majorDimension=ROWS");          //Specify request destination
    int httpCode = http.GET();    //Send the request
    String payload = http.getString();
    Serial.println(httpCode);
    Serial.println(payload);
    if (httpCode == HTTP_CODE_OK) {           //Check the returning code
 
      payload = http.getString();   //Get the request response payload
      Serial.println(payload);             //Print the response payload
 
    }
 
    http.end();                   //Close connection
 
  }
  server.send ( 200, "text/plain", message );
}

void control() {
  String message = "";
  for ( int i = 0; i < server.args(); i++ ) {
       if(server.argName ( i ) == "key")
       {
        message=server.arg(i);
       }
  }
  server.send ( 200, "text/plain", message );
}

void handleNotFound() {
    String message = "URI: ";
    message += server.uri();
    message += "\nMethod: ";
    message += ( server.method() == HTTP_GET ) ? "GET" : "POST";
    message += "\nArguments: ";
    message += server.args();
    message += "\n";
  
    for ( uint8_t i = 0; i < server.args(); i++ ) {
      message += " " + server.argName ( i ) + ": " + server.arg ( i ) + "\n";
  }
  server.send ( 404, "text/plain", message );
}

void setup() {
  Serial.begin ( 9600 );
  WiFi.mode ( WIFI_STA );
  WiFi.begin ( ssid, password );
  
  pinMode(reset_pin,OUTPUT);
  pinMode(select,OUTPUT);
  digitalWrite(reset_pin,HIGH);
  
  // Wait for connection
  while ( WiFi.status() != WL_CONNECTED ) {
    delay ( 500 );
    Serial.print ( "." );
  }

  Serial.println ( "" );
  Serial.print ( "Connected to " );
  Serial.println ( ssid );
  Serial.print ( "IP address: " );
  Serial.println ( WiFi.localIP() );

   if ( MDNS.begin ( "esp8266" ) ) {
    Serial.println ( "MDNS responder started" );
  }
  server.on("/id",getid);
  server.on("/device",getdevice);
  server.on("/control",control);
  server.on("/frequency",getfrequency);
  server.on("/data",getdata);
  
  server.onNotFound ( handleNotFound );
  server.begin();
  Serial.println ( "HTTP server started" );
}

void loop() {
  // put your main code here, to run repeatedly:
  server.handleClient();
}
