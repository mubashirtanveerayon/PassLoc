package commons.utils.monitor;

import java.util.ArrayList;
import java.util.Arrays;

public class PasswordMonitor extends Thread{

    private final char[] password;
    private int time;
    private final int timeout;
    private boolean requirePassword;
    private final ArrayList<PasswordMonitorListener>listeners;

    public static PasswordMonitor getInstance() {
        return instance;
    }

    public static void setInstance(PasswordMonitor instance) {
        PasswordMonitor.instance = instance;
    }

    private static PasswordMonitor instance;

    public PasswordMonitor(char[] password, int timeoutInSeconds) {
        this.password = password;
        this.timeout = timeoutInSeconds;
        listeners = new ArrayList<>();
        requirePassword = true;
    }
    public void run(){
        requirePassword = false;
        time =0;
        notifyListenerPasswordProvided();
        while(time++<timeout && !requirePassword){
            try{
                Thread.sleep(1000);

            }catch(Exception e){
                e.printStackTrace();
            }
        }
        Arrays.fill(password,(char)0);

        requirePassword = true;
        notifyListenersRequirePassword();
    }

    public void addListener(PasswordMonitorListener listener){
        if(!requirePassword)return;
        listeners.add(listener);
    }
    public void removeListener(PasswordMonitorListener listener){
        if(!requirePassword)return;
        listeners.remove(listener);
    }

    public void notifyListenersRequirePassword(){
        for(PasswordMonitorListener listener : listeners){
            listener.requirePassword();
        }
    }

    public void notifyListenerPasswordProvided(){
        for(PasswordMonitorListener listener : listeners){
            listener.passwordProvided();
        }
    }

    public void resetPassword(){
        requirePassword = true;
    }

    public char []getPassword(){
        return password;
    }

    public void resetTimer(){

        time =0;
    }

}
