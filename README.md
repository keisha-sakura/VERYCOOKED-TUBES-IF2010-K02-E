
**Selamat datang di VERYCOOKED!** 🎮  
Game memasak ala *Overcooked* dimana kamu jadi chef yang harus memasak pizza sebelum waktu habis!

---

## 📋 Table of Contents

- [Features](#-features)
- [Branches](#-branches)
- [Requirements](#-requirements)
- [Installation](#-installation)
- [How to Run](#-how-to-run)
  - [GUI Version (main)](#gui-version-main)
- [Controls](#-controls)
- [Gameplay](#-gameplay)
- [Project Structure](#-project-structure)
- [Credits](#-credits)

---

## ✨ Features

🍕 **3 Pizza Recipes**: Margherita, Sosis, Ayam  
👨‍🍳 **2 Playable Chefs**: Pikachu & Jigglypuff  
⏱️ **Time-Based Challenge**: Race against the clock!  
🎨 **Animated Sprites**: Walking animations in 4 directions  
📦 **Multiple Stations**: Cutting, Cooking, Washing, Assembly  
🎯 **Order System**: Complete orders before they expire  
💯 **Scoring System**: Earn points for successful deliveries  

---

## 🌿 Branches

| Branch | Description | Interface |
|--------|-------------|-----------|
| `main-CLI` | Command-Line Interface version | Terminal-based |
| `main` | Graphical User Interface version | JavaFX GUI |

---

## 📦 Requirements

### For CLI Version (main-CLI)
- ☕ **Java 17** or higher
- 🖥️ Terminal/Command Prompt

### For GUI Version (main)
- ☕ **Java 17** or higher
- 🎨 **JavaFX SDK 21.0.9** ([Download here](https://openjfx.io/))
- 🖥️ Windows/Linux/Mac with GUI support

---

## 📥 Installation

### 1. Clone Repository
git clone https://github.com/your-username/VERYCOOKED-TUBES-IF2010-K02-E.git
cd VERYCOOKED-TUBES-IF2010-K02-E

### 2. Download JavaFX (GUI Version Only)
- Download JavaFX SDK 21.0.9 from [openjfx.io](https://openjfx.io/)
- Extract ke lokasi favorit kamu, misal: `C:\javafx-sdk-21.0.9`

---

## 🚀 How to Run

(\__/)
(o^.^)  <- Pikachu siap masak!
z(_(")(")

## 🎨 Quick Start - GUI Version

### Step 1: Clone & Setup
git clone https://github.com/your-username/VERYCOOKED-TUBES-IF2010-K02-E.git
cd VERYCOOKED-TUBES-IF2010-K02-E
git checkout main

### Step 2: Download JavaFX
- Download JavaFX SDK 21.0.9 dari [openjfx.io](https://openjfx.io/)
- Extract ke lokasi favorit, misal: `C:\javafx-sdk-21.0.9`

### Step 3: Navigate to Source
cd src/main/java

### Step 4: Compile (Windows - PowerShell)
$env:JAVAFX_PATH = "C:\javafx-sdk-21.0.9\lib"
$files = Get-ChildItem -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName
javac --module-path $env:JAVAFX_PATH --add-modules javafx.controls,javafx.fxml $files
### Step 4: Compile (Linux/Mac)
export JAVAFX_PATH="/path/to/javafx-sdk-21.0.9/lib"
find . -name "*.java" > sources.txt
javac --module-path $JAVAFX_PATH --add-modules javafx.controls,javafx.fxml @sources.txt

### Step 5: Run (Windows)
java --module-path $env:JAVAFX_PATH --add-modules javafx.controls,javafx.fxml -cp ".;..\resources" Main
### Step 5: Run (Linux/Mac)
java --module-path $JAVAFX_PATH --add-modules javafx.controls,javafx.fxml -cp ".:../resources" Main

### Step 6: Enjoy the GUI! 🎨

---

## 🎮 Controls

| Key | Action |
|-----|--------|
| **W** / **↑** | Move Up |
| **A** / **←** | Move Left |
| **S** / **↓** | Move Down |
| **D** / **→** | Move Right |
| **C** | Pickup/Drop Item |
| **V** | Interact (Chop/Cook/Wash) |
| **B** | Switch Chef |
| **Q** / **ESC** | Quit Game |

---

## 🍳 Gameplay

### Objective
Cook and serve pizzas to complete customer orders before time runs out!

### How to Play
1. **Pick up a plate** from Plate Station (C)
2. **Collect ingredients**: Adonan (Dough), Tomat (Tomato), Keju (Cheese), Optional: Sosis (Sausage) or Ayam (Chicken)
3. **Chop ingredients** at Cutting Station (V)
4. **Cook ingredients** at Cooking Station (V)
5. **Assemble pizza** at Assembly Station (C)
6. **Serve** at Serving Counter (C)

### Pizza Recipes
| Pizza | Ingredients |
|-------|-------------|
| 🍕 **Margherita** | Adonan + Tomat + Keju |
| 🌭 **Sosis** | Adonan + Tomat + Keju + Sosis |
| 🍗 **Ayam** | Adonan + Tomat + Keju + Ayam |

### Scoring
- ✅ Complete order: **+120 points**
- ⏰ Time bonus: Up to **+50 points**
- ❌ Failed order: **-50 points**
- 💀 5 consecutive failures: **Game Over**

### Difficulty Levels
- **Easy**: 4 minutes ⭐
- **Medium**: 3 minutes ⭐⭐
- **Hard**: 2 minutes ⭐⭐⭐

(\__/)
(- ‿- )  Pikachu's Pro Tips!
/>🍕
1. **Team Work**: Use both chefs efficiently - one chops, one cooks!
2. **Watch the Timer**: Orders expire in 60 seconds!
3. **Don't Burn**: Check oven regularly to avoid burned ingredients
4. **Clean Plates**: Dirty plates must be washed before reuse
5. **Plan Ahead**: Read orders and prep ingredients in advance

**Developed by**: IF2010 - K02 - Kelompok E  
**Course**: Pemrograman Berorientasi Objek  
**Institution**: Institut Teknologi Bandung  
**Year**: 2025

### Team Members
- 👨‍💻 Keisha Sakura Widjaja
- 👨‍💻 Rafael Sean
- 👨‍💻 Azfar Arafi
- 👨‍💻 Melvin Irsyad

---

## 📜 License

This project is made for educational purposes as part of IF2010 coursework.

---

## 🎮 Enjoy the Game!

(\__/)
(>‿<)  Thanks for playing!
/>💖





