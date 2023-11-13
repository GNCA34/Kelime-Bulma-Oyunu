package com.gonca.myapplication;
import java.io.BufferedReader;
        import java.io.FileReader;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.regex.Pattern;

public class KelimeHavuzu {
    public static void main(String[] args) {
        String fileName = "kelimeler.txt";
        ArrayList<String> words = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Kelime sayısı 50.000 den fazla kelime içermesi gereklidir
        if (words.size() < 50000) {
            throw new RuntimeException("Kelime sayısı 50.000 den az");
        }

        words.removeIf(word -> word.length() < 3);

        // Havuzdaki kelimeler tek bir kelime şeklinde olmalıdır
        words.removeIf(word -> word.contains(" "));


        Pattern pattern = Pattern.compile("^[a-zçğıöşü]+$", Pattern.CASE_INSENSITIVE);
        words.removeIf(word -> !pattern.matcher(word).matches());


        try (FileWriter fw = new FileWriter("filtrelenmis_kelime_dosyasi.txt")) {
            for (String word : words) {
                fw.write(word + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}
