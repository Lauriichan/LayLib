package me.lauriichan.laylib.localization;

import java.util.UUID;

public interface IMessageReceiver {

    UUID getId();

    String getLanguage();

    void sendMessage(String message);

}
