package ru.bortexel.bot.util;

import ru.ruscalworld.bortexel4j.util.Location;

public class MapReference {
    private final Location location;
    private final String server;

    public MapReference(Location location, String server) {
        this.location = location;
        this.server = server;
    }

    public String getBlueMapLink(int scale) {
        Location loc = this.getBlueMapLocation();
        return String.format(
                "https://map.bortexel.ru/%s/#%s:%s:%s:%s:%s:0.6:0.6:0:0:perspective",
                this.getServer(), loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), scale
        );
    }

    public String getBlueMapLinkMarkdown(int scale) {
        return String.format("[`[Открыть карту]`](%s)", this.getBlueMapLink(scale));
    }

    private static String getBlueMapName(String worldName) {
        if (worldName.equals("world_nether")) return "nether";
        return worldName;
    }

    public Location getBlueMapLocation() {
        location.setWorld(getBlueMapName(location.getWorld()));
        return location;
    }

    public Location getLocation() {
        return location;
    }

    public String getServer() {
        return server;
    }
}
