package pl.edu.pw.ee.pz.event;

import pl.edu.pw.ee.pz.sharedkernel.event.AggregateId;
import pl.edu.pw.ee.pz.sharedkernel.event.DomainEvent;

record AggregateDomainEvent<ID extends AggregateId>(
    DomainEvent<ID> event,
    long revision
) {

}
