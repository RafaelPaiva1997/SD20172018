package gestores;

import models.MesadeVoto;
import models.organizacoes.Faculdade;

import java.rmi.RemoteException;
import java.util.function.BooleanSupplier;

import static adminconsole.AdminConsole.*;

public class Departamento {

    public static void menu() {
        gerir("MENU DEPARTAMENTOS\n" +
                        "O que pretende fazer?\n" +
                        "1 - Adicionar\n" +
                        "2 - Editar\n" +
                        "3 - Remover\n" +
                        "4 - Listar\n" +
                        "5 - Voltar\n",
                "Por favor insira um número correspondente a uma das opcções disponíveis.\n",
                new int[]{1, 2, 3, 4, 5},
                new BooleanSupplier[]{
                        () -> {
                            try {
                                insert();
                                return true;
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                return false;
                            }
                        },
                        () -> {
                            try {
                                update();
                                return true;
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                return false;
                            }
                        },
                        () -> {
                            try {
                                delete();
                                return true;
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                return false;
                            }
                        },
                        () -> {
                            try {
                                System.out.print(rmi.query("Departamentos", "*", ""));
                                return true;
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                });
    }

    public static void insert() throws RemoteException {
        if ((faculdade = (models.organizacoes.Faculdade) escolheID("Faculdades", "a faculdade a qual pretende adicionar um departamento")) == null)
            return;

        sc.nextLine();

        departamento = new models.organizacoes.Departamento();
        departamento.setFaculdade_id(faculdade.getId());

        getProperty("Insira o Nome: ",
                "Por favor insira um nome só com letras!\n",
                () -> !departamento.setNome(sc.nextLine()));

        rmi.insert(departamento);
        rmi.insert(new MesadeVoto(rmi.get("Departamentos", "nome = '" + departamento.getNome() + "'").getId()));
    }

    public static void update() throws RemoteException {
        if ((departamento = (models.organizacoes.Departamento) escolheID("Departamentos", "o departamento a editar")) == null)
            return;

        sc.nextLine();

        getProperty("Escolha a propriedade a editar:\n" +
                        "Nome\n",
                "Por favor insira um número correspondente a uma das propriedades disponíveis.\n",
                () -> !contains(new String[]{"nome"}, (r2 = sc.nextLine())));


        switch (r2.toLowerCase()) {
            case "nome":
                getProperty("Por favor insira um nome só com letras!\n",
                        () -> !departamento.update("nome", editProperty("Nome", departamento.getNome())));
                break;
        }

        rmi.update(departamento);
    }

    public static void delete() throws RemoteException {
        if ((departamento = (models.organizacoes.Departamento) escolheID("Departamentos", "o departamento a remover")) == null)
            return;

        rmi.delete(departamento);
        rmi.delete("Mesas_Voto", "departamento_id = " + departamento.getId());
    }
}
