import it.unimi.dsi.fastutil.ints.IntArrayList;
import groovy.transform.CompileStatic;

class FastUtil extends RunnableState {
    @Override public boolean getDoesIteration() { return true; }
    
    final int width;
    
    public FastUtil(final int width) {
        this.width = width;
    }
    
    public void run() {
        IntArrayList ary = new IntArrayList(width);
        ary.size(width);
        
        int times = (int) getOps();
        int i = 0;
        while(i < times) {
            ++i;
            int index = (int) (i % width);
            //change getInt() to get() to see 3x performance degredation
            ary.set(index, ary.getInt(index) + 1);
        }
        
        setState(ary.get(width - 1))
    }

    public static RunnableState fastUtil(final int width) {
        return new FastUtil(width);
    }
}
