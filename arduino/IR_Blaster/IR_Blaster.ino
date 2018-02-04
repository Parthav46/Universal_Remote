int IRledPin = 6;
int DataLine = 2;

int LOW_VAL = 600;
int HIGH_VAL = 1200;
int RANGE_START = 21;
int RANGE_END = 41;
int MODULATION_TIME = 26;

String node_data = "";

uint16_t IRsignal[200] = {0};
  int arraySize = 0;

void pulseIR(long microsecs)
{
  cli();
  bool level = HIGH;
  while (microsecs > 0)
  {
    digitalWrite(IRledPin, level);
    level ^= 1;
    delayMicroseconds(10);
    digitalWrite(IRledPin, level);
    level ^= 1;
    delayMicroseconds(10);
    microsecs -= 26;
  }
  sei();
}

void EncodeData(int encodeVal)
{
  int tmpEncodeVal = encodeVal;
  for (int i = RANGE_START; i <= RANGE_END; i++)
  {
    if (tmpEncodeVal & 0x01)
      IRsignal[i * 2 + 1] = HIGH_VAL / 10;
    else
      IRsignal[i * 2 + 1] = LOW_VAL / 10;
    tmpEncodeVal = tmpEncodeVal >> 1;
  }

}

void SendIRCode()
{
  for (int i = 0; i < arraySize/2; i++)
  {
    pulseIR(IRsignal[i++] * 10);
    delayMicroseconds(IRsignal[i] * 10);
  }
}

void setIRsignal(String indata) {
  int comma = indata.indexOf(",");
    arraySize = 0;
  while (comma > 0) {
    IRsignal[arraySize] = (int) indata.substring(0, comma).toInt();
    arraySize++;
    indata = indata.substring(comma+1);
    comma = indata.indexOf(',');
  }
}

void setParams(String data) {
  int comma = data.indexOf(',');
  LOW_VAL = (int) data.substring(0, comma).toInt();
  data = data.substring(comma + 1);
  comma = data.indexOf(',');
  HIGH_VAL = (int) data.substring(0, comma).toInt();
  data = data.substring(comma + 1);
  comma = data.indexOf(',');
  RANGE_START = (int) data.substring(0, comma).toInt();
  data = data.substring(comma + 1);
  comma = data.indexOf(',');
  RANGE_END = (int) data.substring(0, comma).toInt();
  data = data.substring(comma + 1);
  comma = data.indexOf(',');
  MODULATION_TIME = (int) data.substring(0, comma).toInt();
}

void printIR() {
  for (int i = 0; i < arraySize/2; i++) {
    Serial.print(IRsignal[i++]);
    Serial.print(" ");
    Serial.println(IRsignal[i]);
  }
}

void printParam() {
  Serial.println(LOW_VAL);
  Serial.println(HIGH_VAL);
  Serial.println(RANGE_START);
  Serial.println(RANGE_END);
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
    while(temp != '\n'){
      node_data += temp;
      delay(1);
      temp = Serial.read();
    }
  }

  if (node_data != "") {
    
    if (digitalRead(2)) {
      setIRsignal(node_data);
      EncodeData(1);
      SendIRCode();
      printIR();
    }
    else {
      setParams(node_data);
      printParam();
    }
    node_data = "";
    Serial.println("end");
    delay(1000);
  }
}
