package ru.bortexel.bot.util.rules;

import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.List;

public class Rules {
    private final String name;
    @SerializedName("last_update")
    private final Timestamp lastUpdate;
    private final List<RulePart> parts;

    public Rules(String name, Timestamp lastUpdate, List<RulePart> parts) {
        this.name = name;
        this.lastUpdate = lastUpdate;
        this.parts = parts;
    }

    public String getName() {
        return name;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdate;
    }

    public List<RulePart> getParts() {
        return parts;
    }
}
