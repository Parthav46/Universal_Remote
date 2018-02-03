#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>

const char *ssid = "NETGEAR300";
const char *password = "home29391";

ESP8266WebServer server ( 80 );

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
  Serial.begin ( 115200 );
  WiFi.mode ( WIFI_STA );
  WiFi.begin ( ssid, password );

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
  
  server.onNotFound ( handleNotFound );
  server.begin();
  Serial.println ( "HTTP server started" );
}

void loop() {
  // put your main code here, to run repeatedly:
  server.handleClient();
}
