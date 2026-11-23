package io.github.nicolasgoyon.runehold;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RuneholdTest {
    @Test
    void modIdIsRunehold() {
        assertEquals("runehold", Runehold.MOD_ID);
    }
}
