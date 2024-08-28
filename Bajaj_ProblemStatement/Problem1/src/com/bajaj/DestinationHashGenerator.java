package com.bajaj;


           
             
    

    
    import java.io.FileReader;
    import java.io.IOException;
    import java.security.MessageDigest;
    import java.security.NoSuchAlgorithmException;
    import java.util.Random;
    import java.util.Scanner;
    import org.json.JSONObject;
    import org.json.JSONTokener;

    public class DestinationHashGenerator {

        public static void main(String[] args) {
            if (args.length != 2) {
                System.out.println("Usage: java DestinationHashGenerator <PRN_NUMBER> <JSON_FILE_PATH>");
                return;
            }

            String prnNumber = args[0];
            String jsonFilePath = args[1];

            // Validate PRN number
            if (prnNumber == null || prnNumber.isEmpty()) {
                System.out.println("PRN number is required");
                return;
            }

            // Generate random string
            String randomString = generateRandomString(8);

            try {
                // Read JSON file
                String destinationValue = readDestinationFromJson(jsonFilePath);

                if (destinationValue == null) {
                    System.out.println("Key 'destination' not found in the JSON file");
                    return;
                }

                // Create string to hash
                String stringToHash = prnNumber + destinationValue + randomString;

                // Calculate MD5 hash
                String md5Hash = calculateMD5(stringToHash);

                // Print result
                System.out.println(md5Hash + ";" + randomString);
                
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        private static String readDestinationFromJson(String filePath) throws IOException {
            try (FileReader reader = new FileReader(filePath)) {
                JSONTokener tokener = new JSONTokener(reader);
                JSONObject jsonObject = new JSONObject(tokener);
                return findDestination(jsonObject);
            }
        }

        private static String findDestination(JSONObject jsonObject) {
            if (jsonObject.has("destination")) {
                return jsonObject.getString("destination");
            }

            for (String key : jsonObject.keySet()) {
                Object value = jsonObject.get(key);
                if (value instanceof JSONObject) {
                    String result = findDestination((JSONObject) value);
                    if (result != null) {
                        return result;
                    }
                } else if (value instanceof org.json.JSONArray) {
                    org.json.JSONArray array = (org.json.JSONArray) value;
                    for (int i = 0; i < array.length(); i++) {
                        Object element = array.get(i);
                        if (element instanceof JSONObject) {
                            String result = findDestination((JSONObject) element);
                            if (result != null) {
                                return result;
                            }
                        }
                    }
                }
            }
            return null;
        }

        private static String generateRandomString(int length) {
            String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            Random random = new Random();
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            return sb.toString();
        }

        private static String calculateMD5(String input) throws NoSuchAlgorithmException {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
    }



