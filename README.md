# TimeTeller
------------

This project is for the XP Days Ukraine 2017 conference, and represents the first state of code for the "Improving Your Organization’s Technical Prowess With Legacy Code Retreats" talk.

Please do not judge me on this code alone. ☺ 

This code is designed to be an example of legacy code that works, but is nothing that we are proud of and pretty hard to maintain.

#### The goal at this point is to:
* Time to start with some of the major structural flaws in the program.  First one.  Maybe the most glaring one.  TimeTeller's getFormattedTime has a duality.  It both gets time, formats time, and sends eMails.  Which one of these is not like the others?  Now that we understand the code better and have some safety nets under us, I'm changing getFormattedTime and getting rid of that third parameter.  The one that optionally sends an eMail of the results.  That's going into it's own package that we're going to be careful with.  Because maybe we want to extend it in the future!
* I also want to move the main in TimeTeller to it's own class (Demo) and get rid of the statics that the main is causing in the TimeTeller class.
* Now I'm looking at a class called TimeTeller which really is mostly about formatting time.  Let's refactor the class name to TimeFormatter.
* And while I'm at it, I refactor TimeFormatting into TimeFormattedAs, because the code reads more like sentances when I do that.
* As I look at TimeFormatter now, I realize that the pubic getFormattedTime method merely returns the private formatTime method.  Since I like the way that tests will read as simply formatTime, I will make that the public method and remove getFormattedTime.
* Now, let's think for a minute.  Why does TimeFormatter pass a parameter for which time zone to use for formatting?  I will now add a Clock class and pass *that* as a parameter into formatTime.  And the *big benefit* of that is that I can now make much better unit tests, because I'm not at the mercy of merely using whatever the time happens to be at the time of the test being run.  I can set the time for testing to whatever clock I need to test specific areas of the formatTime code!  In other words, Clock will be an interface that is constructed with a LocalDateTime and has methods for getHour, getMinute, and getSecond.  But I can have a concrete implementation as well as a fake implementation that I will use for testing.
* Finally, I can change that TimeFormatterTest eMailForLocalTime, which can fail from time to time because sometimes the time will change when it takes the eMail a long time to fire up.  Let's change that to eMailForLocalTimeNoon using the ClockForTesting.
* And now, I can remove the TimeFormatterTest methods for localTimeInWordsCurrent and zuluTimeInWordsCurrent, and replace them with *real* tests!
*