
public class Drone extends Thread {

    private final int id;
    private final Direccion direccion;
    private final ControladorTrafico controlador;

    public Drone(int id, Direccion direccion, ControladorTrafico controlador) {
        this.id = id;
        this.direccion = direccion;
        this.controlador = controlador;
    }

    @Override
    public void run() {
        try {
            controlador.solicitarPaso(id, direccion);
            cruzar();
            controlador.salir(id,direccion);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void cruzar() throws InterruptedException {
        Thread.sleep(1000);
    }
}
