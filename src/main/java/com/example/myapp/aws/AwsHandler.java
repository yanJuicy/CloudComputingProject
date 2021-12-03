package com.example.myapp.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;

import java.util.ArrayList;
import java.util.List;

public class AwsHandler {

    private static AwsHandler awsHandler = null;
    private AmazonEC2 ec2;

    private AwsHandler() {
        init();
    }

    // 싱글톤
    public static AwsHandler getAwsHandler() {
        if (awsHandler == null)
            awsHandler = new AwsHandler();
        return awsHandler;
    }

    // 초기화
    private void init() {
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                            "Please make sure that your credentials file is at the correct " +
                            "location (~/.aws/credentials), and is in valid format.",
                    e);
        }
        ec2 = AmazonEC2ClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion("ap-northeast-2") /* check the region at AWS console */
                .build();
    }

    // 인스턴스 리스트
    public List<Instance> listInstances() {
        boolean done = false;
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        List<Instance> instanceList = new ArrayList<>();
        while (!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);
            for (Reservation reservation : response.getReservations()) {
                for (Instance instance : reservation.getInstances()) {
                    instanceList.add(instance);
                }
            }
            request.setNextToken(response.getNextToken());
            if (response.getNextToken() == null) {
                done = true;
            }
        }
        return instanceList;
    }

    // 인스턴스 시작
    public String startInstance(String instance_id) {
        StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instance_id);
        ec2.startInstances(request);
        System.out.println("Starting ... " + instance_id);
        System.out.printf("Successfully started instance %s", instance_id);

        return instance_id;
    }

    // 인스턴스 생성
    public String createInstance(String ami_id) {

        RunInstancesRequest run_request =
                new RunInstancesRequest()
                        .withImageId(ami_id)
                        .withInstanceType(InstanceType.T2Micro)
                        .withMaxCount(1)
                        .withMinCount(1);

        RunInstancesResult run_response = ec2.runInstances(run_request);
        String reservation_id = run_response
                .getReservation()
                .getInstances()
                .get(0)
                .getInstanceId();

        System.out.printf("Successfully started EC2 instance %s based on AMI %s", reservation_id, ami_id);

        return ami_id;
    }

    // 인스턴스 중지
    public String stopInstance(String instance_id) {
        StopInstancesRequest request = new StopInstancesRequest()
                .withInstanceIds(instance_id);
        ec2.stopInstances(request);
        System.out.printf("Successfully stopped instance %s", instance_id);
        return instance_id;
    }

}
