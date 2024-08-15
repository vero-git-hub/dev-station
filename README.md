# Dev Station
Dev Station is a utility application designed to automate routine tasks for programmers. This desktop application allows users to create scripts for launching, update drivers, clear files, check website functionality, and track changes in files, among other features.

## Features
* **Scripts**: Launch all your programs from one place by simply creating a script.
* **Driver**: Compare driver versions, download, and launch all in one place.
* **Clean**: Emulate a recycle bin: move files to a pseudo-recycle bin and restore them.
* **Ping**: Internal terminal for checking website ping.
* **Monitoring**: View files, track and highlight changes with a timer and cleanup (for logs).
* **Debugging**: View system resources.

### Additional Features
You can also look forward to two interface themes, multiple languages, and an image folder: specify the path to a folder with images, and all pictures will be displayed in the Dev Station program window.

## Technologies
* Java, JavaFX, FXML, Maven
* IntelliJ IDEA

## Installation and Launch
To use the application, you need to:

Clone the repository:
```
git clone https://github.com/yourusername/monitoring-app.git
```
Build the project:
```
cd monitoring-app
mvn package
```
Launch the application:
```
java -jar target/monitoring-app.jar
```

## Usage
After launching the application, the user can configure paths to folders through the settings button, as well as in the corresponding tabs in the left sidebar.

## Contribution
If you want to contribute to the project, please create a pull request with a description of the changes made. All suggestions for improvements are welcome!

## License
This project is licensed under the MIT License.