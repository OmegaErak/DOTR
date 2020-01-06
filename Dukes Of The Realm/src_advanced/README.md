# Dukes of the Realm

Welcome to Dukes of the Realm.
It is a strategy turn-based game where the king has fallen and the dukes want to take his place as ruler of the Realm.
You are playing one of the dukes and must defeat all the others to get the throne.
 
 ## How to compile project
 
 ### JavaFX
 
 First of all, this project depends on JavaFX.
 If your Java version is old enough (10 or minor), JavaFX comes with the JDK.
 If it's not the case, you must get it from [here](https://openjfx.io/) (download the same version as your JDK).
 
 ### Import into IDE
 
 Next step is to import the project into an IDE.
 Officially we cover Eclipse and IntelliJ IDEA, so if you have questions about those, please send a mail to [Rynhho](mailto:ryngetsu@gmail.com).
   
 Import the three folders (resources, src_advanced ,src_basic).
 Then you must mark **resources** and the folder corresponding to the version you want to play (**src_advanced** or **src_basic**) as source folders using your IDE.
 
 #### How to mark as resources
 
 IntelliJ: File > Project Structure > Modules > Chose folder and click on "Sources"
 
 [comment]: <> (TODO)
 Eclipse: 
 
 ### JavaFX again
 
 If you had to download JavaFX, this is where you have to link it to the project by following these [steps](https://openjfx.io/openjfx-docs/) (there is only a section about IntelliJ, but Eclipse should be analog).
 
 ### Launch
 
 Now you just hit compile and launch the game. Have fun!
 
 ## How to play
 
 ### First steps
 
 The gameplay should be quite forward. You can launch a new game or load an existing one.
 
 [comment]: <> (TODO: Update if theres is a file explorer in advanced one)
 
 If you want to load an existing game, you have to put the save file into the source folder.
 
 ### In-game
 
 Once inside the game, you have your castle presented by a flag, the castles of active dukes are colored and the castles of ambitionless dukes are grey.
 
 When you click on a foreign castle, you can see its information and the troops it stores.
 
 When you click on one of your castles, you can see its informations, its troops and the buttons appear for you to use commands as the ruler of the castle.
 You can recruit more troops using your money, you can level up the castle if you have enough money and you can move your troops.
 
 When you move your troops, you select them and chose the castle you want them to move to.
 If it's a foreign castle, they will attack that castle, and there is no going back.
 Each day, they will output a certain damage to the castle's troops, and if your troops beat the foreign troops, you gain control over that castle.
 If you troops lose, they will die and the castle's owner won't change.
 
 You can also click on another castle of yours, and in that case the troops will only change castle.
 
 ## Help and contributions
 
 [comment]: <> (TODO: Documentation)
 If you want to read the code and have a problem understanding it, you have a documentation [here]() and can contact [Rynhho](mailto:ryngetsu@gmail.com).
 
 
 
 
 