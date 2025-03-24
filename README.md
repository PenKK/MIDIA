# My Personal Project 

## A DAW (Digital Audio Workstation)

***What will the application do?***
- Create music with layers of different sounds
    - Have multiple toggable tracks (layers).
    - Insert notes utilizing MIDI presets to make music.
    - Adjust tempo of the project.
    - Adjust pitch of audio.
    - Create melodies via piano roll.
- Playback of the project within the program.
- Save and load progress
- All of the above via a graphical user interface
    - View and edit the timeline

***Who will use it?***
- Those looking to make music with either their own audio or some basic sounds the program provides.
- The program can also be used for audio manipulation and does not have to be restricted to music.

***Why is this project of interest to you?***
- I have made games before, but I wanted to make an actual piece of software that serves a specific purpose. 
- Music has always been of great interest to me, I played the trombone throughout all of highschool. 
- Although I honestly have never made music in a DAW before, I have some (very basic) understanding of how these programs work and music. 
- The idea came to me from the `SimpleDrawingPlayer` repository, though that program is more of a playground than a DAW. I wanted to make a much more complete and formal version.
- I thought that creating such a program would be a feasible challenge considering Java's built-in support for MIDI handling in the Sound library.

**User Stories**
- As a user, I want to be able to add a track to my project so that I can start creating music with different layers of sound.
- As a user, I want to be able to view the list of tracks in my project so that I can see all the elements I've been working with.
- As a user, I want to be able to add a Block to my track.
- As a user, I want to be able to add MIDI notes to a block and specify the pitch, velocity, and duration so that I can create a sequence of sounds.
- As a user, I want to be able to add MIDI notes using a piano roll.
- As a user, I want to be able to move the position of a block so that I can change when the notes inside play.
- As a user, I want to be able to modify an existing note on a block by changing its pitch, velocity, or timing so that I can make adjustments to my composition.
- As a user, I want to be able to delete tracks, blocks, and notes from my project so that I can remove elements I no longer need.
- As a user, I want to specify tempo so that I can change the speed of my project
- As a user, I want to be able to control playback of my project so that I can start and stop where I want to on the timeline
- As a user, I want to be able to save projects so that when my program closes I dont lose progress
- As a user, I want to be able to load a project from a list so that I can continue working on any project I saved before


## Instructions for End User

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