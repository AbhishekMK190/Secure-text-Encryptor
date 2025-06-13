import java.util.Date;

public class Note {
    private String title;
    private String encryptedContent;
    private long createdAt;
    private long updatedAt;
    private EncryptorAndDecryptor cryptoHelper;
    private long revealDateMillis = 0;

    public Note(String title, String initialContent, int encryptionKey) {
        this.title = title;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.cryptoHelper = new EncryptorAndDecryptor(encryptionKey);
        this.revealDateMillis = 0; // Default reveal date
        encryptContent(initialContent);
    }

    public Note(String title, String initialContent, int encryptionKey, long revealDateMillis) {
        this(title, initialContent, encryptionKey); // Call main constructor
        this.setRevealDateMillis(revealDateMillis);
    }


    public long getRevealDateMillis() {
        return revealDateMillis;
    }

    public void setRevealDateMillis(long revealDateMillis) {
        this.revealDateMillis = revealDateMillis;
        this.updatedAt = System.currentTimeMillis();
    }

    public void encryptContent(String plainContent) {
        this.encryptedContent = cryptoHelper.Encryptor(plainContent);
        this.updatedAt = System.currentTimeMillis();
    }

    public String decryptContent() {
        return cryptoHelper.Decryptor(this.encryptedContent);
    }

    public String getTitle() {
        return title;
    }

    public String getEncryptedContent() {
        return encryptedContent;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = System.currentTimeMillis();
    }

    public static void main(String[] args) {
        Note note = new Note("My Secret Note", "This is a secret message.", 123);
        System.out.println("Title: " + note.getTitle());
        System.out.println("Original Content: " + note.decryptContent());

        note.setTitle("My Updated Secret Note");
        System.out.println("Updated Title: " + note.getTitle());

        note.encryptContent("This is a new secret message.");
        System.out.println("New Encrypted Content: " + note.getEncryptedContent());
        System.out.println("New Decrypted Content: " + note.decryptContent());

        System.out.println("Created At: " + new Date(note.getCreatedAt()));
        System.out.println("Updated At: " + new Date(note.getUpdatedAt()));
    }
}
