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
import persistance.JsonReader;
import persistance.JsonWriter;

// The File dropdown Menu, responsible for saving and loading and other file settings
public class FileMenu extends Menu {

    private static final String PROJECTS_DIRECTORY = "data/projects";

    private MenuItem open;
    private MenuItem save;
    private MenuItem newProject;
    private MenuItem delete;

    private JFileChooser fileChooser;

    // EFFECTS: creates file JMenu, its file chooser, its JMenuItems, and apppriate action listeners
    public FileMenu() {
        super("File");
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
            Timeline.setInstance(new Timeline("New Project"));
        } catch (MidiUnavailableException | InvalidMidiDataException e) {
            System.out.println("Unable to create and set new Timline instance");
            e.printStackTrace();
        }
    }

    // MODIFIES: Timeline (singleton instance)
    // EFFECTS: Prompts user to pick a project json file to load
    private void openProject() {
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.CANCEL_OPTION || result == JFileChooser.ERROR_OPTION) {
            return;
        }

        String path = fileChooser.getSelectedFile().getPath();
        JsonReader reader = new JsonReader(path);

        try {
            Timeline newTimeline = reader.read();
            Timeline.setInstance(newTimeline);
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
        JsonWriter writer = new JsonWriter(path);

        try {
            Timeline instance = Timeline.getInstance();
            instance.setProjectName(fileChooser.getSelectedFile().getName());

            writer.open();
            writer.write(instance);
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

        try {
            Timeline.setInstance(new Timeline("New Project"));
        } catch (MidiUnavailableException e) {
            System.out.println("MIDI device unavaliable");
            e.printStackTrace();
        } catch (InvalidMidiDataException e) {
            System.out.println("Unable to create new timeline, invalid MIDI data found");
            e.printStackTrace();
        }
    }
}