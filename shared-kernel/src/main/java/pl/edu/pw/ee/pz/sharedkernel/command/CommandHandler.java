package pl.edu.pw.ee.pz.sharedkernel.command;

import io.smallrye.mutiny.Uni;
import java.lang.reflect.ParameterizedType;

public interface CommandHandler<C extends Command> {

  default Class<C> commandType() {
    var parameterizedType = (ParameterizedType) (this.getClass().getGenericInterfaces()[0]);
    @SuppressWarnings("unchecked")
    var type = (Class<C>) parameterizedType.getActualTypeArguments()[0];
    return type;
  }

  Uni<Void> handle(C command);

  static <C extends Command> CommandHandler<C> handler(Class<C> commandType, CommandHandler<C> handler) {
    return new CommandHandler<C>() {
      @Override
      public Uni<Void> handle(C command) {
        return handler.handle(command);
      }

      @Override
      public Class<C> commandType() {
        return commandType;
      }
    };
  }
}
