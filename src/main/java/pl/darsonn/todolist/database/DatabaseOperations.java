package pl.darsonn.todolist.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseOperations {
    private Connection connection;
    private Statement statement;

    public DatabaseOperations() {
        String request = "jdbc:mysql://localhost:3306/todo?useUnicode=true&characterEncoding=utf8";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(request, "root", "");
        } catch (ClassNotFoundException | SQLException exception) {
            Logger.getLogger(DatabaseOperations.class.getName()).log(Level.SEVERE, null, exception);
            System.err.println("Błąd połączenia z bazą danych.");
            System.exit(101);
        }
    }

    public int getNumberOfLists() {
        String request = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'todo';";
        int numberOfLists = 0;

        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(request);

            while (resultSet.next()) {
                numberOfLists = resultSet.findColumn("COUNT(*)");
            }
        } catch (SQLException exception) {
            System.err.println("Błąd odczytu z bazy danych.");
        }

        return numberOfLists;
    }

    public ArrayList<String> getLists() {
        String request = "SHOW TABLES;";
        ArrayList<String> lists = new ArrayList<>();

        try {
            statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(request);

            while (resultSet.next()) {
                lists.add(resultSet.getString(1));
            }

            return lists;
        } catch (SQLException exception) {
            System.err.println("Błąd odczytu z bazy danych.");
            return null;
        }
    }

    public boolean checkIfTableExists(String name) {
        String request = "SELECT COUNT(*) " +
                "FROM information_schema.tables " +
                "WHERE table_schema = 'todo' " +
                "AND table_name = '" + name + "';";
        int numberOfLists = 0;

        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(request);

            while (resultSet.next()) {
                numberOfLists = resultSet.getInt("COUNT(*)");
            }
        } catch (SQLException exception) {
            System.err.println("Błąd odczytu z bazy danych.");
        }

        return (numberOfLists > 0) ? true : false;
    }

    public boolean createNewList(String name) {
        String request = "CREATE TABLE `" + name + "`(`ID` INT NOT NULL AUTO_INCREMENT , `Author` " +
                "VARCHAR(80) NOT NULL , `CreationDate` DATE NOT NULL DEFAULT CURRENT_TIMESTAMP , `Title` " +
                "VARCHAR(255) NOT NULL , `Comment` VARCHAR(500) NOT NULL , PRIMARY KEY (`ID`)) ENGINE = InnoDB;";

        if(checkIfTableExists(name)) return false;

        try (final var statement = connection.prepareStatement(request)) {
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Błąd podczas tworzenia nowej listy w bazie danych.");
            return false;
        }
    }

    public boolean dropList(String name) {
        String request = "DROP TABLE `todo`.`" + name + "`;";

        if(!checkIfTableExists(name)) return false;

        try (final var statement = connection.prepareStatement(request)) {
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Błąd podczas tworzenia nowej listy w bazie danych.");
            return false;
        }
    }

    public boolean addElementToList(String author, String title, String comment, String list) {
        String request = "INSERT INTO `" + list + "`(`ID`, `Author`, `CreationDate`, `Title`, `Comment`) VALUES " +
                "(?,?,?,?,?);";

        if(!checkIfTableExists(list)) return false;

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try (final var statement = connection.prepareStatement(request)) {
            statement.setString(1, null);
            statement.setString(2, author);
            statement.setString(3, String.valueOf(timestamp));
            statement.setString(4, title);
            statement.setString(5, comment);
            statement.execute();

            return true;
        } catch (SQLException e) {
            System.err.println("Błąd podczas wprowadzania nowego elementu do bazy danych");
            return false;
        }
    }

    public boolean removeElementFromList(String id, String list) {
        String request = "DELETE FROM `" + list + "` WHERE `ID` = ?;";

        if(!checkIfTableExists(list)) return false;
        if(!checkIfElementExists(id, list)) return false;

        try (final var statement = connection.prepareStatement(request)) {
            statement.setString(1, id);
            statement.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Błąd podczas usuwania elementu z bazy danych");
            return false;
        }
    }

    public boolean checkIfElementExists(String id, String list) {
        String request = "SELECT `ID` FROM `" + list + "` WHERE `ID` = " + id + ";";

        if(!checkIfTableExists(list)) return false;

        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(request);
            if(resultSet.next()) {
                return Objects.equals(resultSet.getString("ID"), id);
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Błąd podczas odczytu elementów z bazy danych");
            return false;
        }
    }

    public ResultSet getElements(String list) {
        String request = "SELECT * FROM `" + list + "`;";

        try {
            if(!checkIfTableExists(list)) return null;
            statement = connection.createStatement();
            return statement.executeQuery(request);
        } catch (SQLException e) {
            System.err.println("Błąd podczas odczytu elementów z bazy danych");
            return null;
        }
    }
}
