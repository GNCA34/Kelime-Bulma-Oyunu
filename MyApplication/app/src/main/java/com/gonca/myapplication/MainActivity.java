package com.gonca.myapplication;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private static final int ROW_COUNT = 10;
    private static final int COL_COUNT = 8;
    private char[][] letters = new char[COL_COUNT][ROW_COUNT];
    private Random random = new Random();
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isFalling = false;
    private int count = 0;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private String selected_letter;
    private String selectedLetters = "";
    //List<String> words = new ArrayList<>();
    //private List<String> words;
    // ...
    private TextView resultTextView;
    private String kelime = "abcde"; // örnek olarak atama yapıldı, istediğiniz harfleri yazabilirsiniz


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TableLayout tableLayout = findViewById(R.id.table_layout);
        generateInitialLetters();
        drawTable(tableLayout);
        startFalling();
        TextView selectedLetterTextView;
        selectedLetterTextView = findViewById(R.id.selected_letter);



// Harflerin puanlarına ait HashMap
        HashMap<Character, Integer> points = new HashMap<Character, Integer>();
        points.put('a', 1);
        points.put('b', 3);
        points.put('c', 4);
        points.put('ç', 4);
        points.put('d', 3);
        points.put('e', 1);
        points.put('f', 7);
        points.put('g', 5);
        points.put('ğ', 8);
        points.put('h', 5);
        points.put('ı', 2);
        points.put('i', 1);
        points.put('j', 10);
        points.put('k', 1);
        points.put('l', 1);
        points.put('m', 2);
        points.put('n', 1);
        points.put('o', 2);
        points.put('ö', 7);
        points.put('p', 5);
        points.put('r', 1);
        points.put('s', 2);
        points.put('ş', 4);
        points.put('t', 1);
        points.put('u', 2);
        points.put('ü', 3);
        points.put('v', 7);
        points.put('y', 3);
        points.put('z', 4);
//...



// Sonucun gösterilmesi
        TextView sonucalani = findViewById(R.id.resultTextView);


        ArrayList<String> words = new ArrayList<>();

        try {
            InputStream is = getAssets().open("filtrelenmis_kelime_dosyası.txt");
            Scanner scanner = new Scanner(is);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] splitWords = line.split("\\s+");
                for (String word : splitWords) {
                    words.add(word);
                }
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // set onTouchListener for each TextView in the table
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COL_COUNT; col++) {
                final TextView textView = (TextView) ((TableRow) tableLayout.getChildAt(ROW_COUNT - row - 1)).getChildAt(col + 1);
                textView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                // get selected row and column
                                selectedRow = ROW_COUNT  - tableLayout.indexOfChild((TableRow) v.getParent());
                                selectedCol = ((TableRow) v.getParent()).indexOfChild(v)-1 ;
                                // change the background color of the selected TextView
                                textView.setBackgroundColor(Color.RED);
                                TextView selectedLetterView = findViewById(R.id.selected_letter);
                                //selectedLetterView.setY(selectedLetterView.getY() + 100);
                                selectedLetterView.setX(selectedLetterView.getX() + 10);
                                char selectedLetter = letters[selectedCol][selectedRow];
                                selectedLetters += selectedLetter;
                                selectedLetterView.setText(selectedLetters);
                                break;

                            case MotionEvent.ACTION_UP:
                                // remove selection
                                selectedRow = -1;
                                selectedCol = -1;
                                textView.setBackgroundColor(Color.TRANSPARENT);
                                break;
                        }
                        return true;
                    }
                });
            }
        }


        Button controlButton = findViewById(R.id.button1);
        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                // read words from file and add them to ArrayList
                ArrayList<String> wordsList = new ArrayList<>();
                try {
                    InputStream inputStream = getAssets().open("filtrelenmis_kelime_dosyası.txt");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        wordsList.add(line.trim().toLowerCase(Locale.getDefault()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // check if selected word is in the list
                String selectedWord = selectedLetters.toLowerCase(Locale.getDefault());
                if (wordsList.contains(selectedWord)) {
                    Toast.makeText(getApplicationContext(), selectedWord + " listede bulundu!", Toast.LENGTH_SHORT).show();



                    // Kelimenin harflerinin puanlarını toplama işlemi
                    int totalPoints = 0;
                    for (int i = 0; i < selectedWord.length(); i++) {
                        char c = selectedWord.charAt(i);
                        if (points.containsKey(c)) {
                            totalPoints += points.get(c);
                        }
                    }
                    sonucalani.setText("Toplam Puan: " + totalPoints);
                    selectedLetters = "";
                    TextView selectedLetterView = findViewById(R.id.selected_letter);
                    selectedLetterView.setText(selectedLetters);

                    // remove selected letters from matrix
                    for (int i = 0; i < selectedWord.length(); i++) {
                        boolean foundLetter = false;
                        for (int row = 0; row < ROW_COUNT; row++) {
                            for (int col = 0; col < COL_COUNT; col++) {
                                if (letters[col][row] == selectedWord.charAt(i)) {
                                    TextView textView = (TextView) ((TableRow) tableLayout.getChildAt(ROW_COUNT - row - 1)).getChildAt(col + 1);
                                    textView.setText("");
                                   letters[col][row] = 0;
                                    foundLetter = true;
                                    break;
                                }
                            }
                            if (foundLetter) {
                                break;
                            }
                        }
                    }
                    }

                else {
                    Toast.makeText(getApplicationContext(), selectedWord + " listede bulunamadı!", Toast.LENGTH_SHORT).show();
                }



            }
        });
        Button clearButton = findViewById(R.id.button2);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedLetters = "";
                TextView selectedLetterView = findViewById(R.id.selected_letter);
                selectedLetterView.setText(selectedLetters);
            }
        });

    }


    private void stopFalling(Runnable runnable) {
        isFalling = false;
        handler.removeCallbacks(runnable);
    }


    private void redrawTable() {
        TableLayout tableLayout = findViewById(R.id.table_layout);
        tableLayout.removeAllViews(); // clear the existing table
        drawTable(tableLayout); // draw the updated table
    }

    // add Turkish characters to the characters string
    String characters = "abcçdefgğhıijklmnoöprsştuüvyz";


    // generateInitialLetters method
    private void generateInitialLetters() {
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COL_COUNT; col++) {
                // generate a random index for the characters string
                int index = random.nextInt(characters.length());
                // select the character at the generated index and store it in the letters array
                letters[col][row] = characters.charAt(index);
            }
        }
    }

    private boolean fallLetters() {
        boolean isStopped = false;
        for (int col = 0; col < COL_COUNT; col++) {
            for (int row = ROW_COUNT - 1; row > 0; row--) {
                letters[col][row] = letters[col][row - 1];
            }
            letters[col][0] = (char) (random.nextInt(26) + 'a');
            if (letters[col][1] != ' ') {
                isStopped = true;
            }
        }
        return isStopped;
    }
    private void startFalling() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500); // wait for 500ms before falling
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (fallLetters()) {
                        break;
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            redrawTable();
                        }
                    });
                }
                Runnable runnable = new Runnable() {

                    @Override
                    public void run() {

                    }
                };stopFalling(runnable);
            }
        });
        thread.start();
    }


    private void drawTable(TableLayout tableLayout) {
        // add row numbers from 10 to 1
        for (int row = ROW_COUNT - 1; row >= 0; row--) {
            TableRow tableRow = new TableRow(this);
            // add row number
            TextView rowNumber = new TextView(this);
            rowNumber.setText(String.valueOf(row + 1));
            rowNumber.setTextSize(20);
            rowNumber.setPadding(20, 20, 20, 20);
            tableRow.addView(rowNumber);
            for (int col = 0; col < COL_COUNT; col++) {
                TextView textView = new TextView(this);
                if (row > 2) { // if row is greater than 2, leave the letter blank
                    textView.setText("");
                } else {
                    textView.setText(String.valueOf(letters[col][row]));
                }
                textView.setTextSize(20);
                textView.setPadding(20, 20, 20, 20);
                tableRow.addView(textView);
            }
            tableLayout.addView(tableRow);
        }

        // add column numbers starting from 9th row
        TableRow columnRow = new TableRow(this);
        columnRow.addView(new TextView(this)); // empty cell
        for (int col = 1; col <= COL_COUNT; col++) {
            TextView textView = new TextView(this);
            textView.setText(String.valueOf(col + 1));
            textView.setTextSize(20);
            textView.setPadding(20, 20, 20, 20);
            columnRow.addView(textView);
        }
        tableLayout.addView(columnRow);

        // add empty row
        tableLayout.addView(new TableRow(this));
    }

    public void play(View view) {

    }
}