# AttentionApp

Made for couples to be able to 'request' each other's attention


# First Time Setup

Asks for each person to enter their name. Then they each press the respective button identifying one of them as the 'giver' of attention and one as the 'receiver' of attention.

The 'receiver' is displayed a code which they must give to the 'giver', which upon submission completes the setup of the couple


# Usage

The 'receiver' can open the app which will show a button labeled 'Request Attention'. 

On press, this will start a foreground service on the 'giver's phone, with vibration, alarm sound, and a full-screen activity with flashing text saying '[getter] WANTS ATTENTION' and a button to acknowledge the request (note [getter] is replaced with their name in all caps)

When the acknowledge button is pressed, the sounds and vibration stop, the activity closes and the notification is removed. The 'getter' gets a notification of this acknowledgement.

If the 'giver' tries to close the activity, the vibration and sound will continue. The notification cannot be swiped away, but can be clicked to reopen the activity with an acknowledgement button.

I used firebase for the backend stuff (on this github in a different branch)
