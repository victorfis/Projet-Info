/* 
 * example code illustrating Time l.
 * Messages consist of the letter T followed by ten digit time (as seconds since Jan 1 1970)
 * you can send the text on the next line using Serial Monitor to set the clock to noon Jan 1 2013
 T1357041600  
 *
 * A Processing example sketch to automatically send the messages is included in the download
 * On Linux, you can use "date +T%s\n > /dev/ttyACM0" (UTC time zone)
 * Or echo -e "$(date +T%s)\n" > /dev/ttyACM0
 */ 
 
#include <TimeLib.h>
#include <Adafruit_ssd1306syp.h> //This is a simplified library

#define SDA_PIN A4  //SDA pin of OLED connected to A4
#define SCL_PIN A5  //SCL pin of OLED connected to A5

Adafruit_ssd1306syp display(SDA_PIN,SCL_PIN);

#define TIME_HEADER  "T"   // Header tag for serial time sync message
#define TIME_REQUEST  7    // ASCII bell character requests a time sync message 

void setup()  {
  Serial.begin(9600); //enable bluetooth with tx and rx pins
  while (!Serial) ; // Needed for Leonardo only
  pinMode(13, OUTPUT);  //set 13th pin as output
  setSyncProvider( requestSync);  //set function to call when sync required
  Serial.println("Waiting for sync message");
  delay(1000);  //1 sec delay
  //Initialization of OLED
  display.initialize(); //initialize the screen 
  display.setTextSize(2); //set the text size 
  display.setTextColor(WHITE);  //set the text color (white seen as blue actually)
  display.setCursor(10,20);  //set the position of the 1st letter
  display.print("WATCH'INT"); //set the text WATCH'INT
  display.update();
}

void loop(){    
  if (Serial.available()) { //if something is received
    processSyncMessage(); //call a function to react to received messages
  }
  if (timeStatus()!= timeNotSet) { //if the time is set
    display.clear(); 
    digitalClockDisplay();  //it is displayed
  }
  if (timeStatus() == timeSet) {  //if the time is set
    digitalWrite(13, HIGH); // LED on if synced
  } else {
    digitalWrite(13, LOW);  // LED off if needs refresh
  }
  delay(1000); //1 sec delay
}

void digitalClockDisplay(){
  // digital clock display of the time
  display.setTextSize(2);
  display.setTextColor(WHITE);
  display.setCursor(10,20);
  display.print(hour());//diplay time
  printDigits(minute());
  printDigits(second());
  display.println(); // equivalent to \n
  display.print(day());//display date
  display.print("/");
  display.print(month());
  display.print("/");
  display.print(year()); 
  display.update();
}

void printDigits(int digits){
  // utility function for digital clock display: prints preceding colon and leading 0
  display.print(":");
  if(digits < 10)     // if the number is between 0 and 9
    display.print('0'); //print a 0 before
  display.print(digits); //display minutes or seconds
}


void processSyncMessage() {
  unsigned long pctime;
  const unsigned long DEFAULT_TIME = 1357041600; // Jan 1 2013

  if(Serial.find(TIME_HEADER)) {
     pctime = Serial.parseInt();
     if( pctime >= DEFAULT_TIME) { // check the integer is a valid time (greater than Jan 1 2013)
       setTime(pctime); // Sync Arduino clock to the time received on the serial port
     }
  }
}

time_t requestSync()
{
  Serial.write(TIME_REQUEST);  
  return 0; // the time will be sent later in response to serial mesg
}

