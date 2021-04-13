package ab.demo.emailprocessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static java.util.Objects.nonNull;

@RestController
public class FileController {

    private static final String ZIP_FORMAT = "application/zip";
    private static final String RESPONSE = "file uploaded";

    @Autowired
    private FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String uploadFile(@RequestParam("file") MultipartFile file) {

        if (!isValid(file)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is not valid ZIP!");
        }

        try {
            fileService.processData(file);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing file!");
        }

        return RESPONSE;
    }

    private boolean isValid(MultipartFile file) {
        return nonNull(file) && ZIP_FORMAT.equals(file.getContentType());
    }
}