package com.martinodutto.enums;

import com.sun.istack.internal.Nullable;

import java.util.HashMap;

public enum Commands {
    START_ME_UP("start"),
    HELP("help"),
    READ_LIST("read"),
    DELETE("delete");

    private static HashMap<String, Commands> map;

    static {
        map = new HashMap<>();
        for (Commands command : Commands.values()) {
            map.put(command.getInstruction(), command);
        }
    }

    private String instruction;

    Commands(String instruction) {
        this.instruction = instruction;
    }

    public String getInstruction() {
        return instruction;
    }

    @Nullable
    public static Commands getFromInstruction(String instruction) {
        return map.get(instruction);
    }
}
