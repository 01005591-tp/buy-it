package pl.edu.pw.ee.pz.file;

public record File(
    FileName name,
    FilePath path,
    FileContent content
) {

}
