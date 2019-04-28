package zone.arctic.ytpplus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zone.arctic.ytpplus.effect.Effect;
import zone.arctic.ytpplus.utility.FileUtilities;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Generator {

	private final Logger logger;
	private final GeneratorSettings settings;
	private final GeneratorTools tools;

	private Thread process = null;

	public Generator(String name, GeneratorSettings settings) {
		this.logger = LoggerFactory.getLogger(name);
		this.settings = settings;
		if (settings.getSources().isEmpty()) {
			throw new IllegalArgumentException("Empty sources list in settings!");
		}
		this.tools = new GeneratorTools(settings.getFfmpegCommand(),
				settings.getFfprobeCommand(),
				settings.getMagickCommand());

	}

	public GeneratorSettings getSettings() {
		return settings;
	}

	public boolean isRunning() {
		return process != null && process.isAlive();
	}

	public void start() {
		logger.info("Generation is starting!");
		if (isRunning()) {
			throw new IllegalStateException("The generation process is already running!");
		}
		process = new Thread(() -> {
			try {
				// Prepare output file
				Path outputFile = settings.getOutputFile();
				if (Files.exists(outputFile)) {
					if (settings.isOverwriteFile()) {
						Files.delete(outputFile);
					} else {
						throw new FileAlreadyExistsException("The output file already exists!");
					}
				}
				// Prepare workspace
				Path workDirectory = settings.getWorkDirectory();
				if (Files.exists(workDirectory)) {
					FileUtilities.deleteDirectoryWithContent(workDirectory);
				}
				Files.createDirectories(workDirectory);

				List<Path> clips = new ArrayList<>();
				logger.info("Processing clips... ({} available, {} max)", settings.getSources().size(), settings.getMaxClips());
				for (int i = 0; i < settings.getMaxClips(); i++) {
					Path clipTempFile = workDirectory.resolve("clip" + i + ".mp4");

					// TODO: make insertion probability configurable?
					if (settings.isInsertTransitionClips() && ThreadLocalRandom.current().nextInt(0, 15) == 15) {
						Path pickedInsertion = pickInsertion();
						Duration duration = tools.getDuration(pickedInsertion);
						logger.info("{}/{}: picked insertion clip {}, duration: {}", i, settings.getMaxClips(), pickedInsertion.getFileName(), duration);
						tools.encodeVideo(pickedInsertion, clipTempFile);
					} else {
						Path sourceToPick = settings.getSources().get(ThreadLocalRandom.current().nextInt(settings.getSources().size() - 1));
						Duration duration = tools.getDuration(sourceToPick);
						logger.info("{}/{}: picked clip {}, duration: {}", i + 1, settings.getMaxClips(), sourceToPick.getFileName(), duration);
						Duration startOfClip = Duration.ofMillis(ThreadLocalRandom.current().nextLong(duration.toMillis() - settings.getMaxStreamDuration()));
						Duration endOfClip = Duration.ofMillis(startOfClip.toMillis() + ThreadLocalRandom.current().nextLong(settings.getMinStreamDuration(), settings.getMaxStreamDuration()))
						logger.info(" > Beginning: {}", startOfClip);
						logger.info(" > Ending: {}", endOfClip);
						tools.snipVideo(sourceToPick, startOfClip, endOfClip, clipTempFile);
					}

					// Add a random effect to the video
					Effect effect = settings.getEffects().get(ThreadLocalRandom.current().nextInt(0, settings.getEffects().size()));
					logger.info(" > Applying effect {}...", effect.getName());
					effect.apply(clipTempFile);
					clips.add(clipTempFile);
				}
				tools.concatenateVideo(clips, outputFile);
			} catch (Exception ex) {
				throw new RuntimeException("Unable to generate the video", ex);
			}
		});
		process.start();
	}
}
