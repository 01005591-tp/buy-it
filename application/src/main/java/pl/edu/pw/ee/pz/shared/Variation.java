package pl.edu.pw.ee.pz.shared;

import java.util.List;

@SuppressWarnings("rawtypes")
public record Variation(
    String id,
    // TODO: Must be raw type, because Jackson cannot serialize it properly otherwise
    //       Register custom Jackson serializer to serialize List<Attribute> properly.
    List<Attribute> attributes
) {

}
