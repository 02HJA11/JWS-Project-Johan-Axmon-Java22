import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.*;

import java.util.Scanner;
import java.util.Set;

public class Main {
    //Ready up sockets and alike for usage later
    static Socket socket = null;
    static InputStreamReader inputSR = null;
    static OutputStreamWriter outputSW = null;
    static BufferedReader bReader = null;
    static BufferedWriter bWriter = null;

    public static void main(String[] args) throws ParseException {
        System.out.println("Client är nu redo");

        while (true) {
            String strRequest = userInput();

            String strResponse = connectToServer(strRequest);


            if ("QUIT".equals(strResponse)) break;


            openResponse(strResponse);
        }

        try {

            if (socket != null) socket.close();
            if (inputSR != null) inputSR.close();
            if (outputSW != null) outputSW.close();
            if (bWriter != null) bWriter.close();
            if (bReader != null) bReader.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("Client Avslutas");
    }

//initialize connection to the server
    static String connectToServer(String strRequest) {
        try {

            socket = new Socket("localhost", 4321);

            inputSR = new InputStreamReader(socket.getInputStream());
            outputSW = new OutputStreamWriter(socket.getOutputStream());
            bReader = new BufferedReader(inputSR);
            bWriter = new BufferedWriter(outputSW);

            Scanner scan = new Scanner(System.in);

            while (true) {


                bWriter.write(strRequest);
                bWriter.newLine();
                bWriter.flush();

                String resp = bReader.readLine();

                return resp;


            }
        } catch (UnknownHostException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } finally {

        }

        return "hej";
    }
    //user input
    static String userInput() {

        System.out.println("1. Get persons.json");
        System.out.println("2. Close");


        Scanner scan = new Scanner(System.in);
        System.out.print("Select: ");

        String val = scan.nextLine();


        switch (val) {
            case "1": {

                JSONObject jsonReturn = new JSONObject();
                jsonReturn.put("httpURL", "persons");
                jsonReturn.put("httpMethod", "get");

                System.out.println(jsonReturn.toJSONString());


                return jsonReturn.toJSONString();

            }
            case "2": {
                return "QUIT";
            }
        }

        return "error";
    }

    static String openResponse(String resp) throws ParseException {
        String testReturn = "";
        JSONParser parser = new JSONParser();
        JSONObject serverResponse = (JSONObject) parser.parse(resp);
        if ("200".equals(serverResponse.get("httpStatusCode").toString())) {
            JSONObject data = (JSONObject) parser.parse((String) serverResponse.get("data"));
            Set<String> keys = data.keySet();
            for (String x : keys) {
                JSONObject person = (JSONObject) data.get(x);
                System.out.println(person.get("name"));
                testReturn += person.get("name");
            }
        }

        return testReturn;
    }
}