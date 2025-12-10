package center.bedwars.api.tablist;

import java.util.List;

public interface ITablist {

    void update(String header, String footer);

    void clear();

    boolean isUpdated();
}
