package elliott.back;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class SimpleDateFormatConcurrencyTest {
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Asia/Tokyo");

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private static ThreadLocal<SimpleDateFormat> dateFormatThreadLocal =
            ThreadLocal.withInitial(() -> {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                format.setTimeZone(TIME_ZONE);
                return format;
            });

    static {
        format.setTimeZone(TIME_ZONE);
    }

    public static Date startDate() {
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.set(1984, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date endDate() {
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.set(1984, Calendar.DECEMBER, 31, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static Date getRandomDate() {
        // Get the time in milliseconds of the start and end dates
        long startMillis = startDate().getTime();
        long endMillis = endDate().getTime();

        // Generate a random time in milliseconds within the range
        long randomMillis = ThreadLocalRandom.current().nextLong(startMillis + 1000, endMillis - 1000);

        // Create a new Date object with the random time
        return new Date(randomMillis);
    }

    public static List<Date> parseDates(String[] dateStrings, int threadCount, boolean useThreadLocal) throws InterruptedException, ExecutionException {
        // Define a thread pool
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Date>> futureList = new ArrayList<>();

        // Submit parsing tasks to the thread pool
        for (String dateString : dateStrings) {
            Callable<Date> task = () -> {
                try {
                    if (useThreadLocal)
                        return dateFormatThreadLocal.get().parse(dateString);
                    else
                        return format.parse(dateString);
                } catch (Exception e) {
                    return new Date(0);
                }
            };
            Future<Date> future = executor.submit(task);
            futureList.add(future);
        }

        // Collect the parsed dates
        List<Date> parsedDates = new ArrayList<>();
        for (Future<Date> future : futureList) {
            parsedDates.add(future.get());
        }

        // Shut down the executor service
        executor.shutdown();
        return parsedDates;
    }

    public static double checkDates(List<Date> dates, List<Date> goodDates) {
        Date [] dateArr = new Date[dates.size()];
        dateArr = dates.toArray(dateArr);

        Date [] goodDatesArr = new Date[goodDates.size()];
        goodDatesArr = goodDates.toArray(goodDatesArr);

        long unequal = 0;
        for(int i = 0; i < dateArr.length; i++)
            if(!goodDatesArr[i].equals(dateArr[i]))
                unequal++;

        return (double) (dates.size() - unequal) / dates.size();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int count = 1000000; // a million random dates
        String[] dates = new String[count];
        for (int i = 0; i < count; i++)
            dates[i] = format.format(getRandomDate());

        List<Date> goodDates = parseDates(dates, 16, true);

        for (int threads = 1; threads <= 1024; threads *= 2) {
            List<Date> parsed = parseDates(dates, threads, false);
            double percentage = checkDates(parsed, goodDates);
            System.out.println(String.format("%d threads - %.2f%% inside bounds", threads, percentage * 100.0));
        }

        System.out.println("-------------------------------------------------------------");

        for (int threads = 1; threads <= 1024; threads *= 2) {
            List<Date> parsed = parseDates(dates, threads, true);
            double percentage = checkDates(parsed, goodDates);
            System.out.println(String.format("threadlocal: %d threads - %.2f%% inside bounds", threads, percentage * 100.0));
        }
    }
}
