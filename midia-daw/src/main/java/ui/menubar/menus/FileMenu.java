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

import org.json.JSONException;

import model.Timeline;
import model.TimelineController;
import persistance.JsonReader;
import persistance.JsonWriter;
import persistance.OSPathResolver;

/**
 * The File menu, responsible for opening, saving, creating, and deleting projects.
 */
public class FileMenu extends Menu {

    public static String PROJECTS_DIRECTORY = OSPathResolver.getProjectsDirectory().toString();
    public static String AUTO_SAVE_FILE_DIRECTORY = PROJECTS_DIRECTORY.concat("/autosave/");

    private final MenuItem open;
    private final MenuItem save;
    private final MenuItem newProject;
    private final MenuItem delete;

    private final JFileChooser fileChooser;

    /**
     * Constructs the File menu with menu items and a JSON-only file chooser.
     *
     * @param timelineController the controller used to handle menu actions
     */
    public FileMenu(TimelineController timelineController) {
        super("File", timelineController);
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

    /**
     * Routes menu actions to the appropriate handlers.
     */
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

    /**
     * Creates a new project with default settings.
     */
    private void newProject() {
        timelineController.setInstance(new Timeline("New Project", timelineController.getPropertyChangeSupport()));
    }

    /**
     * Prompts the user to open a project JSON file and loads it into the application.
     */
    private void openProject() {
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.CANCEL_OPTION || result == JFileChooser.ERROR_OPTION) {
            return;
        }

        String path = fileChooser.getSelectedFile().getPath();
        JsonReader reader = new JsonReader(path);

        try {
            Timeline newTimeline = reader.read(timelineController.getPropertyChangeSupport());
            timelineController.setInstance(newTimeline);
        } catch (JSONException e) {
            System.out.printf("Invalid JSON data at path %s%n", path);
        } catch (IOException | MidiUnavailableException | InvalidPathException e) {
            System.out.println("Unable to load file");
        } catch (InvalidMidiDataException e) {
            System.out.println("The file had invalid MIDI data, cannot load");
        }
    }

    /**
     * Saves the current project to a chosen file path.
     */
    private void saveProject() {
        int result = fileChooser.showSaveDialog(this);

        if (result == JFileChooser.CANCEL_OPTION || result == JFileChooser.ERROR_OPTION) {
            return;
        }

        String path = fileChooser.getSelectedFile().getPath();
        JsonWriter writer = new JsonWriter(path);

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

    /**
     * Prompts the user to delete an existing project file.
     */
    private void deleteProject() {
        int result = fileChooser.showDialog(this, "Delete Project");

        if (result == JFileChooser.CANCEL_OPTION || result == JFileChooser.ERROR_OPTION) {
            return;
        }

        if (!fileChooser.getSelectedFile().delete()) {
            throw new RuntimeException("Unable to delete project file at path "
                                       + fileChooser.getSelectedFile().getPath());
        }
    }
}