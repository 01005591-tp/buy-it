package pl.edu.pw.ee.pz.file;

import static lombok.AccessLevel.PACKAGE;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MakeBucketArgs;
import io.minio.MinioAsyncClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.smallrye.mutiny.Uni;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.ee.pz.sharedkernel.concurrency.CompletableFutureUtil;
import pl.edu.pw.ee.pz.sharedkernel.function.UncheckedSupplier;

@Slf4j
@RequiredArgsConstructor(access = PACKAGE)
class MutinyMinioAsyncClient {

  private final MinioAsyncClient minioClient;

  Uni<ObjectWriteResponse> putObject(PutObjectArgs args) {
    return Uni.createFrom().completionStage(UncheckedSupplier.from(() -> minioClient.putObject(args)));
  }

  Uni<Boolean> bucketExists(BucketExistsArgs args) {
    return Uni.createFrom().completionStage(UncheckedSupplier.from(() -> minioClient.bucketExists(args)));
  }

  Uni<GetObjectResponse> getObject(GetObjectArgs args) {
    return Uni.createFrom().completionStage(UncheckedSupplier.from(() -> minioClient.getObject(args)));
  }

  CompletableFuture<Boolean> bucketExistsCompletableFuture(BucketExistsArgs args, Executor executor) {
    return CompletableFutureUtil.callAsync(() -> minioClient.bucketExists(args), executor)
        .thenCompose(Function.identity());
  }

  Uni<Void> makeBucket(MakeBucketArgs args) {
    return Uni.createFrom().completionStage(UncheckedSupplier.from(() -> minioClient.makeBucket(args)));
  }
}
