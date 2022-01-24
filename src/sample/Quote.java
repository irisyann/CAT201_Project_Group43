package sample;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Quote {

    ArrayList<String> quotesList = new ArrayList<String>();

    public Quote() {
        Scanner sc = new Scanner(getClass().getResourceAsStream("quotesList.txt" ));

        while (sc.hasNextLine()) {
            quotesList.add(sc.nextLine());
        }
        sc.close();
    }

    public String generateQuote() {
        Random random = new Random();
        int randomNum = random.nextInt(10);
        return quotesList.get(randomNum);
    }
}
