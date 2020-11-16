package ru.bortexel.bot.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextUtilTest {
    @Test
    void removeDoubleSpaces() {
        assertEquals(" ", TextUtil.removeDoubleSpaces("          "));
        assertEquals("Привет, мир! ", TextUtil.removeDoubleSpaces("Привет,  мир! "));
    }

    @Test
    void getPlural() {
        assertEquals("яблок", TextUtil.getPlural(0, "яблоко", "яблока", "яблок"));
        assertEquals("яблоко", TextUtil.getPlural(1, "яблоко", "яблока", "яблок"));
        assertEquals("яблока", TextUtil.getPlural(2, "яблоко", "яблока", "яблок"));
        assertEquals("яблока", TextUtil.getPlural(4, "яблоко", "яблока", "яблок"));
        assertEquals("яблок", TextUtil.getPlural(5, "яблоко", "яблока", "яблок"));
        assertEquals("яблок", TextUtil.getPlural(9, "яблоко", "яблока", "яблок"));
        assertEquals("яблок", TextUtil.getPlural(10, "яблоко", "яблока", "яблок"));
        assertEquals("яблок", TextUtil.getPlural(11, "яблоко", "яблока", "яблок"));
        assertEquals("яблок", TextUtil.getPlural(12, "яблоко", "яблока", "яблок"));
        assertEquals("яблок", TextUtil.getPlural(14, "яблоко", "яблока", "яблок"));
        assertEquals("яблок", TextUtil.getPlural(15, "яблоко", "яблока", "яблок"));
        assertEquals("яблок", TextUtil.getPlural(19, "яблоко", "яблока", "яблок"));
        assertEquals("яблок", TextUtil.getPlural(20, "яблоко", "яблока", "яблок"));
        assertEquals("яблоко", TextUtil.getPlural(21, "яблоко", "яблока", "яблок"));
        assertEquals("яблока", TextUtil.getPlural(22, "яблоко", "яблока", "яблок"));
        assertEquals("яблока", TextUtil.getPlural(24, "яблоко", "яблока", "яблок"));
        assertEquals("яблок", TextUtil.getPlural(25, "яблоко", "яблока", "яблок"));
        assertEquals("яблок", TextUtil.getPlural(29, "яблоко", "яблока", "яблок"));
        assertEquals("яблок", TextUtil.getPlural(30, "яблоко", "яблока", "яблок"));
    }

    @Test
    void makeProgressBar() {
        assertEquals("[===]", TextUtil.makeProgressBar(50, 10, "[", "=", "]", "."));
        assertEquals(".", TextUtil.makeProgressBar(0, 10, "[", "=", "]", "."));
    }
}