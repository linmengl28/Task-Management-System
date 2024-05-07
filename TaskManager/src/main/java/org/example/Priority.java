package org.example;

public enum Priority {
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    private final int value;

    Priority(int value) {
        this.value = value;
    }

    /**
     * Gets every defaulted value of Priority's instance
     * @return value defaulted value of Priority's instance
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Compares two priority object according to their value
     * @param value the default value of Priority instance
     * @return
     */
    public static Priority getPriority(int value) {
        for (Priority priority : Priority.values()) {
            if (priority.getValue() == value) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid priority value: " + value);
    }


}

        



