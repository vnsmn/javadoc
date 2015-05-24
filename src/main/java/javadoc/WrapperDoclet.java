package javadoc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import javadoc.Util.TAG;

import com.sun.tools.doclets.standard.Standard;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;

public final class WrapperDoclet extends Doclet {
	static String PATHS = WrapperDoclet.class.getName() + ".paths";

	public static boolean start(RootDoc root) {
		System.getProperties().put(RootDoc.class.getName(), root);
		try {
			Util.deleteJavaDir();
			Map<String, String> paths = new HashMap<String, String>();
			for (ClassDoc cd : root.classes()) {
				String s = Util.javaToHtml(cd);
				if (!StringUtils.isBlank(s)) {
					paths.put(cd.position().file().getAbsolutePath(), s);
				}
			}
			System.getProperties().put(PATHS, paths);
			println("START");
			return Standard.start(root);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			String sb = Util.getCustomTagHead(TAG.JAVADOC_DEBUG);
			if ("true".equals(sb)) {
				debugInfo();
			}
		}
	}

	public static LanguageVersion languageVersion() {
		return Standard.languageVersion();
	}

	public static int optionLength(String option) {
		return Standard.optionLength(option);
	}

	public static boolean validOptions(String[][] options,
			DocErrorReporter reporter) {
		return Standard.validOptions(options, reporter);
	}

	static RootDoc getRootDoc() {
		return (RootDoc) System.getProperties().get(RootDoc.class.getName());
	}

	static Map<String, String> getPaths() {
		return (Map<String, String>) System.getProperties().get(PATHS);
	}

	static void debugInfo() {
		RootDoc root = getRootDoc();
		List<Tag> tags = new ArrayList<Tag>();
		List<Tag> inlineTags = new ArrayList<Tag>();
		println("\n***** DEBUG BEGIN *****\n");

		println("********** PRINT COMMAND LINES **********");
		for (String[] opts : root.options()) {
			for (String opt : opts) {
				System.out.println(opt);
			}
		}

		for (PackageDoc d : root.specifiedPackages()) {
			for (Tag t : d.tags()) {
				tags.add(t);
			}
			for (Tag t : d.inlineTags()) {
				inlineTags.add(t);
			}
		}

		println("\n\n\n\n\n========== CLASSES ==========");

		for (ClassDoc cd : root.classes()) {
			printClass(cd, tags, inlineTags);
		}

		println("\n\n\n\n\n********** TAGS **********");

		for (Tag t : tags) {
			if (t.name().equals("Text")) {
				continue;
			}
			println("\n********** %s **********", t.name());
			println("\t%s", t.text());
			// println("\t%s", t.kind());
			println("\t%s", t.position().file().getAbsolutePath());
			println("\tline = %s, column = %s", t.position().line(), t
					.position().column());
		}

		println("\n\n\n\n\n++++++++++ INLINE TAGS ++++++++++");

		for (Tag t : inlineTags) {
			if (t.name().equals("Text")) {
				continue;
			}
			println("\n++++++++++ %s ++++++++++", t.name());
			println("\t%s", t.text());
			// println("\t%s", t.kind());
			println("\t%s", t.position().file().getAbsolutePath());
			println("\tline = %s, column = %s", t.position().line(), t
					.position().column());
		}

		println("\n\n\n\n\n~~~~~~~~~~ PATHS ~~~~~~~~~~");

		Map<String, String> paths = getPaths();

		for (Entry<String, String> ent : paths.entrySet()) {
			println("\n~~~~~~~~~~ %s ~~~~~~~~~~", ent.getKey());
			println("\t%s", ent.getValue());
		}

		println("\n***** DEBUG END *****\n");
	}

	static void println(String format, Object... args) {
		System.out.println(String.format(format, args));
	}

	static void println(Object obj) {
		System.out.println(obj);
	}

	static void printClass(ClassDoc cd, List<Tag> tags, List<Tag> inlineTags) {
		println("\n========== %s ==========", cd.toString());
		println(cd.position().file().getAbsolutePath());
		for (Tag t : cd.tags()) {
			tags.add(t);
		}
		for (Tag t : cd.inlineTags()) {
			inlineTags.add(t);
		}
		for (MethodDoc m : cd.methods()) {
			println("\n\t---------- %s%s ----------", m.name(),
					m.flatSignature());
			println("\tline = %s, column = %s", m.position().line(), m
					.position().column());
			println("\t%s", m.signature());
			for (Tag t : m.tags()) {
				tags.add(t);
			}
			for (Tag t : m.inlineTags()) {
				inlineTags.add(t);
			}
		}
	}
}
