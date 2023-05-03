package pl.edu.pw.ee.pz.brand;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.ee.pz.brand.port.BrandAggregatePort;
import pl.edu.pw.ee.pz.sharedkernel.command.CommandHandler.NoResultCommandHandler;

@RequiredArgsConstructor
public class ChangeBrandCodeCommandHandler implements NoResultCommandHandler<ChangeBrandCodeCommand> {

  private final BrandAggregatePort brandAggregatePort;

  @Override
  public Uni<Void> handle(ChangeBrandCodeCommand command) {
    return brandAggregatePort.findById(command.id())
        .onItem().invoke(brand -> brand.changeCode(command.code()))
        .onItem().transformToUni(brandAggregatePort::save);
  }
}
