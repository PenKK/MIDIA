package ui.menubar.menus;

import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.InvalidPathException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Timeline;
import model.TimelineController;
import persistance.JsonReader;
import persistance.JsonWriter;

// The File dropdown Menu, responsible for saving and loading and other file settings
public class FileMenu extends Menu {

    public static final String PROJECTS_DIRECTORY = "data/projects";
    public static final String AUTO_SAVE_FILE_DIRECTORY = PROJECTS_DIRECTORY.concat("/autosave/");

    private TimelineController timelineController;
    private MenuItem open;
    private MenuItem save;
    private MenuItem newProject;
    private MenuItem delete;

    private JFileChooser fileChooser;
    private JsonReader reader;
    private JsonWriter writer;

    // EFFECTS: creates file JMenu, its file chooser, its JMenuItems, and apppriate action listeners
    public FileMenu(TimelineController timelineController) {
        super("File");

        this.timelineController = timelineController;
        open = new MenuItem("Open Project", this);
        save = new MenuItem("Save Project", this);
        newProject = new MenuItem("New Project", this);
        delete = new MenuItem("Delete a Project", this);

        FileFilter jsonFilter = new FileNameExtensionFilter("JSON file", "json");

        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        fileChooser = new JFileChooser(PROJECTS_DIRECTORY);
        fileChooser.setFileFilter(jsonFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);
    }

    // EFFECTS: Assigns actions to methods
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(newProject)) {
            newProject();
        } else if (e.getSource().equals(open)) {
            openProject();
        } else if (e.getSource().equals(save)) {
            saveProject();
        } else if (e.getSource().equals(delete)) {
            deleteProject();
        }
    }

    // MODIFIES: timeline singleton
    // EFFECTS: assigns the instance a new Timeline 
    private void newProject() {
        try {
            timelineController.setInstance(new Timeline("New Project", timelineController.getPropertyChangeSupport()));
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            System.out.println("Unable to create and set new Timline instance");
            e.printStackTrace();
        }
    }

    // MODIFIES: timelineController
    // EFFECTS: Prompts user to pick a project json file to load
    private void openProject() {
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.CANCEL_OPTION || result == JFileChooser.ERROR_OPTION) {
            return;
        }

        String path = fileChooser.getSelectedFile().getPath();
        reader = new JsonReader(path);

        try {
            Timeline newTimeline = reader.read();
            newTimeline.setPropertyChangeSupport(timelineController.getPropertyChangeSupport());
            timelineController.setInstance(newTimeline);
        } catch (IOException | MidiUnavailableException | InvalidPathException e) {
            System.out.println("Unable to load file");
        } catch (InvalidMidiDataException e) {
            System.out.println("The file had invalid MIDI data, cannot load");
        }
    }

    // MODIFIES: projects folder
    // EFFECTS: saves current project instance to a prompted path
    private void saveProject() {
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.CANCEL_OPTION || result == JFileChooser.ERROR_OPTION) {
            return;
        }

        String path = fileChooser.getSelectedFile().getPath();
        writer = new JsonWriter(path);

        try {
            Timeline timeline = timelineController.getTimeline();
            timeline.setProjectName(fileChooser.getSelectedFile().getName());

            writer.open();
            writer.write(timeline);
            writer.close();

        } catch (FileNotFoundException e) {
            System.out.println("Invalid path in Project Save");
        }
    }

    // MODIFIES: Timeline instance, projects folder
    // EFFECTS: prompts user to delete a project with JFileChooser
    private void deleteProject() {
        int result = fileChooser.showDialog(this, "Delete Project");

        if (result == JFileChooser.CANCEL_OPTION || result == JFileChooser.ERROR_OPTION) {
            return;
        }

        fileChooser.getSelectedFile().delete();
    }
}