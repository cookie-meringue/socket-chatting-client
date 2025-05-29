package woowacourse.tcp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class TcpChattingClient {

    public void start(InetAddress serverHost, int serverPort) {
        try (
            // TCP 소켓을 생성하여 서버에 연결한다
            // 서버의 호스트 주소와 포트 번호를 사용하여 소켓을 생성한다
            Socket socket = new Socket(serverHost, serverPort);

            // PrintWriter 는 서버로 데이터를 전송하는 데 사용된다
            // true 플래그를 사용하여 자동으로 버퍼를 비우도록 설정한다
            // out 에 데이터가 전송될 때마다 자동으로 flush() 메서드가 호출되어, 데이터를 즉시 서버로 전송한다
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // BufferedReader 는 서버로부터 데이터를 수신하는 데 사용된다
            // InputStreamReader 를 사용하여 소켓의 입력 스트림을 읽는다
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("서버에 연결되었습니다. 메시지를 입력하세요 (EXIT 입력 시 종료)");

            // 서버로부터 메시지를 수신하는 스레드
            // 별도의 스레드를 생성하여 서버로부터 메시지를 비동기적으로 수신한다
            Thread receiveThread = new Thread(() -> {
                try {
                    // 서버로부터 받는 메시지
                    String serverMessage;

                    // BufferedReader 를 사용하여 서버로부터 메시지를 읽는다
                    // while 루프를 사용하여 서버로부터 오는 메시지를 지속적으로 읽는다
                    // in.readLine() 메서드의 반환값이 null 이라면 서버 연결이 종료된 것이다
                    // in 은 socket 의 입력 스트림을 읽는 BufferedReader 이므로, 서버가 연결을 종료하면 null 을 반환하기 때문이다
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (Exception e) {
                    System.out.println("서버 연결이 종료되었습니다.");
                }
            });
            // receiveThread 를 데몬 스레드로 설정하여, 메인 스레드가 종료되면 receiveThread 도 함께 종료되도록 한다
            // 데몬 스레드는 백그라운드에서 실행되며, 메인 스레드가 종료되면 자동으로 종료되는 쓰레드이다
            receiveThread.setDaemon(true);

            // receiveThread 를 시작하여 서버로부터 메시지를 비동기적으로 수신한다
            receiveThread.start();

            // 사용자 입력을 서버로 전송
            // Scanner 를 사용하여 사용자로부터 메시지를 입력받는다
            while (true) {
                // 사용자로부터 메시지를 입력받는다
                System.out.print("메시지 입력 > ");
                String message = scanner.nextLine();

                // 입력받은 메시지를 서버로 전송한다
                // out 에 데이터가 전송될 때마다 자동으로 flush() 메서드가 호출되어, 데이터를 즉시 서버로 전송한다
                out.println(message);

                // 만약 사용자가 "EXIT" 를 입력하면, 클라이언트를 종료한다
                if (message.equals("EXIT")) {
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("서버 연결 실패: " + e.getMessage());
        }

        System.out.println("클라이언트를 종료합니다.");
    }
}
