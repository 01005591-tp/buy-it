package pl.edu.pw.ee.pz.file;

import io.smallrye.mutiny.Uni;

public interface FileService {

  Uni<Void> upload(UploadFileCommand command);

  Uni<File> download(DownloadFileQuery query);
}
