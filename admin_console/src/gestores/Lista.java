package gestores;

import models.eleicoes.*;
import models.eleicoes.Eleicao;
import models.pessoas.*;
import models.pessoas.Pessoa;

import java.rmi.RemoteException;
import java.util.function.BooleanSupplier;

import static adminconsole.AdminConsole.*;

public class Lista {
    public static void menu() {
        gerir("MENU LISTAS\n" +
                        "O que pretende fazer?\n" +
                        "1 - Adicionar\n" +
                        "2 - Editar\n" +
                        "3 - Remover\n" +
                        "4 - Listar\n" +
                        "5 - Adicionar Pessoa\n" +
                        "6 - Listar Pessoas\n" +
                        "7 - Remover Pessoa\n" +
                        "8 - Voltar\n",
                "Por favor insira um número correspondente a uma das opcções disponíveis.\n",
                new int[]{1, 2, 3, 4, 5, 6, 7, 8},
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
                                System.out.print(rmi.query("Listas", "*", ""));
                                return true;
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                return false;
                            }
                        },
                        () -> {
                            try {
                                addPessoas();
                                return true;
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                return false;
                            }
                        },
                        () -> {
                            try {
                                listPessoas();
                                return true;
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                return false;
                            }
                        },
                        () -> {
                            try {
                                removePessoas();
                                return true;
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                });
    }

    public static void insert() throws RemoteException {
        if ((eleicao = (models.eleicoes.Eleicao) escolheID("Eleicaos", "a eleicao a qual pretende adicionar uma lista")) == null)
            return;

        sc.nextLine();

        lista = new models.Lista();
        lista.setEleicao_id(eleicao.getId());

        getProperty("Insira o Nome: ",
                "Por favor insira um nome só com letras!\n",
                () -> !lista.setNome(sc.nextLine()));

        if (eleicao.getTipo().equals("conselho geral")) {
            getProperty("Escolha o tipo de lista a inserir:\n" +
                            "1 - Lista Alunos\n" +
                            "2 - Lista Docentes\n" +
                            "3 - Lista Funcionários\n",
                    "Por favor insira um número correspondente a um dos tipos disponíveis.\n",
                    () -> !contains(new int[]{1, 2, 3}, r1 = sc.nextInt()));

            if (r1 == 1)
                lista.setTipo("alunos");
            else if (r1 == 2)
                lista.setTipo("docentes");
            else
                lista.setTipo("funcionarios");
        }
        else
            lista.setTipo("alunos");

        rmi.insert(lista);
    }

    public static void update() throws RemoteException {
        if ((lista = (models.Lista) escolheID("Listas", "a lista a editar")) == null)
            return;
        sc.nextLine();

        getProperty("Escolha a propriedade a editar:\n" +
                        "Nome\n",
                "Por favor insira um número correspondente a uma das propriedades disponíveis.\n",
                () -> !contains(new String[]{"nome"}, (r2 = sc.nextLine())));


        switch (r2.toLowerCase()) {
            case "nome":
                getProperty("Por favor insira um nome só com letras!\n",
                        () -> !lista.update("nome", editProperty("Nome", lista.getNome())));
                break;
        }

        rmi.update(lista);
    }

    public static void delete() throws RemoteException {
        if ((lista = (models.Lista) escolheID("Listas", "a lista a remover")) == null)
            return;

        if (rmi.query("Lista_Pessoas", "(ID)", "WHERE lista_id = "  + lista.getId()).equals("not empty")) {
            System.out.print("A lista pretendida contêm referências a várias pessoas, pretende eliminá-la na mesma?");
            if (sc.nextLine().toLowerCase().equals("sim")) {
                rmi.delete("Lista_Pessoas", "lista_id = " + lista.getId());
                rmi.delete(lista);
            }
        }
        else rmi.delete(lista);
    }


    public static void addPessoas() throws RemoteException {
        if ((lista = (models.Lista) escolheID("Listas", "a lista à qual pretende adicionar uma pessoa")) == null)
            return;

        if ((pessoa = (models.pessoas.Pessoa) escolheID("Pessoas", "a pessoa a adicionar")) == null)
            return;

        rmi.connect(lista, eleicao);
    }

    public static void listPessoas() throws RemoteException {
        if ((lista = (models.Lista) escolheID("Listas", "a lista sobre a qual quer ver as pessoas")) == null)
            return;

        printConnections("Lista", "Pessoa", lista.getId());
    }

    public static void removePessoas() throws RemoteException {
        if ((lista = (models.Lista) escolheID("Listas", "a lista da qual pretende remover uma pessoa")) == null)
            return;

        if ((pessoa = (Pessoa) escolheID("Pessoas", "a pessoa a remover")) == null)
            return;
        rmi.disconnect(lista,pessoa);
    }
}
