package model.editing;

import java.util.ArrayList;
import java.util.List;

public class DawClipboard {
    private final List<Copyable> clipboardContents = new ArrayList<>();

    public void copy(List<Copyable> itemsToCopy) {
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