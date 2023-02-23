package org.app;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;

public class LambdaHandler implements RequestHandler<Map<String, Object>, String> {
    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
    public static final String TWILIO_PHONE_NUMBER = System.getenv("TWILIO_PHONE_NUMBER");

    public static final S3Service s3Service = new S3Service();

    public static final Decoder decoder = new Decoder();

//    public static DynamoDbService dbService = new DynamoDbService();

    public String handleRequest(Map<String, Object> input, Context context) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        String recipient = null;

        try {
            recipient = decoder.getRecipientNumber((String) input.get("body"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//        if(!dbService.processRequest(recipient)) {
//            throw new RuntimeException("Request is invalid: Photo request exceeds 1 every 24 hours.");
//        }

        System.out.println("Recipient: " + recipient);
        String imageURL = s3Service.getRandomImageURL();


        Message message = Message.creator(
                new PhoneNumber(recipient),
                new PhoneNumber(TWILIO_PHONE_NUMBER),
                "Here's a random photo of Autumn <3")
                .setMediaUrl(Arrays.asList(URI.create(imageURL)))
                .create();

        return message.getAccountSid();
    }
}