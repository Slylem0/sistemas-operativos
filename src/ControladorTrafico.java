import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;
import java.util.List;

public class ControladorTrafico {

    private final Queue<Integer> colaNorte = new LinkedList<>();
    private final Queue<Integer> colaSur = new LinkedList<>();

    private Direccion direccionActual = Direccion.NORTE;
    private int dronesCruzando = 0;

    private final List<TrafficListener> listeners = new ArrayList<>();

    public synchronized void solicitarPaso(int id, Direccion dir)
            throws InterruptedException {

        Queue<Integer> cola = (dir == Direccion.NORTE) ? colaNorte : colaSur;
        cola.add(id);

        notifyDroneEnqueued(id, dir);

        while (dir != direccionActual || cola.peek() != id) {
            wait();
        }

        cola.poll();
        dronesCruzando++;

        notifyDroneCrossing(id, dir);
    }

    public synchronized void salir(int id, Direccion dir) {

        dronesCruzando--;

        notifyDroneExited(id, dir);

        if (dronesCruzando == 0) {
            Direccion anterior = direccionActual;
            decidirSiguienteDireccion();

            if (anterior != direccionActual) {
                notifyDirectionChanged(direccionActual);
            }

            notifyAll();
        }
    }

    private void decidirSiguienteDireccion() {

        int norte = colaNorte.size();
        int sur   = colaSur.size();

        if (norte > 0 && sur == 0) {
            direccionActual = Direccion.NORTE;
        }
        else if (sur > 0 && norte == 0) {
            direccionActual = Direccion.SUR;
        }
        else if (norte > sur) {
            direccionActual = Direccion.NORTE;
        }
        else if (sur > norte) {
            direccionActual = Direccion.SUR;
        }
        else {
            direccionActual = (direccionActual == Direccion.NORTE)
                    ? Direccion.SUR
                    : Direccion.NORTE;
        }
    }

    public void addListener(TrafficListener listener) {
        listeners.add(listener);
    }

    private void notifyDroneEnqueued(int id, Direccion dir) {
        for (TrafficListener listener : listeners) {
            listener.onDroneEnqueued(id, dir, colaNorte.size(), colaSur.size());
        }
    }

    private void notifyDroneCrossing(int id, Direccion dir) {
        for (TrafficListener listener : listeners) {
            listener.onDroneCrossing(id, dir);
        }
    }

    private void notifyDroneExited(int id, Direccion dir) {
        for (TrafficListener listener : listeners) {
            listener.onDroneExited(id, dir);
        }
    }

    private void notifyDirectionChanged(Direccion newDir) {
        for (TrafficListener listener : listeners) {
            listener.onDirectionChanged(newDir);
        }
    }
}