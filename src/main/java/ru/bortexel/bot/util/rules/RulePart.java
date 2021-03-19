package ru.bortexel.bot.util.rules;

import java.util.ArrayList;
import java.util.List;

public class RulePart {
    private final String name;
    private final int number;
    private final String description;
    private final List<Rule> rules;

    public RulePart(String name, int number, String description, List<Rule> rules) {
        this.name = name;
        this.number = number;
        this.description = description;
        this.rules = rules;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public int getNumber() {
        return number;
    }

    public static class Rule {
        private final String name;
        private final String text;
        private final List<Rule> rules;

        public Rule(String name, String text, List<Rule> rules) {
            this.name = name;
            this.text = text;
            this.rules = rules;
        }

        public String getName() {
            return name;
        }

        public String getText() {
            return text.replace("&nbsp;", " ");
        }

        public List<Rule> getRules() {
            return rules;
        }

        public String render(int level) {
            List<String> output = new ArrayList<>() {{
                add("║ ".repeat(level) + "**" + getName() + ".** " + getText());
            }};

            if (this.getRules() != null) for (Rule subrule : this.getRules()) output.add(subrule.render(level + 1));
            return String.join("\n", output);
        }
    }
}
