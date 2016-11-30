import groovy.transform.CompileStatic;
import java.util.concurrent.ThreadLocalRandom;

interface Grades {
    public static final double LOWEST = 0.0d;
    public static final double HIGHEST = 100.0d;
    
    public static final Map<IntRange,String> ALL =
        [ (97..100): 'A+',
          (93..96): 'A',
          (90..92): 'A-',
          (87..89): 'B+',
          (83..86): 'B',
          (80..82): 'B-',
          (77..79): 'C+',
          (73..76): 'C',
          (70..72): 'C-',
          (67..69): 'D+',
          (63..66): 'D',
          (60..62): 'D-',
          (57..59): 'B+',
          (53..56): 'F',
          (0..52): 'F-' ];
    
    String grade(double points);
}

@CompileStatic
abstract class BaseGrades implements Grades {

    public static boolean validPoints(double points) {
        return (points >= LOWEST && points <= HIGHEST);
    }

    public static void assertValid(double points) {
        if(!validPoints(points)) {
            throw new IllegalArgumentException("points needs to be between ${LOWEST} and ${HIGHEST}, you passed ${points}")
        }
    }

    public static int actualPoints(double points) {
        assertValid(points);
        return (int) Math.floor(points);
    }

    public RunnableState runner() {
        Grades THIS = this;
        return new RunnableState() {
            String _state;
            @Override public boolean getDoesIteration() { return true; }
            public Object getState() { _state; }
            public void run() {
                def random = ThreadLocalRandom.current();
                def iter = random.doubles(ops as long, Grades.LOWEST, Grades.HIGHEST).iterator();
                
                while(iter.hasNext()) {
                    double val = iter.nextDouble();
                    _state = THIS.grade(val);
                }
            }
        }
    }
}

@CompileStatic
class SlowGrades extends BaseGrades {

    String grade(final double points) {
        Integer actual = actualPoints(points);
        return ALL.find { entry -> entry.key.contains(actual) }.value;
    }
}

@CompileStatic
class FastGrades extends BaseGrades {

    final List<String> theGrades;
    
    public FastGrades() {
        int low = BaseGrades.actualPoints(Grades.LOWEST);
        int high = BaseGrades.actualPoints(Grades.HIGHEST);
        List<String> strs = new ArrayList<>(high - low);
        (low..high).each { int num -> strs.add(ALL.find { entry -> entry.key.contains(num) }.value); };
        this.theGrades = strs.asImmutable() as List<String>;
    }

    String grade(final double points) {
        return theGrades.get(actualPoints(points));
    }
}
