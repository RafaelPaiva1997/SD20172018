import database.DatabaseHandler;
import rmi.DataChecker;
import rmi.RMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {

    public static RMI rmi;

    public static void main(String[] args) {
        if (args.length == 3 || args.length == 4) {
            try {
                Registry registry = LocateRegistry.createRegistry(Integer.parseInt(args[0]));

                DatabaseHandler databaseHandler = new DatabaseHandler(
                        "jdbc:mysql://" + args[1] + ":" + args[2],
                        "BD",
                        "username",
                        "password");

                if (databaseHandler.register()) {
                    if (databaseHandler.connect()) {
                        if (args.length == 4 && args[3].equals("-r")) databaseHandler.reset();
                        rmi = new RMI(databaseHandler);
                        if (rmi.put(registry))
                            System.out.println("RMI está disponível!");
                        DataChecker dataChecker = new DataChecker(rmi);
                        dataChecker.start();
                    }
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
