package util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.NoSuchFileException;

import static org.junit.Assert.assertTrue;

public class IOUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(IOUtilsTest.class);

    @Rule
    public ExpectedException expectedExcetption = ExpectedException.none();

    @Test
    public void readData() throws Exception {
        String data = "abcd123";
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);

        logger.debug("parse body : {}", IOUtils.readData(br, data.length()));
    }

    @Test
    public void convertHtmlToByte() {
        String fileName = "/user/form.html";
        try {
            byte[] result = IOUtils.convertFileToByte(fileName);
            assertTrue(result.length > 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void requestNotExistFile() throws IOException {
        String fileName = "/notExist.html";
        expectedExcetption.expect(NoSuchFileException.class);

        IOUtils.convertFileToByte(fileName);
    }
}
