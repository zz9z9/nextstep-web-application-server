package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class IOUtils {

    private static final String CURRENT_DIRECTORY = "./";
    private static final String FILE_ROOT_DIRECTORY = "/webapp";

    /**
     * @param BufferedReader는
     *            Request Body를 시작하는 시점이어야
     * @param contentLength는
     *            Request Header의 Content-Length 값이다.
     * @return
     * @throws IOException
     */
    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }

    public static byte[] convertFileToByte(String fileName) throws IOException {
        String filePath = CURRENT_DIRECTORY + FILE_ROOT_DIRECTORY + fileName;
        File file = new File(filePath);

        return Files.readAllBytes(file.toPath());
    }
}
