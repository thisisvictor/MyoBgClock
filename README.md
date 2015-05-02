# MyoBgClock
A simple Android background service using Myo to tell time.

The most important file is the "BgClockService.java" under the src directory, which is based on the example provided in the Myo Android SDK named "BackgroundService.java".
The code is almost self-explanatory with some comments inside. You can also uncomment the Toasts to make sure everything works as expected.

## How to use
To get the time, you'll first have to unlock Myo, which is done by a quick Fist-then-release (i.e. a "pump" action). Feel free to replace it with any gesture you want. A short vibration confirms you've unlocked it.

Then perform a finger-spread-then-release, which is confirmed by 2 quick short vibrations (What you end up doing will be a well-paced "squeeze" and then "flex"). 
1 second after that Myo will go through a few vibrations to tell time, using the following system:

### Hour (12 hour format)
0 to 4 short vibrations, each representing an hour.
An extra medium vibration, if it is 5 or above.
And another extra medium vibration, if it is 10 or above.

So for example, if it is 3-something, you'll feel SSS; if it is 7-something, you'll feel SSM. 
Both 11-something-am and 11-something-pm will be represented by SMM.

### Minute (1.8 seconds after the hour is told)
0 to 5 short vibrations, each representing 10 minutes.
Pauses for 1.8 seconds then
0 to 4 short vibrations, each representing 1 minute.
An extra medium vibration, if it is 5 or above for the last digit.

So for example, if it is 24 minutes pass some hour, you'll feel SS-SSSS; if it is 1 mintue to the next hour, you'll feel SSSSS-SSSSM.

### Importance of the pause
As you can see the pause of 1.8 seconds is very important to separate the increments. Especially when there is a 0 in the time. 
For example, 0:09 will be --SSSSM, after the initial 2 quick short vibrations of confirmation.

This probably won't be the best way to tell time, but it's pretty interesting. 
And feel free to adjust the time-telling system and pause duration by reading through the comments in the code.

## Note
This service was created in Nov 2014 so the API calls might have changed. Didn't have the time to update or try it out for the latest SDK so pardon me if it doesn't work, or there is a better way to do so.
