package org.modelmapper.bugs;

import static org.testng.Assert.assertTrue;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/20
 */
@Test
public class GH20 extends AbstractTest {
  public static class ServiceOperation {
    private String target;
    private Command cmd;

    public String getTarget() {
      return target;
    }

    public void setTarget(String target) {
      this.target = target;
    }

    public Command getCmd() {
      return cmd;
    }

    public void setCmd(Command cmd) {
      this.cmd = cmd;
    }
  }

  public static class ServiceOperationDTO {
    private String target;
    private String cmdStr;

    public String getTarget() {
      return target;
    }

    public void setTarget(String target) {
      this.target = target;
    }

    public String getCmd() {
      return cmdStr;
    }

    public void setCmd(String cmdStr) {
      this.cmdStr = cmdStr;
    }
  }

  public enum Command {
    Up, Down, Once, Pause, Cont, Hup, Alarm, Interrupt, Quit, Usr1("1"), Usr2("2"), Term, Kill, Exit, Status, Start, Stop, Restart, Shutdown, ForceStop(
        "force-stop"), ForceReload("force-reload"), ForceRestart("force-restart"), ForceShutdown(
        "force-shutdown"), Check, Invalid;

    private static final Map<String, Command> lookup = new HashMap<String, Command>();

    static {
      for (Command c : EnumSet.allOf(Command.class))
        lookup.put(c.toString(), c);
    }

    Command() {
      this.value = this.name().toLowerCase();
    }

    Command(String svCmd) {
      this.value = svCmd;
    }

    @Override
    public String toString() {
      return value;
    }

    public static Command fromString(String cmd) {
      return lookup.get(cmd);
    }

    private final String value;
  }

  public void test() {
    ServiceOperationDTO dto = new ServiceOperationDTO();
    dto.setCmd("force-stop");
    dto.setTarget("myService");

    modelMapper.addConverter(new Converter<String, Command>() {
      public Command convert(MappingContext<String, Command> context) {
        String source = context.getSource();
        return source == null ? null : Command.fromString(source);
      }
    });

    ServiceOperation domainObj = modelMapper.map(dto, ServiceOperation.class);
    assertTrue(domainObj.getCmd() == Command.ForceStop);
  }
}
