package perf;

import java.util.concurrent.ConcurrentHashMap;
import static java.util.concurrent.ThreadLocalRandom.current as tlr;

class PerformanceIssues {

    static final int MAX_KEYS = 50_000;
    static final ConcurrentHashMap<Integer,String> map = new ConcurrentHashMap<>();

    static String randomText() {
        int length = tlr().nextInt(1, 1000);
        StringBuilder sb = new StringBuilder(length);
        for(int i = 0; i < length; ++i) {
            sb.append(tlr().nextInt(0, 10) as String)
        }
        
        return sb.toString();
    }

    static void putRandom() {
        Integer key = tlr().nextInt(MAX_KEYS);
        String s = '';
        while(s.length() < 750) {
            s = randomText();
        }
        
        map[key] = s;
    }

    static String findRandom() {
        Integer key = tlr().nextInt(MAX_KEYS);
        return map[key];
    }

    static void loopPut() {
        while(true) {
            putRandom();
        }
    }

    static void loopFind() {
        int counter = 0;
        while(true) {
            String s = findRandom();
            ++counter;
            if(s && counter % 1_000_000 == 0) {
                println(s);
            }
        }
    }

    static void main(String[] args) {
        def threads = [];
        Thread.start(PerformanceIssues.&loopPut);
        Thread.start(PerformanceIssues.&loopFind);
    }
}
