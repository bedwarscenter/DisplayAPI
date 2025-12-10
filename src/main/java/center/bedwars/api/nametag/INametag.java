package center.bedwars.api.nametag;

public interface INametag {

    void create();

    void update(NametagData data);

    void remove();

    boolean isCreated();

    String getTeamName();
}
