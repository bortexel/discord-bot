package ru.bortexel.bot.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextUtilTest {
    @Test
    void removeDoubleSpaces() {
        assertEquals(" ", TextUtil.removeDoubleSpaces("          "));
        assertEquals("Привет, мир! ", TextUtil.removeDoubleSpaces("Привет,  мир! "));
    }
}