package ui.util;

import java.util.ArrayList;
import java.util.List;

import model.util.Copyable;

public class DawClipboard {
    private List<Copyable> clipboardContents = new ArrayList<>();

    public void setContents(List<Copyable> itemsToCopy) {
        clipboardContents.clear();
        for (Copyable item : itemsToCopy) {
            clipboardContents.add(item.clone());
        }
    }

    public List<Copyable> getContents() {
        return new ArrayList<>(clipboardContents);
    }

    public boolean isEmpty() {
        return clipboardContents.isEmpty();
    }
}