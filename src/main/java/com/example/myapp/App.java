package com.example.myapp;

import com.example.myapp.aws.AwsHandler;

import java.util.Scanner;

public class App {

    private static Scanner sc = new Scanner(System.in);
    private static AwsHandler handler;

    public static void main(String[] args) {
        start();
    }

    private static void start() {
        handler = AwsHandler.getAwsHandler();
        int selectNum = 0;
        while (true) {
            selectNum = menu();
            if (selectNum == 9) break;
            detailMenu(selectNum);
            System.out.println("\n");
        }
    }

    private static void detailMenu(int selectNum) {
        switch (selectNum) {
            case 1:
                handler.listInstances();
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            default:
                System.out.println("Wrong Access");
                break;
        }
    }

    private static int menu() {
        System.out.println("---------------------------------------------------");
        System.out.println("1. list instance\t\t\t2.available zones");
        System.out.println("3. start instance\t\t\t4.available regions");
        System.out.println("5. stop instance\t\t\t6.create instance");
        System.out.println("7. reboot instance\t\t\t8.list images");
        System.out.println("9. quit");
        System.out.println("---------------------------------------------------");
        System.out.print("Enter an integer: ");
        return sc.nextInt();
    }


}
