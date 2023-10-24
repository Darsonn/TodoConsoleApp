package pl.darsonn.todolist.todo;

import pl.darsonn.todolist.Main;
import pl.darsonn.todolist.database.DatabaseOperations;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TodoListHandler {
    static DatabaseOperations databaseOperations = new DatabaseOperations();
    private static ArrayList<String> lists;
    static Scanner scanner = new Scanner(System.in);
    public static void todoListsShow() {
        lists = databaseOperations.getLists();
        System.out.println("\n===============================");
        System.out.println("Dostępne listy:");

        for(int i = 0; i < lists.size(); i++) {
            System.out.println("[" + (i+1) + "] " + lists.get(i));
        }
        System.out.println("[" + (lists.size()+1) + "] Utwórz nową listę");

        int chosenList;

        System.out.print("Wybierz listę: ");
        try {
            chosenList = scanner.nextInt();
            if(chosenList >= 1 && chosenList < lists.size()+1) {
                showOptionsWithLists(chosenList);
            } else if(chosenList == (lists.size()+1)) {
                createNewList();
            } else {
                System.out.println("Podano liczbę spoza zakresu!");
                todoListsShow();
            }
        } catch (InputMismatchException exception) {
            System.err.println("Błędny wybór! Proszę podać liczbę.");
            Main.showOptions();
        }
    }

    private static void showOptionsWithLists(int chosenList) {
        String[] options = {
                "Wyświetl zawartość listy",
                "Dodaj nowy element",
                "Usuń element",
                "Usuń listę",
                "Cofnij",
                "Wyjdź do menu głównego"
        };

        chosenList--;
        System.out.println("\n===============================");
        System.out.println("Wybrano listę o nazwie:\n" + lists.get(chosenList));
        System.out.println();

        System.out.format("+----+----------------------------+%n");
        System.out.format("| ID |           Opcja            |%n");
        System.out.format("+----+----------------------------+%n");

        String table = "| %-2s | %-26s |%n";

        for(int i = 0; i < options.length; i++) {
            System.out.format(table, (i+1), options[i]);
        }

        System.out.format("+----+----------------------------+%n");

        String chosenOption = scanner.next();

        switch(chosenOption) {
            case "1" -> {
                try {
                    showListElements(chosenList);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            case "2" -> addNewElement(chosenList);
            case "3" -> deleteElement(chosenList);
            case "4" -> deleteList(chosenList);
            case "5" -> todoListsShow();
            case "6" -> Main.showOptions();
            default -> {
                System.out.println("Błędna opcja");
                showOptionsWithLists(chosenList);
            }
        }
    }

    private static void showListElements(int chosenList) throws SQLException {
        ResultSet resultSet = databaseOperations.getElements(lists.get(chosenList));

        System.out.println("\n===============================");
        System.out.println("Elementy listy: " + lists.get(chosenList));
        System.out.println();

        System.out.format("+----+------------+-----------------+-------------------------+-----------------------------------------------+%n");
        System.out.format("| ID |   Author   |  Creation Date  |          Title          |                    Comment                    |%n");
        System.out.format("+----+------------+-----------------+-------------------------+-----------------------------------------------+%n");

        String table = "| %-2s | %-10s | %-15s | %-23s | %-45s |%n";

        if (resultSet != null) {
            while (resultSet.next()) {
                System.out.format(
                        table,
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getString(5)
                );
            }
            resultSet.close();
        }

        System.out.format("+----+------------+-----------------+-------------------------+-----------------------------------------------+%n");
        System.out.print("\nNaciśnij ENTER aby kontynuować...");
        try {
            System.in.read(new byte[2]);
        } catch (IOException ignored) {}
        showOptionsWithLists(++chosenList);
    }

    private static void addNewElement(int chosenList) {
        System.out.println("\n===============================");
        System.out.println("Dodawanie elementu do listy:");
        System.out.println(lists.get(chosenList));

        System.out.println("Podaj tytuł");
        String title = scanner.next() + scanner.nextLine();

        System.out.println("Podaj opis");
        String comment = scanner.next() + scanner.nextLine();

        if(databaseOperations.addElementToList(Main.nickname, title, comment, lists.get(chosenList))) {
            System.out.println("Pomyślnie dodano element do listy.");
        } else {
            System.err.println("Wystąpił błąd podczas dodawania elementu do listy!");
        }

        System.out.print("\nNaciśnij ENTER aby kontynuować...");
        try {
            System.in.read(new byte[2]);
        } catch (IOException ignored) {}

        showOptionsWithLists((chosenList+1));
    }

    private static void deleteElement(int chosenList) {
        System.out.println("\n===============================");

        System.out.print("Podaj ID elementu do usunięcia: ");
        String wiadomosc = scanner.next();

        if(databaseOperations.removeElementFromList(wiadomosc, lists.get(chosenList))) {
            System.out.println("Pomyślnie usunięto element.");
        } else {
            System.err.println("Operacja usuwania elementu nie powiodła się!");
        }

        System.out.print("\nNaciśnij ENTER aby kontynuować...");
        try {
            System.in.read(new byte[2]);
        } catch (IOException ignored) {}
        showOptionsWithLists(++chosenList);
    }

    private static void createNewList() {
        System.out.println("\n===============================");
        System.out.print("Podaj nazwę nowej listy: ");

        String newListName = scanner.next();
        if(databaseOperations.checkIfTableExists(newListName)) {
            System.out.println("Podana nazwa listy jest już zajęta!");
            todoListsShow();
        }
        if(databaseOperations.createNewList(newListName)) {
            System.out.println("Pomyślnie utworzono listę o nazwie " + newListName);
        } else {
            System.err.println("Wystąpił błąd podczas tworzenia listy!");
        }

        System.out.print("\nNaciśnij ENTER aby kontynuować...");
        try {
            System.in.read(new byte[2]);
        } catch (IOException ignored) {}

        todoListsShow();
    }

    private static void deleteList(int chosenList) {
        System.out.println("\n===============================");
        if(databaseOperations.dropList(lists.get(chosenList))) {
            System.out.println("Pomyślnie usunięto listę: " + lists.get(chosenList));
        } else {
            System.err.println("Operacja usuwania listy nie powiodła się!");
        }

        System.out.print("\nNaciśnij ENTER aby kontynuować...");
        try {
            System.in.read(new byte[2]);
        } catch (IOException ignored) {}

        todoListsShow();
    }
}
