package ui;

import java.awt.Rectangle;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JFrame;

import ui.menubar.MenuBar;
import ui.tabs.TabbedPane;

// The frame of the graphical UI. Contains the entirety of the UI.
public class DawFrame extends JFrame {

    private MenuBar menuBar;
    private TabbedPane tabbedPane;
    private DawController controller;

}
