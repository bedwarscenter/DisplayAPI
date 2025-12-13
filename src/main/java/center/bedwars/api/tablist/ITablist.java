package center.bedwars.api.tablist;

public interface ITablist {

    void update(String header, String footer);

    void clear();

    boolean isUpdated();
}
