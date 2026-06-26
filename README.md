# fiveCardGUI

A desktop implementation of Five Card Draw Poker built with **Java** and **JavaFX**. The player competes against a dealer controlled by an algorithm that evaluates its hand and strategically chooses which cards to discard before the round starts.

## Features

- Five Card Draw poker gameplay
- Dealer AI that intelligently discards cards
- Betting mechanics
- Folding and calling
- Raise functionality
- Multiple rounds
- Graphical user interface built with JavaFX

---

## Technologies Used

- Java 21
- JavaFX 21
- Eclipse IDE

---

## Requirements

- Java 21 (or newer)
- JavaFX SDK 21.0.11

Download JavaFX21 here:

[JavaFX21](https://www.oracle.com/java/technologies/downloads/javafx/#javafx21)

---

## Running the Project

### Option 1: Run the executable JAR

Download the latest `fiveCardGUI.jar` from the **Releases** page.

Open a terminal and run:

```bash
java --module-path "E:\javafx-sdk-21.0.11\lib" --add-modules javafx.controls,javafx.fxml -jar fiveCardGUI.jar
```

Replace the JavaFX path with the location where **you** installed the JavaFX SDK.

---

### Option 2: Run from Eclipse

1. Clone the repository

```bash
git clone https://github.com/MinhQuangNT/fiveCardGUI.git
```

2. Import the project into Eclipse.

3. Add the JavaFX SDK to the project's build path.

4. Configure the VM arguments:

```text
--module-path "PATH_TO_YOUR_JAVAFX_LIB" --add-modules javafx.controls,javafx.fxml
```

Example:

```text
--module-path "E:\javafx-sdk-21.0.11\lib" --add-modules javafx.controls,javafx.fxml
```

5. Run the application.

---

## Author

**Minh Quang Nguyen Tong**

GitHub:
https://github.com/MinhQuangNT
