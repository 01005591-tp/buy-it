package pl.edu.pw.ee.pz.sharedkernel.command;

import io.smallrye.mutiny.Uni;
import java.lang.reflect.ParameterizedType;

public interface CommandHandler<C extends Command, R> {

  default Class<C> commandType() {
    var parameterizedType = (ParameterizedType) (this.getClass().getGenericInterfaces()[0]);
    @SuppressWarnings("unchecked")
    var type = (Class<C>) parameterizedType.getActualTypeArguments()[0];
    return type;
  }

  Uni<R> handle(C command);

  interface NoResultCommandHandler<C extends Command> extends CommandHandler<C, Void> {

  }

  static <C extends Command, R> CommandHandler<C, R> handler(Class<C> commandType, CommandHandler<C, R> handler) {
    return new CommandHandler<C, R>() {
      @Override
      public Uni<R> handle(C command) {
        return handler.handle(command);
      }

      @Override
      public Class<C> commandType() {
        return commandType;
      }
    };
  }
}
