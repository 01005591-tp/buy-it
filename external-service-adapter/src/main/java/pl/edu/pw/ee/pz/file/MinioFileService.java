package pl.edu.pw.ee.pz.file;

import static lombok.AccessLevel.PACKAGE;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.PutObjectArgs;
import io.smallrye.mutiny.Uni;
import java.time.Duration;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = PACKAGE)
class MinioFileService implements FileService {

  private static final long PUT_OBJECT_PART_SIZE = -1;

  // avoid network calls for verifying bucket existence
  private final AsyncCache<String, Boolean> bucketExistsCache = Caffeine.newBuilder()
      .maximumSize(1_000L)
      .expireAfterAccess(Duration.ofDays(1L))
      .buildAsync();
  private final MutinyMinioAsyncClient minioClient;

  @Override
  public Uni<Void> upload(UploadFileCommand command) {
    var file = command.file();
    var bucketName = file.path().space().value();
    return makeBucketIfNotExists(bucketName)
        .onItem().transformToUni(success -> uploadFile(file));
  }

  @Override
  public Uni<File> download(DownloadFileQuery query) {
    var args = GetObjectArgs.builder()
        .bucket(query.path().space().value())
        .object(query.name().value())
        .build();
    return minioClient.getObject(args)
        .map(response -> new File(
            query.name(),
            query.path(),
            new FileContent(response)
        ));
  }

  private Uni<Void> makeBucketIfNotExists(String bucketName) {
    return bucketExists(bucketName)
        .onItem().transformToUni(exists -> exists
            ? Uni.createFrom().voidItem()
            : makeBucket(bucketName)
        );
  }

  private Uni<Boolean> bucketExists(String bucketName) {
    var bucketExistsQuery = BucketExistsArgs.builder().bucket(bucketName).build();
    return Uni.createFrom().completionStage(bucketExistsCache.get(
        bucketName,
        (bucket, executor) -> minioClient.bucketExistsCompletableFuture(bucketExistsQuery, executor)
    ));
  }

  private Uni<Void> makeBucket(String bucketName) {
    var makeBucketCommand = MakeBucketArgs.builder().bucket(bucketName).build();
    return minioClient.makeBucket(makeBucketCommand);
  }

  private Uni<Void> uploadFile(UploadFile file) {
    var putObjectArgs = PutObjectArgs.builder()
        .bucket(file.path().space().value())
        .object(file.name().value())
        .stream(file.content().content(), file.size().value(), -1)
        .build();
    return minioClient.putObject(putObjectArgs)
        .replaceWithVoid();
  }
}
