package me.lauriichan.laylib.localization.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import me.lauriichan.laylib.localization.Key;
import me.lauriichan.laylib.localization.MessageManager;

public class PlaceholderTest {

    private final MessageManager messageManager = new MessageManager();

    @Test
    public void test() {
        assertEquals("Once upon a there_is_no_bread_time there was world domination.", messageManager.format(
            "Once upon a $there;_time there was $hello;domination.", "", Key.of("hello", "world "), Key.of("there", "there_is_no_bread")));
    }

}
