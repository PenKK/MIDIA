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

## Phase 4: Task 2

Application launched

``
[Wed Mar 26 19:09:09 PDT 2025] A new timeline instance was created with project name: New Project
``

Created a new track

``
[Wed Mar 26 19:09:22 PDT 2025] Created new MidiTrack, instrument: Acoustic Grand Piano, channel: 0, percussive: false. Remaining instrumental channels: 14
``

Added a block in the previously made track (X in Y)

``
[Wed Mar 26 19:09:26 PDT 2025] Added Block with 0 notes to MidiTrack piano melody
``

Created a note inside of a block

``
[Wed Mar 26 19:09:35 PDT 2025] Added note: pitch: 63, velocity: 80, startTick: 0, durationTicks: 2880 to Block: Start tick: 960, current note count: 1
``

Created a second note inside of the same block

``
[Wed Mar 26 19:09:46 PDT 2025] Added note: pitch: 61, velocity: 80, startTick: 960, durationTicks: 1920 to Block: Start tick: 960, current note count: 2
``

Created another track (percussive)

``
[Wed Mar 26 19:10:06 PDT 2025] Created new MidiTrack, instrument: Acoustic Bass Drum, channel: 9, percussive: true. Remaining instrumental channels: 14
``

Deleted the track I just made

``
[Wed Mar 26 19:10:09 PDT 2025] Removed MidiTrack[1]: drums maybe. Remaining instrumental channels: 14
``