package Timer;

import javax.swing.*;

public class Timer {

    public static int test3;

    public static void main(String[] args){


            test3 = 0;
            Timer2 testtråd = new Timer2();
            testtråd.start();

            while(true) {
                test3 = 0;

                String test = JOptionPane.showInputDialog("Ange 1 eller 2");
                int test2 = Integer.parseInt(test);
                if (test2 == 1) {
                    if (test3 > 0) {
                        System.out.println("Returnerar fel svar för att tiden har gått ut");
                        testtråd.stopThread();
                        break;
                    } else {
                        System.out.println("Returnerar svar 1");
                        testtråd.stopThread();
                        break;
                    }

                } else if (test2 == 2) {
                    if (test3 > 0 ) {
                        System.out.println("Returnerar fel svar för att tiden har gått ut");
                    } else {
                        System.out.println("Returnerar svar 2");
                    }

                    break;
                }
            }
    }



}
