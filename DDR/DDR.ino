// DDR Arduino Controller – Active HIGH buttons

const int btnLeft = 2;
const int btnDown = 3;
const int btnUp = 4;
const int btnRight = 5;

void setup() {
  Serial.begin(9600);

  pinMode(btnLeft, INPUT);
  pinMode(btnDown, INPUT);
  pinMode(btnUp, INPUT);
  pinMode(btnRight, INPUT);
}

void loop() {
  if (digitalRead(btnLeft) == HIGH) {
    Serial.println("LEFT");
    delay(150);
  }
  else if (digitalRead(btnDown) == HIGH) {
    Serial.println("DOWN");
    delay(150);
  }
  else if (digitalRead(btnUp) == HIGH) {
    Serial.println("UP");
    delay(150);
  }
  else if (digitalRead(btnRight) == HIGH) {
    Serial.println("RIGHT");
    delay(150);
  }
  else {
    Serial.println("NONE");
    delay(50);
  }
}
