package zone.arctic.ytpplus;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import zone.arctic.ytpplus.effect.BuiltInEffects;
import zone.arctic.ytpplus.effect.Effect;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Value
public class GeneratorSettings {

	// Tool commands
	@Builder.Default
	private String ffmpegCommand = "ffmpeg";
	@Builder.Default
	private String ffprobeCommand = "ffprobe";
	@Builder.Default
	private String magickCommand = "magick";

	// Paths
	@NonNull
	private List<Path> sources;
	@NonNull
	private Path soundDirectory;
	@NonNull
	private Path musicDirectory;
	@NonNull
	private Path insertionDirectory;
	@NonNull
	private Path workDirectory;
	@NonNull
	private Path outputFile;

	// Settings
	@Builder.Default
	private boolean overwriteFile = false;
	@Builder.Default
	private long maxStreamDuration = 5000;
	@Builder.Default
	private long minStreamDuration = 400;
	@Builder.Default
	private int maxClips = 20;
	@Builder.Default
	private boolean insertTransitionClips = true;
	@Builder.Default
	private List<Effect> effects = Arrays.stream(BuiltInEffects.values())
			.map(BuiltInEffects::getEffect).collect(Collectors.toList());
}
