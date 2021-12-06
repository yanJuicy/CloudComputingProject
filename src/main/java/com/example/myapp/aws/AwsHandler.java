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
        ec2 = AmazonEC2ClientBuilder
                .standard()
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
            request.setNextToken(response.getNextToken());
            if (response.getNextToken() == null) {
                done = true;
            }
        }
        return instanceList;
    }

    // 인스턴스 시작
    public String startInstance(String instance_id) {
        StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance_id);
        ec2.startInstances(request);
        System.out.println("Starting ... " + instance_id);
        System.out.println("Successfully started instance " + instance_id);

        return instance_id;
    }

    // 인스턴스 생성
    public String createInstance(String ami_id) {

        RunInstancesRequest run_request = new RunInstancesRequest()
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
        System.out.println("Successfully stopped instance %s" + instance_id);
        return instance_id;
    }

    // 인스턴스 리부트
    public void rebootInstance(String instance_id) {
        RebootInstancesRequest request = new RebootInstancesRequest()
                .withInstanceIds(instance_id);

        RebootInstancesResult response = ec2.rebootInstances(request);
        System.out.println("Rebooting .... " + instance_id);
        System.out.println("Successfully rebooted instance " + instance_id);

    }

    // 이미지 리스트
    public List<Image> listImages() {
        DescribeImagesRequest request = new DescribeImagesRequest().withOwners("self");
        List<Image> imageList = ec2.describeImages(request).getImages();

        for (Image image : imageList) {
            System.out.printf("[Image ID] %s, " + "Owner ID] %s, " + "[AMI Status] %s, \n", image.getImageId(), image.getOwnerId(), image.getState());
        }
        return imageList;
    }

    // 사용가능한 존
    public void listAvailableZones() {
        DescribeAvailabilityZonesResult zones_response = ec2.describeAvailabilityZones();

        for(AvailabilityZone zone : zones_response.getAvailabilityZones()) {
            System.out.printf(
                    "Found availability zone %s " +
                            "with status %s " +
                            "in region %s\n",
                    zone.getZoneName(),
                    zone.getState(),
                    zone.getRegionName());
        }
    }

    // 사용가능한 지역
    public void listAvailableRegions() {
        DescribeRegionsResult regions_response = ec2.describeRegions();

        for(Region region : regions_response.getRegions()) {
            System.out.printf(
                    "Found region %s " +
                            "with endpoint %s\n",
                    region.getRegionName(),
                    region.getEndpoint());
        }
    }
}
