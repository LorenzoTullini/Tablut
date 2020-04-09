package client;

public class TimeManager {
    private Boolean isEnd;

    public TimeManager(){
        isEnd = false;
    }

    //Return true if timer is over
    public Boolean isEnd() {
        synchronized(this.isEnd){
            return isEnd;
        }
    }

    public void setEnd(Boolean end) {
        synchronized(this.isEnd){
            isEnd = end;
        }
    }
}
