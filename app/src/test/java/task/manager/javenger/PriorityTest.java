package task.manager.javenger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PriorityTest {
    @Test
    public void testGetValue() {
        assertEquals(1, Priority.LOW.getValue());
        assertEquals(2, Priority.MEDIUM.getValue());
        assertEquals(3, Priority.HIGH.getValue());
    }

    @Test
    public void testGetPriority() {
        assertEquals(Priority.LOW, Priority.getPriority(1));
        assertEquals(Priority.MEDIUM, Priority.getPriority(2));
        assertEquals(Priority.HIGH, Priority.getPriority(3));
    }

    @Test
    public void testGetPriorityWithInvalidValue() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> Priority.getPriority(4));
    }


}
