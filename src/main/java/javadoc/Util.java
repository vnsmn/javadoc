package javadoc;

import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.util.DocFinder;
import com.sun.tools.internal.ws.processor.util.DirectoryUtil;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

public class Util {
	public enum TAG {
		JAVADOC_DEBUG("javadoc.debug"), JAVADOC_NUMBER("javadoc.number");
		TAG(String name) {
			this.name = name;
		}

		private String name;
	}

	public static String getClassPath(String className) {
		ClassDoc cd = WrapperDoclet.getRootDoc().classNamed(className);
		return cd == null ? null : cd.position().file().getAbsolutePath();
	}

	public static ClassDoc getClassDoc(String className) {
		return WrapperDoclet.getRootDoc().classNamed(className);
	}

	public static int getMethodLine(String className, String method) {		
		ClassDoc cd = WrapperDoclet.getRootDoc().classNamed(className);
		for (MethodDoc m : cd.methods()) {
			if ((m.name() + m.flatSignature()).equals(method)) {
				return m.position().line();
			}
		}
		return -1;
	}

	public static String getCustomTagHead(TAG tag) {
		for (String[] opts : WrapperDoclet.getRootDoc().options()) {
			if (opts[0].equals("-tag")) {
				String[] ss = opts[1].split("[:]");
				if (ss[0].equals(tag.name)) {
					return ss[2];
				}
			}
		}
		return null;
	}

	public static String[] getOption(String name) {
		for (String[] opts : WrapperDoclet.getRootDoc().options()) {
			if (opts[0].equals(name)) {
				return opts;
			}
		}
		return null;
	}

	public static void deleteJavaDir() {
		String dir = getOption("-d")[1];
		File fdir = new File(dir, "src.java");
		try {
			if (fdir.exists()) {
				FileUtils.deleteDirectory(fdir);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String javaToHtml(ClassDoc cd) throws IOException {
		if (!cd.position().file().getName().equals(cd.name() + ".java")) {
			return null;
		}

		List<String> sl = FileUtils.readLines(cd.position().file(), "UTF-8");
		List<String> ds = new ArrayList<String>();

		ds.add("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>");
		
		ds.add("<pre>");
		String sb = Util.getCustomTagHead(TAG.JAVADOC_NUMBER);
		String code = "";
		char[] patterns = new char[("" + sl.size()).length()];
		Arrays.fill(patterns, '0');
		int i = 0;
		DecimalFormat decimalFormatter = new DecimalFormat(
				String.copyValueOf(patterns));
		String defColor = "black";
		for (String s : sl) {			
			if ("true".equals(sb)) {
				code = "" + decimalFormatter.format(++i)
						+ ".&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
						+ StringEscapeUtils.escapeHtml4(s);// + "<br>");
			} else {
				code = StringEscapeUtils.escapeHtml4(s) + "<br>";
			}
			if (s.matches("^([0-9]+[.]){0,1}([ ]|\t)*[*][/]([ ]*|\n*|$)")) {
				defColor = "black";
				code = code.replace("*/", ""); //compile-time error finish
			}
			if (code.contains(" //error") || code.contains(" ///error")) {
				code = "<font color=\"red\">" + code + "</font>";
			} else if (code.contains(" //ok") || code.contains(" ///ok")) {
				code = "<font color=\"green\">" + code + "</font>";
			} else if (code.contains(" //warning") || code.contains(" ///warning")) {
				code = "<font color=\"GoldenRod\">" + code + "</font>";
			} else {
				code = String.format("<font color=\"%s\">%s</font>", defColor, code);
			}
			if (code.contains(" ///ok")
					|| code.contains(" ///error") || code.contains(" ///warning")) {
				code = "<b>" + code + "</b>";
				code = code.replace(" ///error", " ");
				code = code.replace(" ///ok", " ");
				code = code.replace(" ///warning", " ");
			} else if (code.contains(" //ok") 
					|| code.contains(" //error") || code.contains(" //warning")) {
				code = code.replace(" //error", " ");
				code = code.replace(" //ok", " ");
				code = code.replace(" //warning", " ");
			} 			
			if (s.matches("^([0-9]+[.]){0,1}(|[ ]|\t)*[/][*]error([ ]*|\n*|$)")) {
				defColor = "red";
				code = code.replace("/*error", "<b>//compile-time error</b>");
			}
			code = String.format("<a href=\"#\" id=\"i%s\" name=\"n%s\"></a>",
					i, i) + code;
			ds.add(code);
		}
		ds.add("</pre>");
		ds.add("</body></html>");
		String dir = getOption("-d")[1];
		File fdir = new File(dir, "src.java");
		if (!fdir.exists()) {
			fdir.mkdirs();
		}
		File f = new File(fdir, cd.toString() + ".html");
		if (!f.exists()) {
			f.createNewFile();
		}
		FileUtils.writeLines(f, ds);
		return f.getAbsolutePath();
	}

	public static String toRelativizePath(String current, String java) {
		String doc = WrapperDoclet.getPaths().get(java);
		if (doc == null) {
			return null;
		}
		Path pathBaseDoc = Paths.get(getOption("-d")[1]);
		Path pathBaseJava = Paths.get(getOption("-sourcepath")[1]);
		Path pathJava = Paths.get(current);
		Path pathDoc = Paths.get(doc);
		Path pathRelativeJava = pathBaseJava.relativize(pathJava);
		Path pathRelativeDoc = pathBaseDoc.relativize(pathDoc);
		int skip = pathRelativeJava.toString()
				.split("[" + File.separator + "]").length - 1;
		String skipPath = "";
		while (skip > 0) {
			skipPath += "../";
			skip--;
		}
		return skipPath + pathRelativeDoc;
	}
}
