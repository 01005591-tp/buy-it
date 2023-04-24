package pl.edu.pw.ee.pz.file;

import static lombok.AccessLevel.PACKAGE;
import static pl.edu.pw.ee.pz.sharedkernel.mutiny.MutinyUtil.uniFromCompletionStageCallable;

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
import pl.edu.pw.ee.pz.sharedkernel.concurrency.CompletableFutureUtil;

@RequiredArgsConstructor(access = PACKAGE)
class MutinyMinioAsyncClient {

  private final MinioAsyncClient minioClient;

  Uni<ObjectWriteResponse> putObject(PutObjectArgs args) {
    return uniFromCompletionStageCallable(() -> minioClient.putObject(args));
  }

  Uni<Boolean> bucketExists(BucketExistsArgs args) {
    return uniFromCompletionStageCallable(() -> minioClient.bucketExists(args));
  }

  Uni<GetObjectResponse> getObject(GetObjectArgs args) {
    return uniFromCompletionStageCallable(() -> minioClient.getObject(args));
  }

  CompletableFuture<Boolean> bucketExistsCompletableFuture(BucketExistsArgs args, Executor executor) {
    return CompletableFutureUtil.callAsync(() -> minioClient.bucketExists(args), executor)
        .thenCompose(Function.identity());
  }

  Uni<Void> makeBucket(MakeBucketArgs args) {
    return uniFromCompletionStageCallable(() -> minioClient.makeBucket(args));
  }
}
