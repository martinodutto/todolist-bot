package com.martinodutto.services;

import org.telegram.telegrambots.api.objects.Update;

public interface InputHandler {

    String handleMessage(Update update);

    String handleEditedMessage(Update update);
}
