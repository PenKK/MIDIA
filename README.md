<img width="963" height="804" alt="display" src="https://github.com/user-attachments/assets/3ec6d260-70de-4374-bc30-fd7c8d92c447" />

# MIDIA <br>
Musical Interface for Digital Instrument Arrangement

Made with Java Swing and the java.sound.midi library. <br>
Tested with JUnit. <br>
FlatLaf look and feel was used: https://www.formdev.com/flatlaf/.

## UML Diagram (outdated)
<img width="700" alt="UML_DIAGRAM-1" src="https://github.com/user-attachments/assets/2104ee08-ff3b-4511-98e8-d64312eba37c" />


## Usage


### Create a track
1. Use the Track menu at the top left to insert a new track
2. Specify a name and instrument
    - After submitting, an empty track should appear in the timeline tab

### Create a note
1. Use the Track menu to add a block
    - Specify a start beat
2. Double click a block to open the piano roll for edting the block
    - Left click to create a note, and right click to delete a note
    
### Play the project

1. Press the play button at the top right to play the song
    - The red line should follow the position of the audio
2. You can play, pause, and drag the position of the timeline (even during playback)
3. The tempo may be adjusted using by holding and dragging the up/down on the BPM display.

### Modify a track
1. Right click the label with the track name
2. Can choose to change the name, instrument, or delete it
    - Name updates will be reflected in the label text
    - If a track is deleted, it will disappear from the timeline

### Save the project

1. Click the file menu at the top left and then save
2. Specify the path and then save

### Open a project

1. Click the file menu at the top left and then load
2. Select a project and then click open
    - The project should be loaded into the timeline
