package client;

public class TimerThread extends Thread{
    private final TimeManager timeManager;
    private final int deadline;

    public TimerThread(TimeManager timeManager, int deadline) { //Deadline in millisecondsz
        this.timeManager = timeManager;
        this.deadline = deadline;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(deadline);
            timeManager.setEnd(true);
            System.out.println("[TimerThread] tempo scaduto !!");
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }
}
