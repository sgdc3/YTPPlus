package zone.arctic.ytpplus;

import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.FilterGraph;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collection;

public class GeneratorTools {

	private final Path ffmpegExecutable;
	private final Path ffprobeExecutable;
	private final Path magickExecutable;

	public GeneratorTools(String ffmpegExecutable, String ffprobeExecutable, String magickExecutable) {
		this.ffmpegExecutable = Paths.get(ffmpegExecutable);
		this.ffprobeExecutable = Paths.get(ffprobeExecutable);
		this.magickExecutable = Paths.get(magickExecutable);
		if (!Files.isExecutable(this.ffmpegExecutable)) {
			throw new IllegalStateException("The specified ffmpeg executable isn't a runnable file!");
		}
		if (!Files.isExecutable(this.ffprobeExecutable)) {
			throw new IllegalStateException("The specified ffprobe executable isn't a runnable file!");
		}
		if (!Files.isExecutable(this.magickExecutable)) {
			throw new IllegalStateException("The specified magick executable isn't a runnable file!");
		}
	}

	private FFmpeg getFFMpeg() {
		return new FFmpeg(ffmpegExecutable);
	}

	private FFprobe getFFProbe() {
		return new FFprobe(ffprobeExecutable);
	}

	/**
	 * Returns the duration of the given video file.
	 *
	 * @param video input video path to analyze
	 * @return the duration of the video (in Duration format)
	 */
	public Duration getDuration(Path video) {
		FFprobeResult result = getFFProbe().setInput(video).execute();
		return Duration.ofMillis((long) (result.getFormat().getDuration() / 1000));
	}

	/**
	 * Snip a video file between the start and end time, and save it to an output file.
	 *
	 * @param video     input video path to work with
	 * @param startTime start time (in Duration format, e.g. Duration.ofSeconds(seconds);)
	 * @param endTime   start time (in TimeStamp format, e.g. Duration.ofSeconds(seconds);)
	 * @param output    output video path to save the snipped clip to
	 */
	public void snipVideo(Path video, Duration startTime, Duration endTime, Path output) {
		getFFMpeg()
				.addInput(UrlInput.fromPath(video)
						.setPosition(startTime.toMillis()) // Start position
						.addArguments("to", "" + endTime.toMillis()) // End position
						.addArguments("ac", "1") // Audio channels
						.addArguments("ar", "44100") // Audio frequency
						.addArguments("vf", "scale=640x480,setsar=1:1,fps=fps=30") // Resize and set FPS
				)
				.setOverwriteOutput(true)
				.addOutput(UrlOutput.toPath(output))
				.execute();
	}

	/**
	 * Copies a video and encodes it in the proper format without changes.
	 *
	 * @param video  input video path to work with
	 * @param output output video path to save the clip to
	 */
	public void encodeVideo(Path video, Path output) {
		getFFMpeg()
				.addInput(UrlInput.fromPath(video)
						.addArguments("ac", "1") // Audio channels
						.addArguments("ar", "44100") // Audio frequency
						.addArguments("vf", "scale=640x480,setsar=1:1,fps=fps=30") // Resize and set FPS
				)
				.setOverwriteOutput(true)
				.addOutput(UrlOutput.toPath(output))
				.execute();
	}

	/**
	 * Concatenate video clips
	 *
	 * @param clips  the clips to concatenate
	 * @param output output video path
	 */
	public void concatenateVideo(Collection<Path> clips, Path output) {
		FFmpeg ffmpeg = getFFMpeg();
		int found = 0;
		for (Path currentClip : clips) {
			if(Files.isRegularFile(currentClip)) {
				ffmpeg.addInput(UrlInput.fromPath(currentClip));
				found++;
			}
		}

		ffmpeg.setComplexFilter();
		ffmpeg.setOverwriteOutput(true);
		ffmpeg.execute();



		try {
			File export = new File(out);

			if (export.exists())
				export.delete();

			String command1 = ffmpeg;

			for (int i = 0; i < count; i++) {
				if (new File(TEMP + "video" + i + ".mp4").exists()) {
					command1 = command1.concat(" -i " + TEMP + "video" + i + ".mp4");
				}
			}
			command1 = command1.concat(" -filter_complex \"");

			int realcount = 0;
			for (int i = 0; i < count; i++) {
				if (new File(TEMP + "video" + i + ".mp4").exists()) {
					realcount += 1;
				}
			}



			for (int i = 0; i < realcount; i++) {
				command1 = command1.concat("[" + i + ":v:0][" + i + ":a:0]");
			}

			//realcount +=1;
			command1 = command1.concat("concat=n=" + realcount + ":v=1:a=1[outv][outa]\" -map \"[outv]\" -map \"[outa]\" -y " + out);
			System.out.println(command1);

			CommandLine cmdLine = CommandLine.parse(command1);
			DefaultExecutor executor = new DefaultExecutor();
			int exitValue = executor.execute(cmdLine);

			//cmdLine = CommandLine.parse(command2);
			//executor = new DefaultExecutor();
			//exitValue = executor.execute(cmdLine);

			//temp.delete();

		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
}
