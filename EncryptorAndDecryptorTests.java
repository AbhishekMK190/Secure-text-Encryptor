public class EncryptorAndDecryptorTests {

    static int testCount = 0;
    static int passCount = 0;

    static void assertEquals(String expected, String actual, String testName) {
        testCount++;
        System.out.print("Test: " + testName + " - ");
        if (expected.equals(actual)) {
            System.out.println("PASS");
            passCount++;
        } else {
            System.out.println("FAIL");
            System.out.println("  Expected: \"" + expected + "\"");
            System.out.println("  Actual  : \"" + actual + "\"");
        }
    }

    static void assertNotEquals(String unexpected, String actual, String testName) {
        testCount++;
        System.out.print("Test: " + testName + " - ");
        if (!unexpected.equals(actual)) {
            System.out.println("PASS");
            passCount++;
        } else {
            System.out.println("FAIL");
            System.out.println("  Unexpected: \"" + unexpected + "\"");
            System.out.println("  Actual    : \"" + actual + "\"");
        }
    }


    public static void main(String[] args) {
        System.out.println("--- Running EncryptorAndDecryptor Tests ---");

        // Test 1: Basic Encryption and Decryption
        EncryptorAndDecryptor crypto1 = new EncryptorAndDecryptor(123);
        String originalText1 = "Hello World";
        String encryptedText1 = crypto1.Encryptor(originalText1);
        String decryptedText1 = crypto1.Decryptor(encryptedText1);
        assertEquals(originalText1, decryptedText1, "Basic Encryption/Decryption");

        // Test 2: Empty String
        EncryptorAndDecryptor crypto2 = new EncryptorAndDecryptor(456);
        String originalText2 = "";
        String encryptedText2 = crypto2.Encryptor(originalText2);
        String decryptedText2 = crypto2.Decryptor(encryptedText2);
        assertEquals(originalText2, decryptedText2, "Empty String Encryption/Decryption");

        // Test 3: Special Characters
        EncryptorAndDecryptor crypto3 = new EncryptorAndDecryptor(789);
        String originalText3 = "!@#$%^&*()_+[]{};':\",./<>?\\|`~";
        String encryptedText3 = crypto3.Encryptor(originalText3);
        String decryptedText3 = crypto3.Decryptor(encryptedText3);
        assertEquals(originalText3, decryptedText3, "Special Characters Encryption/Decryption");

        // Test 4: Test Key Influence (Simplified testDifferentKeysFocus)
        EncryptorAndDecryptor e_k1 = new EncryptorAndDecryptor(111);
        EncryptorAndDecryptor e_k2 = new EncryptorAndDecryptor(222);
        String plainTextForKeys = "Test Text for Key Influence";
        String encryptedByKey1 = e_k1.Encryptor(plainTextForKeys);
        String encryptedByKey2 = e_k2.Encryptor(plainTextForKeys);
        // With random numbers involved in encryption, even with different keys,
        // a simple notEquals might not be enough if lengths are different or stacks collide by chance.
        // However, the core idea is that they *should* produce different results.
        // A more robust test would require deeper inspection or disabling randomness.
        assertNotEquals(encryptedByKey1, encryptedByKey2, "Key Influence on Encryption Output (should differ)");
        // Also check decryption with original encryptor
        assertEquals(plainTextForKeys, e_k1.Decryptor(encryptedByKey1), "Key Influence - k1 decrypts k1");
        assertEquals(plainTextForKeys, e_k2.Decryptor(encryptedByKey2), "Key Influence - k2 decrypts k2");


        // Test 5: Test Instance Isolation
        EncryptorAndDecryptor instanceA = new EncryptorAndDecryptor(10);
        EncryptorAndDecryptor instanceB = new EncryptorAndDecryptor(20);

        String textA = "Message for Instance A";
        String encryptedA = instanceA.Encryptor(textA);

        String textB = "Message for Instance B, which is different.";
        String encryptedB = instanceB.Encryptor(textB);

        assertEquals(textA, instanceA.Decryptor(encryptedA), "Instance A Decryption");
        assertEquals(textB, instanceB.Decryptor(encryptedB), "Instance B Decryption");

        // Cross-decryption attempt (should ideally fail or produce garbage, not original text)
        // Since stack is instance specific, instanceB cannot decrypt instanceA's message
        String crossDecrypted_B_on_A = instanceB.Decryptor(encryptedA);
        assertNotEquals(textA, crossDecrypted_B_on_A, "Instance B cannot decrypt Instance A's message correctly");

        String crossDecrypted_A_on_B = instanceA.Decryptor(encryptedB);
        assertNotEquals(textB, crossDecrypted_A_on_B, "Instance A cannot decrypt Instance B's message correctly");


        System.out.println("\n--- EncryptorAndDecryptor Test Summary ---");
        System.out.println(passCount + " out of " + testCount + " tests passed.");
    }
}
