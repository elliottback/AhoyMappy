package elliott.back;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class SimpleDateFormatConcurrencyTest {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public static Date startDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1984, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
        return calendar.getTime();
    }

    public static Date endDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1984, Calendar.DECEMBER, 31, 23, 59, 59);
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
        return calendar.getTime();
    }

    public static Date getRandomDate() {
        // Get the time in milliseconds of the start and end dates
        long startMillis = startDate().getTime();
        long endMillis = endDate().getTime();

        // Generate a random time in milliseconds within the range
        long randomMillis = ThreadLocalRandom.current().nextLong(startMillis, endMillis);

        // Create a new Date object with the random time
        return new Date(randomMillis);
    }

    public static List<Date> parseDates(String[] dateStrings, int threadCount) throws InterruptedException, ExecutionException {
        // Define a thread pool
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<Date>> futureList = new ArrayList<>();

        // Submit parsing tasks to the thread pool
        for (String dateString : dateStrings) {
            Callable<Date> task = () -> format.parse(dateString);
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

    public static double checkDates(List<Date> dates, Date start, Date end) {
        long countWithinBounds = dates.stream()
                .filter(date -> !date.before(start) && !date.after(end))
                .count();

        return (double) countWithinBounds / dates.size();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        for (int threads = 1; threads < 2; threads++) {
            int count = 1000000; // a million random dates
            String[] dates = new String[count];
            for (int i = 0; i < count; i++)
                dates[i] = format.format(getRandomDate());

            List<Date> parsed = parseDates(dates, threads);
            double percentage = checkDates(parsed, startDate(), endDate());
            System.out.println(String.format("%d threads - %.2f%% inside bounds", threads, percentage * 100.0));
        }
    }
}
