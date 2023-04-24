package pl.edu.pw.ee.pz.sharedkernel.event;

record SerializableEvent<E extends DomainEvent>(
    byte[] event,
    Class<E> type
) {

}
