package pl.edu.pw.ee.pz.file;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "minio")
public interface MinioProperties {

  MinioClientProperties client();

  interface MinioClientProperties {

    String uri();

    String accessKey();

    String secretKey();
  }
}
