package rmi;

import com.sun.media.sound.InvalidFormatException;
import database.DatabaseHandler;
import exceptions.EmptyQueryException;
import exceptions.PasswordException;
import exceptions.UsernameException;
import models.MesadeVoto;
import models.Model;
import models.Voto;
import models.eleicoes.ConselhoGeral;
import models.eleicoes.Eleicao;
import models.eleicoes.NucleoEstudantes;
import models.Lista;
import models.organizacoes.Departamento;
import models.organizacoes.Faculdade;
import models.pessoas.Aluno;
import models.pessoas.Docente;
import models.pessoas.Funcionario;
import models.pessoas.Pessoa;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class RMI extends UnicastRemoteObject implements RMIInterface {

    private final DatabaseHandler databaseHandler;
    private ArrayList<String> out;

    public RMI(DatabaseHandler databaseHandler) throws RemoteException {
        super();
        this.databaseHandler = databaseHandler;
    }

    @Override
    public boolean insert(Model model) throws RemoteException {
        return databaseHandler.execute(model.sqlInsert());
    }

    @Override
    public boolean update(Model model) throws RemoteException {
        return databaseHandler.execute(model.sqlUpdate());
    }

    @Override
    public boolean delete(Model model) throws RemoteException {
        return databaseHandler.execute(model.sqlDelete());
    }

    @Override
    public boolean delete(String table, String query) throws RemoteException {
        return databaseHandler.execute("DELETE FROM " + table + " WHERE " + query);
    }

    @Override
    public boolean connect(Model model1, Model model2) throws RemoteException {
        return databaseHandler.execute(model1.connect(model2));
    }

    @Override
    public boolean disconnect(Model model1, Model model2) throws RemoteException {
        return databaseHandler.execute(model1.disconnect(model2));
    }

    @Override
    public boolean reset() throws RemoteException {
        return databaseHandler.reset();
    }

    @Override
    public Model get(String table, String query) throws RemoteException {
        try {
            ResultSet resultSet = databaseHandler.executeQuery("SELECT * FROM " + table + " WHERE " + query);

            if (resultSet.next()) {
                switch (table.toLowerCase()) {
                    case "faculdades":
                        return new Faculdade(resultSet);
                    case "departamentos":
                        return new Departamento(resultSet);
                    case "pessoas":
                        return new Pessoa(resultSet);
                    case "alunos":
                        return new Aluno(resultSet);
                    case "docentes":
                        return new Docente(resultSet);
                    case "funcionarios":
                        return new Funcionario(resultSet);
                    case "listas":
                        return new Lista(resultSet);
                    case "mesa_votos":
                        return new MesadeVoto(resultSet);
                    case "votos":
                        return new Voto(resultSet);
                    case "eleicaos":
                        if (resultSet.getString("tipo").equals("conselho geral"))
                            return new ConselhoGeral(resultSet);
                        else
                            return new NucleoEstudantes(resultSet);
                    default:
                        return null;
                }
            } else return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ArrayList<Model> getMany(String table, String query) throws RemoteException, EmptyQueryException, InvalidFormatException {
        try {
            ResultSet resultSet = databaseHandler.executeQuery("SELECT * FROM " + table + query);

            ArrayList<Model> models = new ArrayList<>();

            if (!resultSet.next())
                throw new EmptyQueryException();

            do {
                switch (table.toLowerCase()) {
                    case "faculdades":
                        models.add(new Faculdade(resultSet));
                        break;
                    case "departamentos":
                        models.add(new Departamento(resultSet));
                        break;
                    case "pessoas":
                        models.add(new Pessoa(resultSet));
                        break;
                    case "alunos":
                        models.add(new Aluno(resultSet));
                        break;
                    case "docentes":
                        models.add(new Docente(resultSet));
                        break;
                    case "funcionarios":
                        models.add(new Funcionario(resultSet));
                        break;
                    case "listas":
                        models.add(new Lista(resultSet));
                        break;
                    case "mesa_votos":
                        models.add(new MesadeVoto(resultSet));
                        break;
                    case "votos":
                        models.add(new Voto(resultSet));
                        break;
                    case "eleicaos":
                        if (resultSet.getString("tipo").equals("conselho geral"))
                            models.add(new ConselhoGeral(resultSet));
                        else
                            models.add(new NucleoEstudantes(resultSet));
                        break;
                    default:
                        throw new InvalidFormatException();
                }
            } while (resultSet.next());

            return models;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("SQL Exception");
        }
    }

    @Override
    public String query(String table, String query, String query2) throws RemoteException {
        StringBuilder s = new StringBuilder();
        try {
            ResultSet resultSet = databaseHandler.executeQuery("SELECT " + query + " FROM " + table + " " + query2);

            if (query.equals("(ID)")) {
                if (!resultSet.next())
                    return "empty";
                else
                    return "not empty";
            }

            switch (table.toLowerCase()) {
                case "faculdades":
                    Faculdade faculdade;
                    s.append("Faculdades: \n");
                    while (resultSet.next()) {
                        faculdade = new Faculdade(resultSet);
                        s.append(faculdade.toString());
                    }
                    break;

                case "departamentos":
                    Departamento departamento;
                    s.append("Departamentos: \n");
                    while (resultSet.next()) {
                        departamento = new Departamento(resultSet);
                        s.append(departamento.toString());
                    }
                    break;

                case "lista_pessoas":
                case "pessoas":
                    Pessoa pessoa;
                    s.append("Pessoas: \n");
                    while (resultSet.next()) {
                        pessoa = new Pessoa(resultSet);
                        s.append(pessoa.toString());
                    }
                    break;

                case "listas":
                    Lista lista;
                    s.append("Listas: \n");
                    while (resultSet.next()) {
                        lista = new Lista(resultSet);
                        s.append(lista.toString());
                    }
                    break;

                case "mesa_votos":
                    MesadeVoto mesadeVoto;
                    s.append("Mesas de Voto: \n");
                    while (resultSet.next()) {
                        mesadeVoto = new MesadeVoto(resultSet);
                        s.append(mesadeVoto.toString());
                    }
                    break;

                case "mesa_voto_eleicaos":
                case "eleicaos":
                    Eleicao eleicao;
                    s.append("Eleições: \n");
                    while (resultSet.next()) {
                        if (resultSet.getString("tipo").equals("conselho geral"))
                            eleicao = new ConselhoGeral(resultSet);
                        else
                            eleicao = new NucleoEstudantes(resultSet);
                        s.append(eleicao.toString());
                    }
                    break;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return s.append("\n").toString();
    }

    @Override
    public Pessoa login(String username, String password) throws RemoteException, UsernameException, PasswordException {
        try {
            ResultSet resultSet = databaseHandler.executeQuery("SELECT * FROM Pessoas WHERE username='" + username + "'");

            if (!resultSet.next())
                throw new UsernameException();

            Pessoa pessoa = new Pessoa(resultSet);

            if (!pessoa.getPassword().equals(password))
                throw new PasswordException();

            return pessoa;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("SQL Exception");
        }
    }

    @Override
    public int queryInt(String table, String query, String query2) throws RemoteException {
        try {
            ResultSet resultSet = databaseHandler.executeQuery("SELECT " + query + " FROM " + table + " " + query2);

            int contador = 0;
            while (resultSet.next())
                contador++;
            return contador;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public String[] votar(Eleicao eleicao, Pessoa pessoa) throws RemoteException {
        try {
            Lista lista;
            StringBuilder s = new StringBuilder();
            ArrayList<String> out = new ArrayList<>();
            ResultSet resultSet = databaseHandler.executeQuery("SELECT * FROM Listas WHERE eleicao_id = " + eleicao.getId() + " && tipo = '" + pessoa.getTipo() + "s'");

            if (!resultSet.next())
                return new String[0];

            s.append("Escolha a lista em que pretende votar:\n\n");
            do {
                lista = new Lista(resultSet);
                s.append(lista.toString());
                out.add(String.valueOf(lista.getId()));
            } while (resultSet.next());

            s.append("VOTO BRANCO\nVOTO NULO\n");

            out.add(s.toString());

            return out.toArray(new String[out.size()]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    @Override
    public String votar(String[] listas, String input) throws RemoteException {
        if (!input.toLowerCase().equals("voto branco") && !input.toLowerCase().equals("voto nulo") && !Arrays.toString(listas).toLowerCase().contains(input.toLowerCase()))
            return "fail";
        return input.toLowerCase();
    }

    @Override
    public boolean votar(String id, Eleicao eleicao, Pessoa pessoa, MesadeVoto mesadeVoto, Date date) throws RemoteException {
        Voto voto = new Voto();

        voto.setEleicao_id(eleicao.getId());
        voto.setPessoa_id(pessoa.getId());
        voto.setMesa_voto_id(mesadeVoto.getId());
        voto.setData(date);

        if (id.equals("voto branco"))
            voto.setTipo("branco");
        else if (id.equals("voto nulo"))
            voto.setTipo("nulo");
        else
            voto.setTipo("normal");

        boolean flag = insert(voto);

        if (voto.getTipo().equals("normal")) {

            if ((voto = (Voto) get("Votos", "eleicao_id = " + eleicao.getId() + " && pessoa_id = " + pessoa.getId())) == null)
                return false;

            Lista lista;

            if ((lista = (Lista) get("Listas", "ID = " + id)) == null)
                return false;

            return connect(lista, voto) && flag;
        }

        return flag;
    }

    @Override
    public Eleicao[] getEleicoes(String query) throws RemoteException {
        try {
            ArrayList<Eleicao> eleicaos = new ArrayList<>();
            ResultSet resultSet = databaseHandler.executeQuery("SELECT * FROM Eleicaos " + query);

            if (!resultSet.next())
                return new Eleicao[0];

            do {
                eleicaos.add(new Eleicao(resultSet));
            } while (resultSet.next());

            return eleicaos.toArray(new Eleicao[eleicaos.size()]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Eleicao[0];
        }
    }

    @Override
    public MesadeVoto[] getMesasVoto(String query) throws RemoteException {
        try {
            ArrayList<MesadeVoto> mesadeVotos = new ArrayList<>();
            ResultSet resultSet = databaseHandler.executeQuery("SELECT * FROM Mesa_Votos " + query);

            if (!resultSet.next())
                return new MesadeVoto[0];

            do {
                mesadeVotos.add(new MesadeVoto(resultSet));
            } while (resultSet.next());

            return mesadeVotos.toArray(new MesadeVoto[mesadeVotos.size()]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new MesadeVoto[0];
        }
    }

    @Override
    public Lista[] getListas(String query) throws RemoteException {
        try {
            ArrayList<Lista> listas = new ArrayList<>();
            ResultSet resultSet = databaseHandler.executeQuery("SELECT * FROM Listas " + query);

            if (!resultSet.next())
                return new Lista[0];

            do {
                listas.add(new Lista(resultSet));
            } while (resultSet.next());

            return listas.toArray(new Lista[listas.size()]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Lista[0];
        }
    }

    @Override
    public Voto[] getVotos(String query) throws RemoteException {
        try {
            ArrayList<Voto> votos = new ArrayList<>();
            ResultSet resultSet = databaseHandler.executeQuery("SELECT * FROM Votos " + query);

            if (!resultSet.next())
                return new Voto[0];

            do {
                votos.add(new Voto(resultSet));
            } while (resultSet.next());

            return votos.toArray(new Voto[votos.size()]);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Voto[0];
        }    }

    public boolean put(Registry registry) {
        boolean flag = true;
        try {
            registry.rebind("rmi-object", this);
        } catch (RemoteException e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }
}
