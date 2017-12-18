package database;

import java.sql.*;

public class DatabaseHandler {

    private final String url;
    private final String db;
    private final String user;
    private final String password;
    private Connection connection;

    public DatabaseHandler(String url, String db, String user, String password) {
        this.url = url;
        this.db = db;
        this.user = user;
        this.password = password;
    }

    public boolean register() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found, check if the jar is reachable !");
            e.printStackTrace();
            return false;

        }
        System.out.println("JDBC Driver funciona .. tentar a ligação;");
        return true;
    }

    public boolean connect() {
        try {
            connection = DriverManager.getConnection(url.concat("/").concat(db), user, password);
        } catch (SQLException e) {
            System.out.println("Ligação falhou.. erro:");
            e.printStackTrace();
            return false;

        }

        if (connection != null) {
            System.out.println("Ligação feita com sucessso!");
            return true;
        } else {
            System.out.println("Não conseguimos estabelecer a ligação!");
            return false;
        }
    }

    public boolean createTables() {
        Statement statement;
        try {
            if ((statement = connection.createStatement()) == null)
                return false;

            statement.execute("CREATE TABLE Faculdades (" +
                    "ID int NOT NULL AUTO_INCREMENT," +
                    "nome varchar(255) NOT NULL," +
                    "PRIMARY KEY(ID)," +
                    "UNIQUE(nome)" +
                    ")");

            statement.execute("CREATE TABLE Departamentos (" +
                    "ID int NOT NULL AUTO_INCREMENT," +
                    "faculdade_id int NOT NULL," +
                    "nome varchar(255) NOT NULL," +
                    "PRIMARY KEY(ID)," +
                    "FOREIGN KEY(faculdade_id) REFERENCES Faculdades(ID)," +
                    "UNIQUE(nome)" +
                    ")");

            statement.execute("CREATE TABLE Pessoas (" +
                    "ID int NOT NULL AUTO_INCREMENT," +
                    "departamento_id int NOT NULL," +
                    "tipo varchar(11) NOT NULL," +
                    "nome varchar(255) NOT NULL," +
                    "username varchar(20) NOT NULL," +
                    "password varchar(20) NOT NULL," +
                    "telemovel bigint NOT NULL," +
                    "morada varchar(255) NOT NULL," +
                    "codigo_postal varchar(8) NOT NULL," +
                    "localidade varchar(31) NOT NULL," +
                    "numero_cc bigint NOT NULL," +
                    "validade_cc date NOT NULL," +
                    "genero varchar(9) NOT NULL," +
                    "data_nascimento date NOT NULL," +
                    "admin bit NOT NULL," +
                    "PRIMARY KEY(ID)," +
                    "FOREIGN KEY(departamento_id) REFERENCES Departamentos(ID)," +
                    "UNIQUE(username)," +
                    "UNIQUE(numero_cc)" +
                    ")");

            statement.execute("CREATE TABLE Alunos (" +
                    "ID int NOT NULL AUTO_INCREMENT," +
                    "pessoa_id int NOT NULL," +
                    "numero_aluno bigint NOT NULL," +
                    "curso varchar(255) NOT NULL," +
                    "PRIMARY KEY(ID)," +
                    "FOREIGN KEY(pessoa_id) REFERENCES Pessoas(ID)," +
                    "UNIQUE(numero_aluno)" +
                    ")");

            statement.execute("CREATE TABLE Docentes (" +
                    "ID int NOT NULL AUTO_INCREMENT," +
                    "pessoa_id int NOT NULL," +
                    "cargo varchar(255) NOT NULL," +
                    "PRIMARY KEY(ID)," +
                    "FOREIGN KEY(pessoa_id) REFERENCES Pessoas(ID)" +
                    ")");

            statement.execute("CREATE TABLE Funcionarios (" +
                    "ID int NOT NULL AUTO_INCREMENT," +
                    "pessoa_id int NOT NULL," +
                    "funcao varchar(255) NOT NULL," +
                    "PRIMARY KEY(ID)," +
                    "FOREIGN KEY(pessoa_id) REFERENCES Pessoas(ID)" +
                    ")");

            statement.execute("CREATE TABLE Eleicaos (" +
                    "ID int NOT NULL AUTO_INCREMENT," +
                    "tipo varchar(17) NOT NULL," +
                    "titulo varchar(255) NOT NULL," +
                    "descricao text NOT NULL," +
                    "data_inicio datetime NOT NULL," +
                    "data_fim datetime NOT NULL," +
                    "finished bit NOT NULL DEFAULT 0," +
                    "departamento_id int," +
                    "PRIMARY KEY(ID)," +
                    "FOREIGN KEY(departamento_id) REFERENCES Departamentos(ID)" +
                    ")");

            statement.execute("CREATE TABLE Listas (" +
                    "ID int NOT NULL AUTO_INCREMENT," +
                    "eleicao_id int NOT NULL," +
                    "tipo varchar(12) NOT NULL," +
                    "nome varchar(255) NOT NULL," +
                    "PRIMARY KEY(ID)," +
                    "FOREIGN KEY(eleicao_id) REFERENCES Eleicaos(ID)," +
                    "UNIQUE(nome)" +
                    ")");

            statement.execute("CREATE TABLE Mesa_Votos (" +
                    "ID int NOT NULL AUTO_INCREMENT," +
                    "departamento_id int NOT NULL," +
                    "working bit NOT NULL DEFAULT 0," +
                    "PRIMARY KEY(ID)," +
                    "FOREIGN KEY(departamento_id) REFERENCES Departamentos(ID)" +
                    ")");

            statement.execute("CREATE TABLE Votos (" +
                    "ID int NOT NULL AUTO_INCREMENT," +
                    "tipo varchar(6) NOT NULL," +
                    "pessoa_id int NOT NULL," +
                    "eleicao_id int NOT NULL," +
                    "mesa_voto_id int NOT NULL," +
                    "data datetime NOT NULL," +
                    "PRIMARY KEY(ID)," +
                    "FOREIGN KEY(pessoa_id) REFERENCES Pessoas(ID)," +
                    "FOREIGN KEY(eleicao_id) REFERENCES Eleicaos(ID)," +
                    "FOREIGN KEY(mesa_voto_id) REFERENCES Mesa_Votos(ID)," +
                    "UNIQUE(pessoa_id, eleicao_id)" +
                    ")");

            statement.execute("CREATE TABLE Mesa_Voto_Eleicaos (" +
                    "mesa_voto_id int NOT NULL," +
                    "eleicao_id int NOT NULL," +
                    "PRIMARY KEY(mesa_voto_id, eleicao_id)," +
                    "FOREIGN KEY(mesa_voto_id) REFERENCES Mesa_Votos(ID)," +
                    "FOREIGN KEY(eleicao_id) REFERENCES Eleicaos(ID)" +
                    ")");

            statement.execute("CREATE TABLE Lista_Votos (" +
                    "lista_id int NOT NULL," +
                    "votos_id int NOT NULL," +
                    "PRIMARY KEY(lista_id, votos_id)," +
                    "FOREIGN KEY(lista_id) REFERENCES Listas(ID)," +
                    "FOREIGN KEY(votos_id) REFERENCES Votos(ID)" +
                    ")");

            statement.execute("CREATE TABLE Lista_Pessoas (" +
                    "lista_id int NOT NULL," +
                    "pessoa_id int NOT NULL," +
                    "PRIMARY KEY(lista_id, pessoa_id)," +
                    "FOREIGN KEY(lista_id) REFERENCES Listas(ID)," +
                    "FOREIGN KEY(pessoa_id) REFERENCES Pessoas(ID)" +
                    ")");

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean reset() {
        Connection connection;
        Statement statement;
        try {
            connection = DriverManager.getConnection(url, user, password);

            if ((statement = connection.createStatement()) == null)
                return false;

            statement.execute("DROP DATABASE ".concat(db));
            statement.execute("CREATE DATABASE ".concat(db));

            return createTables();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean execute(String command) {
        Statement statement;
        try {
            if ((statement = connection.createStatement()) == null)
                return false;

            statement.execute(command);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ResultSet executeQuery(String query) {
        Statement statement;
        try {
            if ((statement = connection.createStatement()) == null)
                return null;

            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
