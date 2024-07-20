import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

public class CheckCals {

    private static boolean fourOfEach(int year)
    {
        Map<DayOfWeek, Integer> counts = new HashMap<>();

        LocalDate d = LocalDate.of(year, 02, 1);

        do {
            DayOfWeek day = d.getDayOfWeek();
            counts.putIfAbsent(day, 0);
            counts.put(day, counts.get(day)+1);
            d = d.plusDays(1);
        } while( d.getMonth() == Month.FEBRUARY);

        for( Integer count : counts.values())
        {
            if( count != 4 )
                return false;
        }

        return true;
    }

    public static void main(String [] args)
    {
        double ratio = 0;
        int count = 3000;

        for(int i = 0; i<count;i++)
            if(fourOfEach(i)) {
                System.out.println(i);
                ratio += 1;
            }
        System.out.println(ratio/((double) count)*100);
    }
}
