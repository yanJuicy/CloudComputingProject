package com.example.myapp.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.s3.AmazonS3Client;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class AwsHandlerTest {

    static AmazonEC2 ec2;

    @Test
    @DisplayName("자격 증명 테스트")
    public void credentials_test() {

        InstanceProfileCredentialsProvider credentials =
                InstanceProfileCredentialsProvider.createAsyncRefreshingProvider(true);

        AmazonS3Client.builder()
                .withCredentials(credentials)
                .build();

        System.out.println(credentials);

        try {
            credentials.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void init() {

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

    @Test
    @DisplayName("리스트 테스트")
    public void listInstances_test() {
        init();

        System.out.println("Listing instances....");
        boolean done = false;
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        System.out.println(request);
        while(!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);
            System.out.println(response);
            for(Reservation reservation : response.getReservations()) {
                for(Instance instance : reservation.getInstances()) {
                    System.out.printf(
                            "[id] %s, " +
                                    "[AMI] %s, " +
                                    "[type] %s, " +
                                    "[state] %10s, " +
                                    "[monitoring state] %s",
                            instance.getInstanceId(),
                            instance.getImageId(),
                            instance.getInstanceType(),
                            instance.getState().getName(),
                            instance.getMonitoring().getState());
                }
                System.out.println();
            }
            request.setNextToken(response.getNextToken());
            if(response.getNextToken() == null) {
                done = true;
            }
        }
    }

    @Test
    @DisplayName("인스턴스 시작 테스트")
    public void startInstanceTest()
    {
//        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        init();

        String instance_id = "i-016140d66ff7894a0";
        StartInstancesRequest request = new StartInstancesRequest()
                .withInstanceIds(instance_id);

        ec2.startInstances(request);
        System.out.printf("Successfully started instance %s", instance_id);
    }

    @Test
    @DisplayName("인스턴스 생성 테스트")
    public void createInstanceTest() {
        init();
        try{
            RunInstancesRequest run_request =
                    new RunInstancesRequest()
                            .withImageId("ami-07a94112e645dc0fd")
                            .withInstanceType(InstanceType.T2Micro)
                            .withMaxCount(1)
                            .withMinCount(1);

            RunInstancesResult run_response = ec2.runInstances(run_request);
            String reservation_id = run_response
                    .getReservation()
                    .getInstances()
                    .get(0)
                    .getInstanceId();

            System.out.printf("Successfully started EC2 instance %s based on AMI %s", reservation_id, "ami-07a94112e645dc0fd");
        }catch(Exception e){
            throw new AmazonClientException("You cannot create this instance. Check the value you entered", e);
        }
    }


}
