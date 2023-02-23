package org.app;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.util.Random;

public class S3Service {

    public static final String AUTUMN_BUCKET = "autumn-photo-bucket";
    private final S3Client s3 = S3Client.builder().region(Region.US_WEST_1).build();

    public String getRandomImageURL() {
        ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(AUTUMN_BUCKET).build();
        ListObjectsV2Response response = this.s3.listObjectsV2(request);
        List<S3Object> images = response.contents();
        Random random = new Random();
        int randomIdx = random.nextInt(images.size());
        return "https://autumn-photo-bucket.s3.us-west-1.amazonaws.com/" + images.get(randomIdx).key();
    }

}
