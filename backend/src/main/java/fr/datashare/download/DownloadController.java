package fr.datashare.download;

import fr.datashare.download.dto.DownloadInfo;
import fr.datashare.download.dto.DownloadRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Am.Yo : routes publiques de téléchargement
@RestController
@RequestMapping("/api/download")
public class DownloadController {

    private final DownloadService downloadService;

    public DownloadController(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @GetMapping("/{token}")
    public DownloadInfo info(@PathVariable String token) {
        return downloadService.getInfo(token);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Resource> download(
            @PathVariable String token,
            @RequestBody(required = false) DownloadRequest request) {
        String password = request == null ? null : request.password();
        return downloadService.download(token, password);
    }
}
