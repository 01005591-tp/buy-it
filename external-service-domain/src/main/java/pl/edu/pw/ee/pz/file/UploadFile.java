package pl.edu.pw.ee.pz.file;

public record UploadFile(
    FileName name,
    FilePath path,
    FileContent content,
    Size size
) {

  public record Size(long value) {

  }
}
