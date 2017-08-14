package com.martinodutto.services;

import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.api.objects.Update;

/**
 * Any user input handler should implement this contract.
 */
public interface InputHandler {

    /**
     * Takes a user message, processes it and then returns a {@link String} that represents the bot response.
     *
     * @param update Object representing the user input.
     * @return Bot response for the input.
     */
    @Nullable String handleMessage(Update update);

    /**
     * Takes a user-edited message, processes it and then returns a {@link String} that represents the bot response.
     *
     * @param update Object representing the edited user input.
     * @return Bot response for the input.
     */
    String handleEditedMessage(Update update);
}
