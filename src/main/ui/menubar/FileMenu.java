package ui.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Timeline;
import persistance.JsonReader;
import persistance.JsonWriter;

// The File dropdown Menu, responsible for saving and loading and other file settings
public class FileMenu extends Menu implements ActionListener {

    private static final String PROJECTS_DIRECTORY = "data/projects";

    private JMenuItem open;
    private JMenuItem save;

    private JFileChooser fileChooser;


    // EFFECTS: creates file JMenu, its file chooser, its JMenuItems, and apppriate action listeners
    FileMenu() {
        super();
        this.setText("File");

        open = new JMenuItem("Open Project");
        save = new JMenuItem("Save Project");

        this.add(open);
        this.add(save);
        open.addActionListener(this);
        save.addActionListener(this);

        FileFilter jsonFilter = new FileNameExtensionFilter("JSON file","json");

        UIManager.put("FileChooser.readOnly", Boolean.TRUE);  
        fileChooser = new JFileChooser(PROJECTS_DIRECTORY);
        fileChooser.setFileFilter(jsonFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);
    }

    // EFFECTS: Assigns actions to methods
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == open) {
            openProject();
        } else if (e.getSource() == save) {
            saveProject();
        }
    }

    // MODIFIES: Timeline (singleton instance)
    // EFFECTS: Prompts user to pick a project json file to load
    public void openProject() {
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.CANCEL_OPTION || result == JFileChooser.ERROR_OPTION) {
            return;
        }

        String path = fileChooser.getSelectedFile().getPath();
        JsonReader reader = new JsonReader(path);
        
        try {
            Timeline newTimeline = reader.read();
            Timeline.setInstance(newTimeline);
        } catch (IOException | MidiUnavailableException e) {
            System.out.println("Unable to load file");
        } catch (InvalidMidiDataException e) {
            System.out.println("The file had invalid MIDI data, cannot load");
        }
    }

    // MODIFIES: projects folder
    // EFFECTS: saves current project instance to a prompted path
    public void saveProject() {
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
}