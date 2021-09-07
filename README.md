![Image of Main Menu](https://github.com/Caleb-Ward/MinecraftSnake/blob/main/MainMenu.JPG?raw=true)

# RUNNING THE GAME
* Either double click the .jar or .exe file inside main
* Or open the java project in a code editior with java complier then build and run the code
  
# CONTROLS
## IN GAME
* [←↕→] Arrow Keys for movement
* [P] To pause
* [Escape] to go to Main Menu/Quit

## MENU
* [Enter] To Play
* [O] for Options
* [H] for Help
* [Escape] to Quit

# FEATURES
* TNT item which hitting reduces tail and score by 1
* Random gems each time a new one is spawned
* Hovering text above collisions eg, items show +/-1 for gems/TNT and walls/lava display "ow"
* Pause button to stop snake movement
* Hitting self cuts off tail making a wall from section hit to end of tail and removes life
* Warpzones to allow snake to pass from one end of the screen to the other
* End text to show reason for loss or win
* Start menu with Play, Help, Options and Quit
* Options screen allows for screen size, extra obstacles and warpzone number selection
* Hitting lava will catch the snake on fire, hitting a wall will show a broken wall
* Animations for portal entry, lava, explosions and score changes
* Created smooth movement by interpolating snake position between frame updates

![Image of Gameplay](https://github.com/Caleb-Ward/MinecraftSnake/blob/main/Start.JPG?raw=true)
![Image of Gameplay](https://github.com/Caleb-Ward/MinecraftSnake/blob/main/Rect.JPG?raw=true)

# CHALLENGES

* If multiple keys are pressed snake would go back on self so direction key presses are added 
  to an array and processed only if the snake has moved to a new cell 

* Movement was too fast, but too jerky if I lowered frame rate so researched interpolation 
  and set move update to every second frame while snake images moves a percentage of distance
  between the current and next cell.

* Deciding which features to add and which to leave within the timeframe as it is better to have
  less fully implemented features than a lot of half done ones

# THINGS THAT COULD BE IMPROVED

* More options such as speed, different obstacles, texture themes, multiplayer or key mapping
  selection such as WASD instead of arrow keys.

* Optimising code to avoid repetition and unnecessary checks depending on options set as well
  as moving menu methods into it's own class to remove clutter from Game class

* Adjusting code so speed is not set to framerate as currently doubling/halving the frame rate
  increases or reduces the speed by half respectively.

* More obstacles such as a Zombie that slowly moves towards player attempting to kill them

* Different item types eg ones to increase lives, double score, or temporarily allow player to 
  pass through themselves

# RESOURCES USED

* Centering text help
https://www.tutorialspoint.com/what-are-the-differences-between-a-font-and-a-fontmetrics-in-java

* Smooth movement interpolation help
http://paulbourke.net/miscellaneous/interpolation/

* Random math help
https://www.geeksforgeeks.org/java-math-random-method-examples/

* Images from
https://minecraft.fandom.com

* Titles from
https://textcraft.net/
