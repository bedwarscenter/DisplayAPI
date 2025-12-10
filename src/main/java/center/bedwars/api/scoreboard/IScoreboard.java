package center.bedwars.api.scoreboard;

import java.util.List;

public interface IScoreboard {

    void create();

    void update(String title, List<String> lines);

    void updateTitle(String title);

    void updateLines(List<String> lines);

    void remove();

    boolean isCreated();

    int getLineCount();
}
