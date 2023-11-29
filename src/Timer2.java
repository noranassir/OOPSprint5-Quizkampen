package Timer;

public class Timer2 extends Thread {

    private boolean exit;

    public void run() {
        int i = 10;
        while (!exit) {
            System.out.println(i);
            i--;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(i == 0){
                System.out.println(i + " Tiden är ute, inget svar registrerat!");
                Timer.test3 = 1;
                stopThread();
            }
        }

    }
    public void stopThread(){
        this.exit = true;
    }

}
