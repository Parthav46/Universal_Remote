#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>

const char *ssid = "NETGEAR300";
const char *password = "home29391";

#define reset_pin  D6
#define line  D8

ESP8266WebServer server ( 80 );

void getfrequency(){
      digitalWrite(line,HIGH);
      String data="";
      delay(50);
      for ( int i = 0; i < server.args(); i++ ) {
       if(server.argName ( i ) == "data")
       {
        data=server.arg(i);
       }
      }
      data = data.substring(1,data.length()-1);
      Serial.println(data);
      delay(100);
      digitalWrite(line,LOW);
      server.send ( 200, "text/plain", "OK" );
}

void getdata(){
      String data="";
      digitalWrite(line,LOW);
      for ( int i = 0; i < server.args(); i++ ) {
       if(server.argName ( i ) == "data")
       {
        data=server.arg(i);
       }
      }
      data = data.substring(1,data.length()-1);
      Serial.println(data);
      server.send ( 200, "text/plain", "OK" );
}

void getid(){
  String idname="node mcu";
  server.send(200,"text/plain",idname);
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
  pinMode(line,OUTPUT);
  digitalWrite(line,LOW);
  digitalWrite(reset_pin,HIGH);
  
  // Wait for connection
  while ( WiFi.status() != WL_CONNECTED ) {
    delay ( 100 );
  }
  
  Serial.println ( WiFi.localIP() );

   if ( MDNS.begin ( "esp8266" ) ) ;
  server.on("/id",getid);
  server.on("/frequency",getfrequency);
  server.on("/data",getdata);
  
  server.onNotFound ( handleNotFound );
  server.begin();
}

void loop() {
  // put your main code here, to run repeatedly:
  server.handleClient();
}
