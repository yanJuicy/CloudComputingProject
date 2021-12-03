package com.example.myapp;

import com.amazonaws.services.ec2.model.Instance;
import com.example.myapp.aws.AwsHandler;

import java.util.List;
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
        String instanceId = null;
        switch (selectNum) {
            case 1:
                List<Instance> instanceList = handler.listInstances();
                print(instanceList);
                break;
            case 2:
                break;
            case 3:
                System.out.print("Enter instance id: ");
                instanceId = sc.next();
                handler.startInstance(instanceId);
                break;
            case 4:
                break;
            case 5:
                System.out.print("Enter instance id: ");
                instanceId = sc.next();
                handler.stopInstance(instanceId);
                break;
            case 6:
                System.out.print("Enter ami id: ");
                String amiId = sc.next();
                handler.createInstance(amiId);
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

    private static void print(List<Instance> instanceList) {
        System.out.println("Listing instances....");
        for(Instance instance : instanceList) {
            System.out.printf(
                    "[id] %s, " +
                            "[AMI] %s, " +
                            "[type] %s, " +
                            "[state] %10s, " +
                            "[monitoring state] %s\n",
                    instance.getInstanceId(),
                    instance.getImageId(),
                    instance.getInstanceType(),
                    instance.getState().getName(),
                    instance.getMonitoring().getState());
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
