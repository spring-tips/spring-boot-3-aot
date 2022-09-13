package bootiful.aot.acaotgen;

import bootiful.aot.DemoApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aot.generate.ClassNameGenerator;
import org.springframework.aot.generate.DefaultGenerationContext;
import org.springframework.aot.generate.FileSystemGeneratedFiles;
import org.springframework.aot.generate.GeneratedFiles;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.aot.ApplicationContextAotGenerator;
import org.springframework.javapoet.ClassName;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.Arrays;

@Slf4j
class ApplicationContextAotGeneratorTest {

	private final File root = new File(new File(new File(System.getenv("HOME")), "Desktop"), "aot");

	private final Path sourceOutput = new File(this.root, "source").toPath();

	private final Path resourceOutput = new File(this.root, "resources").toPath();

	private final Path classOutput = new File(this.root, "classes").toPath();

	ApplicationContextAotGeneratorTest() {
		Assert.state(!this.root.exists() || this.root.delete() || FileSystemUtils.deleteRecursively(this.root),
				"the root directory exists but shouldn't");
		for (var path : new Path[] { this.classOutput, this.sourceOutput, this.resourceOutput }) {
			var file = path.toFile();
			Assert.state(file.exists() || file.mkdirs(),
					"the directory " + file.getAbsolutePath() + " does not exist.");
		}

		log.info("all three paths exist.");
	}

	private Path getRoot(GeneratedFiles.Kind kind) {
		return switch (kind) {
			case SOURCE -> this.sourceOutput;
			case RESOURCE -> this.resourceOutput;
			case CLASS -> this.classOutput;
		};
	}

	@Test
	void representApplicationContext() throws Exception {
		var application = DemoApplication.class;
		var ctx = new AnnotationConfigServletWebServerApplicationContext();
		ctx.register(application);
		var generatedFiles = new FileSystemGeneratedFiles(this::getRoot);
		var generationContext = new DefaultGenerationContext(new ClassNameGenerator(application), generatedFiles);
		var generator = new ApplicationContextAotGenerator();
		log.info("about to try...");
		ClassName generatedInitializerClassName = generator.processAheadOfTime(ctx, generationContext);
		generationContext.writeGeneratedContent();
		var generatedClassNamePath = String.join("/",
				Arrays.asList(generatedInitializerClassName.canonicalName().split("\\."))) + ".java";
		var file = new File(this.sourceOutput.toFile(), generatedClassNamePath);
		log.info("the path is " + file.getAbsolutePath());
		Assertions.assertTrue(file.exists());
		log.info("the code is " + FileCopyUtils.copyToString(new FileReader(file)));

	}

}
