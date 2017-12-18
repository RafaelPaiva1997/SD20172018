package rmi;

import models.eleicoes.Eleicao;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class DataChecker extends Thread {

    private RMI rmi;

    public DataChecker(RMI rmi) {
        this.rmi = rmi;
    }

    @Override
    public void run() {
        Timer t = new java.util.Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    Eleicao[] eleicaos = rmi.getEleicoes("");
                    Date agora = new Date();
                    for (Eleicao e : eleicaos) {
                        e.setFinished(!(e.getData_inicio().before(agora) && e.getData_fim().after(agora)));
                        rmi.update(e);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 30000);
    }
}
