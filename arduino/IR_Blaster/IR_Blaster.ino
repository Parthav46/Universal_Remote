#define IRledPin = 6;
#define DataLine = 2;

int MODULATION_TIME = 26;

String node_data = "";

uint16_t IRsignal[200] = {0};
byte arraySize = 0;

void pulseIR(long microsecs)
{
  cli();
  bool level = HIGH;
  int interval = (MODULATION_TIME/2)-4;
  while (microsecs > 0)
  {
    digitalWrite(IRledPin, level);
    level ^= 1;
    delayMicroseconds(interval);
    digitalWrite(IRledPin, level);
    level ^= 1;
    delayMicroseconds(interval);
    microsecs -= MODULATION_TIME;
  }
  sei();
}

void SendIRCode()
{
  for (int i = 0; i < arraySize; i++)
  {
    pulseIR(IRsignal[i++] * 10);
    delayMicroseconds(IRsignal[i] * 10);
  }
}

void setIRsignal(String indata)
{
  int comma = indata.indexOf(",");
  arraySize = 0;
  while (comma > 0) 
  {
    IRsignal[arraySize] = (int) indata.substring(0, comma).toInt();
    arraySize++;
    indata = indata.substring(comma+1);
    comma = indata.indexOf(',');
  }
}

void setParams(String data) 
{
  int comma = data.indexOf(',');
  MODULATION_TIME = (int) data.substring(0, comma).toInt();
}

void printIR() 
{
  Serial.println(arraySize);
  for (int i = 0; i < arraySize; i++) 
  {
    String temp = IRsignal[i++]+" "+IRsignal[i];
    Serial.println(temp);
  }
}

void printParam() 
{
  Serial.println(MODULATION_TIME);
}

void setup(void)
{
  pinMode(IRledPin, OUTPUT);
  pinMode(DataLine, INPUT);
  Serial.begin(9600);
}

void loop(void)
{
  while (Serial.available() > 0)
  {
    delay(10);
    char temp = Serial.read();
    while(temp != '\n')
    {
      node_data += temp;
      delay(1);
      temp = Serial.read();
    }
  }

  if (node_data != "") 
  {  
    if (digitalRead(2)) 
    {
      setIRsignal(node_data);
      SendIRCode();
      printIR();
    }
    else 
    {
      setParams(node_data);
      printParam();
    }
    node_data = "";
    Serial.println("end");
    delay(1000);
  }
}
