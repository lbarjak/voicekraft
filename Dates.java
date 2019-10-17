package residual;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Dates {
    
    public String now() {
        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmm");
        String now = formatter.format(today);
        return now;
    }
    public void diff(Date start, Date stop) {
        long diffSec = (stop.getTime() - start.getTime()) / 1000;
        System.out.println("--------------------");
        System.out.println(diffSec / 60 + " perc " + diffSec % 60 + " másodperc");
    }
}
