package pl.edu.pw.ee.pz.file;

import io.minio.MinioAsyncClient;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class ExternalServiceFileConfiguration {

  @Produces
  MinioFileService minioFileService(MutinyMinioAsyncClient mutinyMinioAsyncClient) {
    return new MinioFileService(mutinyMinioAsyncClient);
  }

  @Produces
  MutinyMinioAsyncClient mutinyMinioAsyncClient(MinioAsyncClient minioAsyncClient) {
    return new MutinyMinioAsyncClient(minioAsyncClient);
  }

  @Produces
  MinioAsyncClient minioAsyncClient(MinioProperties minioProperties) {
    var clientProperties = minioProperties.client();
    return MinioAsyncClient.builder()
        .endpoint(clientProperties.uri())
        .credentials(clientProperties.accessKey(), clientProperties.secretKey())
        .build();
  }
}
