package com.martinodutto.types;

import com.martinodutto.exceptions.UnknownCommandException;
import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Command {

    private Commands kindOf;

    private List<String> parameters = new ArrayList<>();

    public Command(Commands kindOf) {
        this.kindOf = kindOf;
    }

    public Command(String instruction) throws UnknownCommandException {
        this.kindOf = Commands.getFromInstruction(instruction);
    }

    public Commands getKindOf() {
        return kindOf;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void addParameter(String parameter) {
        this.parameters.add(parameter);
    }

    public enum Commands {
        START_ME_UP("start"),
        HELP("help"),
        READ_LIST("read"),
        DELETE("delete"),
        EDIT("edit");

        private String instruction;

        private static HashMap<String, Commands> map;

        static {
            map = new HashMap<>();
            for (Commands command : Commands.values()) {
                map.put(command.getInstruction(), command);
            }
        }

        Commands(String instruction) {
            this.instruction = instruction;
        }

        @NotNull
        public static Commands getFromInstruction(String instruction) throws UnknownCommandException {
            if (map.get(instruction) != null) {
                return map.get(instruction);
            } else {
                throw new UnknownCommandException();
            }
        }

        public String getInstruction() {
            return instruction;
        }
    }

    public boolean validateParameters() {
        boolean valid = true;
        switch (kindOf) {
            case EDIT: {
                final List<String> parameters = getParameters();

                valid &= (parameters.size() == 2);

                if (valid) {
                    try {
                        Long.parseLong(parameters.get(0));
                    } catch (NumberFormatException nfe) {
                        valid &= false;
                    }
                }

                break;
            }
            default: {
                valid &= true;
            }
        }

        return valid;
    }
}
