<img width="963" height="804" alt="display" src="https://github.com/user-attachments/assets/3ec6d260-70de-4374-bc30-fd7c8d92c447" />

# MIDIA <br>
Musical Interface for Digital Instrument Arrangement <br>
Digital Audio Workstation (DAW)

## Overview

**MIDIA** is a lightweight Java-based digital audio workstation designed around intuitive MIDI sequencing.  
It features:

- A **timeline window** for organizing tracks and blocks  
- A **dedicated Piano Roll editor window**, opened per block in its own `JFrame`  
- Real-time MIDI playback with a draggable playhead  
- Block-based editing for structuring musical ideas  
- Clean separation between timeline interaction and note editing  
- Unit-tested core components and a well-organized architecture

The result is a minimal yet powerful MIDI composition tool built in pure Java.

---

## UML Diagram

<img width="700" alt="UML_DIAGRAM-1" src="https://github.com/user-attachments/assets/2104ee08-ff3b-4511-98e8-d64312eba37c" />

---

## Features

### 🎼 Track System
- Create multiple MIDI tracks with customizable names and instruments  
- Rename tracks, change their instruments, or delete them  
- Tracks appear vertically in the timeline, each containing blocks representing musical segments  

### 📦 Block-Based Editing
- Add blocks specifying their starting beat  
- Blocks represent a section of notes to be edited  
- Double-clicking a block opens a **separate Piano Roll window (JFrame)** for detailed note editing  

### 🎹 Piano Roll Window
Each block opens in its own floating editor window:

- Left-click: add a note  
- Right-click: delete a note  
- Scrollable and grid-aligned  
- Plays back using the block's track instrument  
- Editing updates the block in the timeline seamlessly

### ▶️ Playback Controls
- Play, pause, and drag the playhead during playback  
- The red playhead follows MIDI output in real time  
- Adjustable BPM: click-and-drag the BPM value to increase/decrease tempo  

### 💾 Saving & Loading
- **File → Save** writes the full project (tracks, blocks, notes, BPM)  
- **File → Load** restores previously saved sessions  

---

## Usage Guide

### Creating a Track
1. Open **Track → Add Track**  
2. Enter a name and select an instrument  
3. A new track appears in the timeline

### Creating a Block
1. Use **Track → Add Block**  
2. Enter the block’s starting beat  
3. The block appears inside the selected track

### Editing Notes in the Piano Roll
1. Double-click a block  
2. A new Piano Roll window opens  
3. In the Piano Roll:
   - **Left-click** → add note  
   - **Right-click** → delete note  

### Playback
1. Press the top-right Play button  
2. Drag the timeline playhead at any time  
3. Adjust BPM by dragging on the BPM value  

### Track Options
1. Right-click a track label  
2. Choose rename / change instrument / delete track  

### Saving & Loading Projects
- Use **File → Save** and **File → Load**  

---

## Technical Details

- **UI:** Swing with FlatLaf
- **MIDI Playback:** `javax.sound.midi`
- **Testing:** JUnit
- **Platform:** Cross-platform desktop
