package pl.darsonn.todolist;

import pl.darsonn.todolist.todo.TodoListHandler;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public final static Scanner scanner = new Scanner(System.in);
    public static String nickname;
    public static void main(String[] args) {
        System.out.println("==============================");
        System.out.println("Witam w aplikacji ToDoLIST!");
        System.out.println("==============================");

        System.out.print("Podaj swoją nazwę użytkownika: ");
        nickname = scanner.nextLine().replaceAll(" ", "");

        showOptions();
    }

    public static void showOptions() {
        String[] opcje = {"Wyświetlenie tablicy", "Ustawienia", "Wyjdź"};

        System.out.println("===============================");
        System.out.println("Witaj " + nickname + "!");

        try {
            System.out.println("Wybierz jaką operację chcesz wykonać: ");

            int a = 1;
            for (var opcja : opcje) {
                System.out.println("[" + a + "] " + opcja);
                a++;
            }

            a = scanner.nextInt();

            switch (a) {
                case 1 -> TodoListHandler.todoListsShow();
                case 2 -> SettingsHandler.showSettingsMenu();
                case 3 -> {
                    System.out.println("Przykro nam, że nasz opuszczasz!");
                    System.exit(0);
                }
                default -> {
                    System.out.println("Nie ma takiej opcji!");
                    showOptions();
                }
            }
        } catch (InputMismatchException exception) {
            System.out.println("Oczekiwano liczby!");
            showOptions();
        }
    }
}