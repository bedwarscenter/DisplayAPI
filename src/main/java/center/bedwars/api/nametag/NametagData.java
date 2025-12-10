package center.bedwars.api.nametag;

public record NametagData(String prefix, String suffix, String name, int priority) {

    public static NametagData empty(String playerName) {
        return new NametagData("", "", playerName, 0);
    }

    public static Builder builder(String playerName) {
        return new Builder(playerName);
    }

    public static class Builder {
        private String prefix = "";
        private String suffix = "";
        private final String name;
        private int priority = 0;

        public Builder(String name) {
            this.name = name;
        }

        public Builder prefix(String prefix) {
            this.prefix = prefix != null ? prefix : "";
            return this;
        }

        public Builder suffix(String suffix) {
            this.suffix = suffix != null ? suffix : "";
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public NametagData build() {
            return new NametagData(prefix, suffix, name, priority);
        }
    }
}
