import javax.swing.text.html.Option;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Main {
    private static final String ENG_LETTERS = "aeopcyxABEKMHOPCTX";
    private static final String RUS_LETTERS = "аеорсухАВЕКМНОРСТХ";

    public static void main(String[] args) throws IOException {

        String message="src/message.txt";
        String output="src/output.txt";
        String input="src/input.txt";
//        for (int i = 0; i < args.length; i++) {
//            System.out.println("Argument " + i + ": " + args[i]);
//        }
//
        System.out.println(args.length);
        for(int i=0;i<args.length;i++){
            if (args[i].equals("-h") || args[i].equals("--help")){
                System.out.println("-m --message - путь к файлу с " +
                        "сообщением.\n -s --stego - путь к файлу с начальным " +
                        "текстом файлу.\n -c --container путь к файлу в к");
            }
            if (args[i].equals("-m") || args[i].equals("--message")){
                message = args[i+1];
                System.out.println("Файл сообщения по пути" + message);
            }
            if (args[i].equals("-s") || args[i].equals("--stego")){
                input = args[i+1];
                System.out.println("Файл с входным сообщением по пути" + message);
            }
            if (args[i].equals("-c") || args[i].equals("--container")){
                output = args[i+1];
                System.out.println("Файл с выходящем сообщением по пути" + message);
            }
            System.out.println("Argument " + i + ": " + args[i]);
            System.out.println(args[i].length());
        }
        // -m /home/nikita/JavaProject/infSecurity/src/message.txt
        // -s /home/nikita/JavaProject/infSecurity/src/input.txt
        // -c /home/nikita/JavaProject/infSecurity/src/output.txt

        encode(input, message, output);
        uncode(message, output);
    }

    public static void encode(String in, String msg, String out) throws IOException {

        File input = new File(in);
        FileReader fileReader = new FileReader(input, StandardCharsets.UTF_8);
//      Reader reader = new InputStreamReader(System.in);

        RandomAccessFile randomAccessFile = new RandomAccessFile("src/message.txt", "r");

        File messageFile = new File(msg);
        FileReader fileReaderMsg = new FileReader(messageFile,
                StandardCharsets.UTF_8);
        //String message = "";
        List<Byte> message = new ArrayList<>();

        System.out.println(messageFile.length());

        while (true) {
//            int nowChar = fileReaderMsg.read();
            try {
                byte nowChar = randomAccessFile.readByte();

                message.add(nowChar);
            }
            catch (Exception e){
                break;
            }
//            if (nowChar == '\uFFFF' || nowChar == -1) {
//                break;
//            }

        }

        message.add((byte)'$');

        File outputFile = new File(out);
        FileWriter fileWriter = new FileWriter(outputFile, StandardCharsets.UTF_8);

        try {
            boolean endEncoding = true;
            while (true) {
                if (endEncoding)
                    for (int i = 0; i < message.size(); i++) {
                        endEncoding = false;
                        byte nowCharMsg = (byte) message.get(i);

                        for (int j = 7; j >= 0; j--) {
                            boolean howEdit;

                            if ((nowCharMsg >> j & 1) == 1)
                                howEdit = true;
                            else
                                howEdit = false;

                            while (true) {
                                char nowChar = (char) fileReader.read();

                                if (nowChar == '\uFFFF') {
                                    break;
                                }

                                if (ENG_LETTERS.indexOf(nowChar) >= 0 || RUS_LETTERS.indexOf(nowChar) >= 0) {
                                    if (!howEdit) {
                                        System.out.println("Символ сообщения " +
                                                nowCharMsg + " " + (byte)nowCharMsg +
                                                " Кодируется " +
                                                "0");
                                        if (RUS_LETTERS.indexOf(nowChar) >= 0)
                                            fileWriter.write(ENG_LETTERS.charAt(RUS_LETTERS.indexOf(nowChar)));
                                        else
                                            fileWriter.write(nowChar);
                                        break;
                                    } else {
                                        System.out.println("Символ сообщения " +
                                                nowCharMsg + " " + (byte)nowCharMsg +
                                                " Кодируется " +
                                                "1");
                                        if (ENG_LETTERS.indexOf(nowChar) >= 0)
                                            fileWriter.write(RUS_LETTERS.charAt(ENG_LETTERS.indexOf(nowChar)));
                                        else
                                            fileWriter.write(nowChar);
                                        break;
                                    }
                                } else {
                                    fileWriter.write(nowChar);
                                }
                            }
                        }
                    }
                else {
                    char nowChar = (char) fileReader.read();

                    if (nowChar == '\uFFFF') {
                        break;
                    }

                    if (RUS_LETTERS.indexOf(nowChar) >= 0)
                        fileWriter.write(ENG_LETTERS.charAt(RUS_LETTERS.indexOf(nowChar)));
                    else
                        fileWriter.write(nowChar);
                }
            }
        }
        finally {
            fileWriter.close();
        }
    }
    public static void uncode(String msg, String out) throws IOException {
        File input = new File(out);
        FileReader fileReader = new FileReader(input, StandardCharsets.UTF_8);

        File outputFile = new File(msg);
        FileWriter fileWriter = new FileWriter(outputFile, StandardCharsets.UTF_8);

        RandomAccessFile randomAccessFile = new RandomAccessFile("src/message" +
                ".txt", "rw");

        byte[] t = new byte[3];
        t[0]=(byte)128;
        t[1]=1;
        t[2]=0;
        ArrayList<Byte> listByte = new ArrayList<>();


        try {
            int nowCharMsg=0;
            int cntByte = 0;
            while (true) {
                char nowChar = (char) fileReader.read();

                if (nowChar == '\uFFFF') {
                    break;
                }
                if (RUS_LETTERS.indexOf(nowChar) >= 0) {
                    nowCharMsg = (nowCharMsg << 1) + 1;
                    cntByte++;
                }
                if (ENG_LETTERS.indexOf(nowChar) >= 0) {
                    nowCharMsg = (nowCharMsg << 1);
                    cntByte++;
                }
                if (cntByte > 7){
                    //if (nowCharMsg!=0) {
                        listByte.add((byte) nowCharMsg);
                        System.out.println("Декодирован символ " + (byte) nowCharMsg + " " + nowCharMsg);
                    //}
                    cntByte = nowCharMsg = 0;
                }
            }
            int lastDollar = 0;
            for(int i =0;i<listByte.size();i++){
                if (listByte.get(i).equals((byte)'$')){
                    lastDollar = i;
                }
            }

            for(int i = 0; i < lastDollar; i++) {
                randomAccessFile.write(listByte.get(i));
            }
        }
        finally {
            fileWriter.close();
        }
    }

}