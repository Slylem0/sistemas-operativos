public interface TrafficListener {
    void onDroneEnqueued(int id, Direccion dir, int northQueue, int southQueue);
    void onDroneCrossing(int id, Direccion dir);
    void onDroneExited(int id, Direccion dir);
    void onDirectionChanged(Direccion newDir);
}