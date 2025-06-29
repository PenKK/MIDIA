package model.util;

import java.util.List;

public interface Pastable extends Selectable {

    void paste(List<Copyable> copiedItems, long position);

}
