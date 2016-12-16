#define echoPin 7               // Echo Pin
#define trigPin 8               // Trigger Pin
#define LEDPin 13               // Onboard LED
#define soundPin 0;             // A0 Analog pin
#define buzzerPin 9;

int speakerPin = buzzerPin;
int maximumRange = 200;         // Maximum range needed
int minimumRange = 0;           // Minimum range needed
long duration, distance;        // Duration used to calculate distance

byte PIRSensorPin = 6;

int length = 26;
char notes[] = "eeeeeeegcde fffffeeeeddedg";
int beats[] = {1, 1, 2, 1, 1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2};
int soundSensor = 10;
int val=0;
int relayStatus = 1;
int lightSensorPin = A0;
int lightSensorValue = 0;

int tempo = 200;

void setup() {
    Serial.begin (9600);

    pinMode(trigPin, OUTPUT);
    pinMode(speakerPin, OUTPUT);
    pinMode(PIRSensorPin, INPUT);
    pinMode(soundSensor, INPUT);
    pinMode(echoPin, INPUT);

    pinMode(LEDPin, OUTPUT); // Use LED indicator (if required)
    pinMode(10, OUTPUT);
    pinMode(11, OUTPUT);
    pinMode(12, OUTPUT);
}

void playTone(int tone, int duration) {
    for (long i = 0; i < duration * 1000L; i += tone * 2) {
        digitalWrite(speakerPin, LOW);

        digitalWrite(10, HIGH);
    
        delayMicroseconds(tone);
        digitalWrite(speakerPin, HIGH);
        
        digitalWrite(10, LOW);
      
        delayMicroseconds(tone);
    }
}

void playNote(char note, int duration) {
    char names[] = { 'c', 'd', 'e', 'f', 'g', 'a', 'b', 'C' };
    int tones[] = { 1915, 1700, 1519, 1432, 1275, 1136, 1014, 956 };

    // play the tone corresponding to the note name
    for (int i = 0; i < 8; i++) {
        if (names[i] == note) {
            playTone(tones[i], duration);
        }
    }
}

void getDistance() {
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
}

void activateUltrasonic() {
    getDistance();

    if (distance >= maximumRange || distance <= minimumRange){
        // Do nothing
    }else {
        digitalWrite(10, HIGH);
        if((distance > 10) && (distance < 30)) {
            for (int i = 0; i < length; i++) {
                activateLightSensor();
                activateMotion();
                
                if (notes[i] == ' ') {
                    delay(beats[i] * tempo);
                }else {
                    playNote(notes[i], beats[i] * tempo);
                }
                
                delay(tempo / 2); 
            }
        }
        
        digitalWrite(LEDPin, LOW); 
    }
}

void activateMotion() {
    byte state = digitalRead(PIRSensorPin);  // read input value
    Serial.println(state);

    if (state == 0 ){
        digitalWrite(11, HIGH);     // SVHSE
    }else {
        digitalWrite(11, LOW);      // SVHSE
    }
}

void activateLightSensor() {
    lightSensorValue = analogRead(lightSensorPin);
    Serial.print("Light Sensor Value: ");
    Serial.println(lightSensorValue, DEC);
    
    if(lightSensorValue > 40) {
        // Activate the lights
        digitalWrite(12, LOW);
    }else {
        // Deactivate the lights
        digitalWrite(12, HIGH);
    }
}

/*
*   Loop time, yay!
*/
void loop() {
    // Check if distance sensor is active and activate buzzer
    activateUltrasonic();

    // Check the motion sensor and activate the RELAY0
    activateMotion();

    // Light sensor lights up the 3rd series of the cmas lights
    activateLightSensor();

    delay(50);
}
