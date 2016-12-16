#define RELAY  8

int soundSleep = 3000;
int relayStatus = 0;
int mic = 6;

void setup() {
   Serial.begin (9600);
   pinMode(mic, INPUT);
   pinMode(RELAY, OUTPUT);
   digitalWrite(RELAY, 1);
   delay(2000);   
}

void soundON() {
   if( relayStatus == 0 ) {
     relayStatus = 1;
    
     digitalWrite(RELAY, 0);
     Serial.println("Light ON...");
   }
}

void soundOFF() {
    if( relayStatus == 1 ) {
      relayStatus = 0;
    
      digitalWrite(RELAY, 1);
      Serial.println("Light OFF...");
   }
}

void loop() {
 int val = digitalRead(mic);
 if(val == 1) {
   soundON();
   delay(soundSleep);
 }else{
   soundOFF();
 }
 delay(40);
}

