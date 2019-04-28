package zone.arctic.ytpplus.effect;

import java.nio.file.Path;

public interface Effect {
	String getName();
	void apply(Path file);
}
