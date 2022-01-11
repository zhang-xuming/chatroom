package hk.edu.cuhk.ie.iems5722.a2_1155162628;

public class Msg{
    private String message;
    private String time;
    private String name;

    public Msg(String message,String name,String time){
        this.message = message;
        this.name = name;
        this.time = time;
    }

    public String getMessage(){
        return message;
    }

    public String getName(){
        return name;
    }

    public String getTime(){
        return time;
    }
}