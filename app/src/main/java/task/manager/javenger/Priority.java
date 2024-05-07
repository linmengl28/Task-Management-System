package task.manager.javenger;

/**
 * Enumerates the levels of importance and priority for each tasks.
 */
public enum Priority {
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    private final int value;

    /**
     * Constructs the Priority instance by a specified int.
     *
     * @param value the integer of the priority level.
     */
    Priority(int value) {
        this.value = value;
    }

    /**
     * Gets every defaulted value of Priority's instance.
     * @return value defaulted value of Priority's instance.
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Gets the Priority instance corresponding to this int value.
     *
     * @param value the int value belong to each enum Priority instance.
     * @return a Priority object.
     * @throws IllegalArgumentException if the provided value does not belong to any of the Priority instance.
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

        



