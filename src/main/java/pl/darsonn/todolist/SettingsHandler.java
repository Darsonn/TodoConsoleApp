package pl.darsonn.todolist;

import java.util.InputMismatchException;

public class SettingsHandler {
    public static void showSettingsMenu () {
        String[] settings = {"Zmiana nazwy użytkownika", "Powrót"};

        System.out.println("\n\n");
        System.out.println("==============================");
        System.out.println("Ustawienia");
        System.out.println("==============================");

        try {
            System.out.println("Wybierz jaką operację chcesz wykonać: ");

            int a = 1;
            for (var opcja : settings) {
                System.out.println("[" + a + "] " + opcja);
                a++;
            }

            a = Main.scanner.nextInt();

            switch (a) {
                case 1 -> changeUsername();
                case 2 -> Main.showOptions();
                default -> System.out.println("Nie ma takiej opcji!");
            }
        } catch (InputMismatchException exception) {
            System.out.println("Oczekiwano liczby!");
        }
    }
    public static void changeUsername() {
        System.out.print("Podaj nową nazwę użytkownika: ");
        Main.nickname = Main.scanner.next().replaceAll(" ", "");
        System.out.println("Pomyślnie wykonano zmianę użytkownika!");

        showSettingsMenu();
    }
}
