import java.util.Random;
import java.util.Stack;

public class EncryptorAndDecryptor {
    private int count = 0;
    private int key = 6;
    private Stack<Integer> RandomNumStack = new Stack<Integer>();

    public EncryptorAndDecryptor(int key) {
        this.key = key;
        this.RandomNumStack = new Stack<Integer>();
        this.count = 0;
    }

    //Encryptor method
    public String Encryptor(String text){
        Random random = new Random();

        char[] charecters = text.toCharArray();
        for(int i = 0 ; i<charecters.length ; i++){
            int number = random.nextInt(255);
            RandomNumStack.push(number);
            charecters[i] =(char)(charecters[i] + key + number);
        }
        count++;

        String text2 = new String(charecters);
        char[] charecters2 = text2.toCharArray();
        for(int i = 0 ; i<charecters2.length ; i++){
            int number2 = random.nextInt(255);
            RandomNumStack.push(number2);
            charecters2[i]  = (char)(charecters2[i] + key + number2);
        }
        count++;
        return new String(charecters2);
    }

    //Decryptor method

    public String Decryptor(String text){
        char[] decrypt1 = text.toCharArray();
        while(count-->0){
            for(int i = decrypt1.length-1 ; i>=0; i--){
                int DecryptNumber = RandomNumStack.pop();
                decrypt1[i] = (char)(decrypt1[i] - (key + DecryptNumber));
            }
        }
        return new String(decrypt1);
    }

    public static void main(String[] args) {
        String text = "Hello, How are You!!";
        EncryptorAndDecryptor crypto = new EncryptorAndDecryptor(6);
        String EncryptedMessage = crypto.Encryptor(text);
        System.out.println("Encrypted: " + EncryptedMessage);
        String DecryptedMessage = crypto.Decryptor(EncryptedMessage);
        System.out.println("Decrypted: " + DecryptedMessage);
    }
}
