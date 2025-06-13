public class NoteTests {

    static int testCount = 0;
    static int passCount = 0;

    static void assertEquals(String expected, String actual, String testName) {
        testCount++;
        System.out.print("Test: " + testName + " - ");
        if (expected == null && actual == null || (expected != null && expected.equals(actual))) {
            System.out.println("PASS");
            passCount++;
        } else {
            System.out.println("FAIL");
            System.out.println("  Expected: \"" + expected + "\"");
            System.out.println("  Actual  : \"" + actual + "\"");
        }
    }

    static void assertEquals(long expected, long actual, String testName) {
        testCount++;
        System.out.print("Test: " + testName + " - ");
        if (expected == actual) {
            System.out.println("PASS");
            passCount++;
        } else {
            System.out.println("FAIL");
            System.out.println("  Expected: " + expected);
            System.out.println("  Actual  : " + actual);
        }
    }

    static void assertTrue(boolean condition, String testName) {
        testCount++;
        System.out.print("Test: " + testName + " - ");
        if (condition) {
            System.out.println("PASS");
            passCount++;
        } else {
            System.out.println("FAIL");
            System.out.println("  Condition was false.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("--- Running Note Tests ---");

        // Test 1: Note Content Cycle
        Note note1 = new Note("Test Title 1", "Initial Content", 123);
        assertEquals("Initial Content", note1.decryptContent(), "Initial Decryption");
        note1.encryptContent("Updated Content");
        assertEquals("Updated Content", note1.decryptContent(), "Updated Decryption");

        // Test 2: Time Capsule Logic
        long pastTime = System.currentTimeMillis() - 100000; // 100 seconds ago
        long futureTime = System.currentTimeMillis() + 100000; // 100 seconds in future

        Note notePast = new Note("Past Note", "Content P", 1, pastTime);
        Note noteFuture = new Note("Future Note", "Content F", 1, futureTime);
        Note noteNoTime = new Note("No Time Note", "Content NT", 1); // Uses constructor without reveal date

        assertTrue(notePast.getRevealDateMillis() == pastTime, "Past Note Reveal Date Set");
        // For testing, we check if current time is AFTER reveal time
        assertTrue(System.currentTimeMillis() >= notePast.getRevealDateMillis(), "Past Note Is Open");

        assertTrue(noteFuture.getRevealDateMillis() == futureTime, "Future Note Reveal Date Set");
        // For testing, we check if current time is BEFORE reveal time
        assertTrue(System.currentTimeMillis() < noteFuture.getRevealDateMillis(), "Future Note Is Locked");

        assertEquals(0, noteNoTime.getRevealDateMillis(), "No Time Note Has Zero Reveal Date");

        noteFuture.setRevealDateMillis(pastTime); // Change future note's reveal date to past
        assertTrue(noteFuture.getRevealDateMillis() == pastTime, "Future Note Reveal Date Changed to Past");
        assertTrue(System.currentTimeMillis() >= noteFuture.getRevealDateMillis(), "Future Note Set to Past Is Now Open");

        // Test 3: Title and Timestamps
        Note note3 = new Note("T1", "C1", 1);
        assertEquals("T1", note3.getTitle(), "Initial Title");
        long initialUpdate = note3.getUpdatedAt();
        long initialCreate = note3.getCreatedAt();

        Thread.sleep(50); // Ensure time passes for timestamp change, even a small amount

        note3.setTitle("T2");
        assertEquals("T2", note3.getTitle(), "Updated Title");
        assertTrue(note3.getUpdatedAt() > initialUpdate, "Timestamp Updated After Title Change");
        assertEquals(initialCreate, note3.getCreatedAt(), "CreatedAt Timestamp Unchanged After Title Change");

        Thread.sleep(50);
        initialUpdate = note3.getUpdatedAt();
        note3.encryptContent("Newer Content");
        assertTrue(note3.getUpdatedAt() > initialUpdate, "Timestamp Updated After Content Change");

        Thread.sleep(50);
        initialUpdate = note3.getUpdatedAt();
        note3.setRevealDateMillis(System.currentTimeMillis() + 50000);
        assertTrue(note3.getUpdatedAt() > initialUpdate, "Timestamp Updated After Reveal Date Change");


        System.out.println("\n--- Note Test Summary ---");
        System.out.println(passCount + " out of " + testCount + " tests passed.");
    }
}
