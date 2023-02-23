package org.app;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Decoder {

    public String getRecipientNumber(String body) throws UnsupportedEncodingException {
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
