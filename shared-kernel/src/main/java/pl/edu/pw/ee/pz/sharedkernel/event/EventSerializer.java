package pl.edu.pw.ee.pz.sharedkernel.event;

public interface EventSerializer {

  <ID extends AggregateId, E extends DomainEvent<ID>> byte[] serialize(E event);

  <ID extends AggregateId, E extends DomainEvent<ID>> E deserialize(byte[] data);
}
