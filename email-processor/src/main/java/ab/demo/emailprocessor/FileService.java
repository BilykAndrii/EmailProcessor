package ab.demo.emailprocessor;

import com.google.common.collect.Lists;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FileService {

    @Value("${secret}")
    private String secret;

    @Value("${tmp.file}")
    private String tmpFile;

    @Value("${csv.file}")
    private String csvFile;

    @Value("${delimiter}")
    private String delimiter;

    @Value("${csv.header:false}")
    private boolean header;

    @Value("${pattern}")
    private String pattern;

    @Value("${chunk.size}")
    private int chunkSize;

    @Autowired
    private SenderService senderService;

    private Function<String, DataModel> mapToModel = getMapFunction();

    private DataModel.DataModelBuilder builder = new DataModel.DataModelBuilder();

    public void processData(MultipartFile file) throws IOException {
        List<DataModel> extractedData;
        InputStream inputStream = unzip(file);

        extractedData = readFile(inputStream);

        List<DataModel> filtered = filterData(extractedData);

        sendInChunks(filtered);
    }

    private void sendInChunks(List<DataModel> data) {
        List<List<DataModel>> chunks = Lists.partition(data, chunkSize);
        chunks.forEach(this::sendChunk);
    }

    private void sendChunk(List<DataModel> chunk) {
        senderService.send(chunk);
    }

    private List<DataModel> filterData(List<DataModel> data) {
        return data.stream().filter(model -> {
            String expected = String.format(pattern, model.getFirstName().trim(), model.getLastName().trim())
                    .replaceAll("\"", "");
            return expected.equalsIgnoreCase(model.getEmail().trim());
        }).collect(Collectors.toList());
    }

    private List<DataModel> readFile(InputStream inputStream) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            int linesToSkip = header ? 1 : 0;

            List<DataModel> extracted = br.lines().skip(linesToSkip).map(mapToModel).collect(Collectors.toList());
            br.close();
            return extracted;

        } catch (Exception ex) {
            ex.printStackTrace();
            return Collections.emptyList();
        }
    }

    private InputStream unzip(MultipartFile uploadedFile) throws IOException {

        final Path path = Files.createTempFile(tmpFile, ".tmp");

        File file = path.toFile();
        file.deleteOnExit();

        try (OutputStream os = new FileOutputStream(file)) {
            os.write(uploadedFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            ZipFile zipFile = new ZipFile(file);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(secret.toCharArray());
            }
            FileHeader fileHeader = zipFile.getFileHeader(csvFile);
            return zipFile.getInputStream(fileHeader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Function<String, DataModel> getMapFunction() {
        return line -> {

            String[] values = line.split(delimiter);

            return builder
                    .firstName(values[0].replaceAll("\"", ""))
                    .lastName(values[1].replaceAll("\"", ""))
                    .email(values[2].replaceAll("\"", ""))
                    .param0(values[3].replaceAll("\"", ""))
                    .param1(values[4].replaceAll("\"", ""))
                    .param2(values[5].replaceAll("\"", ""))
                    .param3(values[6].replaceAll("\"", ""))
                    .param4(values[7].replaceAll("\"", ""))
                    .param5(values[8].replaceAll("\"", ""))
                    .param6(values[9].replaceAll("\"", ""))
                    .param7(values[10].replaceAll("\"", ""))
                    .param8(values[11].replaceAll("\"", ""))
                    .build();

        };
    }

}



