import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class server {
    public static void main(String[] args) throws IOException {
        //Ready the server with sockets, reader and writers
        System.out.println("Ready to use");

        ServerSocket serverSocket;
        Socket socket;
        InputStreamReader inputSR;
        OutputStreamWriter outputSW;
        BufferedReader bReader;
        BufferedWriter bWriter;


        //set server socket and get the socket address
        try {

            serverSocket = new ServerSocket(4321);
            System.out.println(serverSocket.getInetAddress());
            System.out.println(serverSocket.getLocalSocketAddress());
        } catch (IOException e) {
            System.out.println(e);
            return;
        }
        //check sockets and initialise readers and writers
        while (true) {
            try {

                socket = serverSocket.accept();
                inputSR = new InputStreamReader(socket.getInputStream());
                outputSW = new OutputStreamWriter(socket.getOutputStream());

                bReader = new BufferedReader(inputSR);
                bWriter = new BufferedWriter(outputSW);


                String message = bReader.readLine();
                String returnData = openUpData(message);

                System.out.println("Message Recieved and sent back");

                bWriter.write(returnData);
                bWriter.newLine();
                bWriter.flush();


                if ("QUIT".equals(returnData)) break;
            } catch (ParseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                System.out.println(e);
            }
        }

        socket.close();
        inputSR.close();
        outputSW.close();
        bReader.close();
        bWriter.close();

        System.out.println("Server Avslutas");

    }


    static String openUpData(String message) throws ParseException, IOException {
        if ("QUIT".equals(message)) return "QUIT";
        System.out.println(message);

        JSONParser parser = new JSONParser();
        JSONObject jsonOb = (JSONObject) parser.parse(message);
        String url = jsonOb.get("httpURL").toString();
        String method = jsonOb.get("httpMethod").toString();
        String[] urls = url.split("/");

        //fetch info from json file
        switch (urls[0]) {
            case "persons":
                if (method.equals("get")) {
                    JSONObject jsonReturn = new JSONObject();
                    jsonReturn.put("data", parser.parse(new FileReader("data/data.json")).toString());
                    jsonReturn.put("httpStatusCode", 200);
                    return jsonReturn.toJSONString();
                }
                break;
        }
        return "message Recieved";
    }
}
