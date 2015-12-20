#define echoPin 7
#define trigPin 8

#define RELAY  4

int maximumRange = 50;         // Maximum range needed
int minimumRange = 0;           // Minimum range needed

long duration, distance;        // Duration used to calculate distance

int timer = 3000;               // 5000ms - 5sec

int relayStatus = 0;

void setup() {
   Serial.begin (9600);
   pinMode(trigPin, OUTPUT);
   pinMode(echoPin, INPUT);
   
   pinMode(RELAY, OUTPUT);   
   //digitalWrite(RELAY, 1);
}

void sigON() {
   if( relayStatus == 0 ) {
     // Turn relay ON and wait 5 sec
     relayStatus = 1;
    
     digitalWrite(RELAY, 0);
     Serial.println("Distance light ON");
   }
}

void sigOFF() {
    if( relayStatus == 1 ) {
     relayStatus = 0;
    
     digitalWrite(RELAY, 1);
     Serial.println("Distance light OFF");
   }
}

void loop() {
/* The following trigPin/echoPin cycle is used to determine the
 distance of the nearest object by bouncing soundwaves off of it. */ 
 digitalWrite(trigPin, LOW); 
 delayMicroseconds(2); 

 digitalWrite(trigPin, HIGH);
 delayMicroseconds(10); 
 
 digitalWrite(trigPin, LOW);
 duration = pulseIn(echoPin, HIGH);
 
 
 //Calculate the distance (in cm) based on the speed of sound.
 distance = duration/58.2;
 
 if (distance >= maximumRange || distance <= minimumRange){
    sigOFF();
    Serial.println("OUT of distance");
 }else { 
    sigON();
    Serial.println("IN distance");
    delay(timer);
 }
 
 //Delay 50ms before next reading.
 delay(50);
}

