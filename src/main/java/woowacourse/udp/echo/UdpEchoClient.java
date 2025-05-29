package woowacourse.udp.echo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UdpEchoClient {

    // 클라이언트가 사용할 포트 번호
    private static final int PORT = 6000;

    // 서버가 응답하는 데이터를 저장하기 위한 버퍼 크기
    // 서버가 응답하는 데이터가 1024 바이트를 초과하는 경우, 나머지 데이터는 버려진다
    private static final int BUFFER_SIZE = 1024;

    // Scanner
    private static final Scanner SCANNER = new Scanner(System.in);

    public UdpEchoClient() {
        System.out.println("UDP Echo 클라이언트 초기화.");
    }

    /**
     * UDP 클라이언트를 시작하는 메서드
     *
     * @param serverAddress 서버의 IP 주소
     * @param serverPort 서버의 포트 번호
     */
    public void start(
        InetAddress serverAddress,
        int serverPort
    ) {
        try (
            // DatagramSocket 은 UDP 프로토콜을 사용하여 데이터를 송수신하는 데 사용된다
            // 서버에게 메시지를 보내고, 서버로부터 응답을 받는 데 사용하기 위한 UDP 소켓을 생성한다
            DatagramSocket socket = new DatagramSocket(PORT)
        ) {
            System.out.println("서버에 메시지를 보내세요.");
            while (true) {
                // 사용자로부터 메시지를 입력받는다
                System.out.print("입력 > ");
                String message = SCANNER.nextLine();

                // 입력받은 메시지를 바이트 배열로 변환한다
                // 서버에 메시지를 보내기 위해서는 문자열을 바이트 배열로 변환해야 한다
                byte[] dateToSend = message.getBytes();

                // DatagramPacket 객체를 생성하여 서버에 메시지를 보낸다
                // DatagramPacket 은 UDP 프로토콜을 사용하여 데이터를 송수신하는 데 사용된다
                // 서버에 전송할 데이터의 바이트 배열, 데이터의 길이, 서버의 IP 주소, 포트 번호를 지정한다
                DatagramPacket packetToSend = new DatagramPacket(
                    dateToSend, // 보낼 데이터
                    dateToSend.length, // 데이터의 길이
                    serverAddress, // 서버의 IP 주소
                    serverPort // 서버의 포트 번호
                );

                // DatagramSocket 을 사용하여 서버에 메시지를 전송한다
                // packetToSend 객체를 사용하여 서버에 데이터를 전송한다
                // 서버의 ip 주소나 포트 번호는 packetToSend 객체에 이미 설정되어 있다
                socket.send(packetToSend);

                // 서버로부터 응답을 받기 위한 바이트 배열을 초기화한다.
                byte[] buffer = new byte[BUFFER_SIZE];

                // DatagramPacket 객체를 생성하여 서버로부터 응답을 받을 준비를 한다
                // 서버에서 받은 패킷이 저장될 것이다.
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

                // 서버부터 메시지를 수신한다
                // 해당 메서드가 호출되면, 서버가 메시지를 보낼 때까지 블록된다
                // 즉, 서버가 메시지를 보내기 전까지 대기한다
                // 수신된 메시지는 receivedPacket 객체에 저장된다
                socket.receive(receivedPacket);

                // 수신된 데이터를 문자열로 변환한다
                // receivePacket.getData()는 수신된 데이터가 저장된 바이트 배열을 반환한다
                // offset 은 바이트 배열에서 문자열로 변환할 때 시작 위치를 지정하는 매개변수이다
                // receivePacket.getLength()는 수신된 데이터의 길이를 반환한다
                String response = new String(
                    receivedPacket.getData(),
                    0,
                    receivedPacket.getLength()
                );

                // 수신된 메시지를 콘솔에 출력한다
                System.out.println("[서버 응답] " + response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
