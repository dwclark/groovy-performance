public abstract class RunnableState implements Runnable {
    private Object state;
    public Object getState() { return state; }
    public void setState(Object state) { this.state = state; }

    private long ops;
    public long getOps() { return ops; }
    public void setOps(long ops) { this.ops = ops; }

    public boolean getDoesIteration() { return false; }
}
