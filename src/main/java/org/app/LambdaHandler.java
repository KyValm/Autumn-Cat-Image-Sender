package org.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

public class LambdaHandler implements RequestHandler<Map<String, Object>, String> {
    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
    public static final String TWILIO_PHONE_NUMBER = System.getenv("TWILIO_PHONE_NUMBER");

    public static final String AUTUMN_BUCKET = "autumn-photo-bucket";
    private final S3Client s3 = S3Client.builder().region(Region.US_WEST_1).build();

    public String handleRequest(Map<String, Object> input, Context context) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        String recipient = null;
        try {
            recipient = this.getRecipientNumber((String) input.get("body"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("Recipient: " + recipient);
        String imageURL = this.getRandomImageURL();
        System.out.println("Image URL: " + imageURL);

        Message message = Message.creator(
                new PhoneNumber(recipient),
                new PhoneNumber(TWILIO_PHONE_NUMBER),
                "Here's a random photo of Autumn <3")
                .setMediaUrl(Arrays.asList(URI.create(imageURL)))
                .create();

        return message.getAccountSid();
    }

    private String getRandomImageURL() {
        ListObjectsV2Request request = ListObjectsV2Request.builder().bucket("autumn-photo-bucket").build();
        ListObjectsV2Response response = this.s3.listObjectsV2(request);
        List<S3Object> images = response.contents();
        Random random = new Random();
        int randomIdx = random.nextInt(images.size());
        return "https://autumn-photo-bucket.s3.us-west-1.amazonaws.com/" + images.get(randomIdx).key();
    }

    private String getRecipientNumber(String body) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.getDecoder().decode(body);
        String decodedBody = new String(decodedBytes);
        String[] parameters = decodedBody.split("&");
        Map<String, String> map = new HashMap<>();

        for (String parameter : parameters) {
            String[] keyValuePair = parameter.split("=");
            if (keyValuePair.length == 2) {
                map.put(keyValuePair[0], keyValuePair[1]);
            }
        }
        return URLDecoder.decode(map.get("From"), "UTF-8");
    }
}