#!/bin/bash
cd ~/FAST/SDA/PROJECT/End-to-End-Delivery-System

echo "Compiling..."
javac -cp "lib/mysql/mysql-connector.jar" --module-path ~/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml -d bin src/util/*.java src/model/*.java src/dao/*.java src/application/*.java

if [ $? -eq 0 ]; then
    echo "Syncing FXML/CSS resources..."
    mkdir -p bin/application
    cp src/application/*.fxml bin/application/
    cp src/application/application.css bin/application/
    echo "Running..."
    java -cp "bin:lib/mysql/mysql-connector.jar" --module-path ~/javafx-sdk-21/lib --add-modules javafx.controls,javafx.fxml application.Main
else
    echo "Compilation failed!"
fi
