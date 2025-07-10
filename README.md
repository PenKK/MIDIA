# MIDIA, a MIDI DAW (Digital Audio Workstation)

***Why a DAW?***
- I have made games before, but I wanted to make an actual piece of software that serves a specific purpose. 
- Music has always been of great interest to me, I started playing trombone in grade 7 and have continued since.
- Although I honestly have never made music in a DAW before, I have some (very basic) understanding of how these programs work and music.
- I thought that creating such a program would be a feasible challenge considering Java's built-in support for MIDI handling in the Sound library.

## Usage

### Create a track
1. Use the Track menu at the top left to insert a new track
2. Specify a name and instrument
    - After submitting, an empty track should appear in the timeline tab

### Create a note
1. Use the Track menu to add a block
    - Specify a start beat
2. Use the Track menu to add a note
    - Specify pitch, velocity, the beat to start on, and the beat duration
    - The start beat is relative to the block start beat (it's the start beat within the block)
    - Middle C is at pitch = 60, audible for most instruments
    - Pitch and velocity are [0, 127]
    - The note should appear on the timeline. If it does not, the duration was likely set to 0.
    
### Play the project

2. Press the play button at the top right to play the song
    - The red line should follow the position of the audio
3. You can play, pause, and resume the song. If the song finishes, the red line comes back to the start.

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
