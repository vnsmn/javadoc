package javadoc;

import com.sun.tools.doclets.Taglet;
import com.sun.javadoc.*;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import javadoc.Util.TAG;

/**
 * <p>
 * SourceLink class.
 * </p>
 *
 * @author vns
 */
public class JavaLinkTaglet implements com.sun.tools.doclets.Taglet {
	private static final String NAME = "java";
	private static final String HEADER = "";

	public String getName() {
		return NAME;
	}

	public boolean inField() {
		return true;
	}

	public boolean inConstructor() {
		return true;
	}

	public boolean inMethod() {
		return true;
	}

	public boolean inOverview() {
		return true;
	}

	public boolean inPackage() {
		return true;
	}

	public boolean inType() {
		return true;
	}

	public boolean isInlineTag() {
		return true;
	}

	public static void register(Map tagletMap) {
		JavaLinkTaglet tag = new JavaLinkTaglet();
		Taglet t = (Taglet) tagletMap.get(tag.getName());
		if (t != null) {
			tagletMap.remove(tag.getName());
		}
		tagletMap.put(tag.getName(), tag);
	}

	public String toString(Tag tag) {
		String path;
		String text = tag.text();
		String title = tag.text();

		String methodNumber = "";
		String number = "";
		if (tag.text() == null || tag.text().trim().isEmpty()) {
			path = tag.position().file().getAbsolutePath();
		} else {
			String[] ss = tag.text().split("[#]");
			path = Util.getClassPath(ss[0]);
			String methodSignature = ss.length < 2 ? null : ss[1];
			if (!StringUtils.isBlank(methodSignature)) {
				int n = Util.getMethodLine(ss[0], ss[1]);
				if (n != -1) {
					methodNumber = "#i" + n;
					number = "#" + n;
				}
			}
			String[] fns = ss[0].split("[.]");
			text = fns[fns.length - 1];
			title = ss[0];
			if (!StringUtils.isBlank(methodSignature)) {
				title += "." + methodSignature;
			}
		}

		if (StringUtils.isBlank(path)) {
			return "<B>" + HEADER + "</B>";
		} else {
			String href = Util.toRelativizePath(tag.position().file()
					.getAbsolutePath(), path);
			if (StringUtils.isBlank(href)) {
				text = "?" + text;
			}
			return	"<B>"
					+ HEADER
					+ "</B>"
					+ String.format("<a href=\"%s\" title=\"%s\">[%s]</a>", "" + href
							+ methodNumber, title, text + number);
		}
	}

	public String toString(Tag[] tags) {
		String result = "\n<DT><B>" + HEADER + "</B><DD>";
		result += "<table cellpadding=2 cellspacing=0><tr><td bgcolor=\"yellow\">";
		for (int i = 0; i < tags.length; i++) {
			if (i > 0) {
				result += ", ";
			}
			result += tags[i].text();
		}
		return result + "</td></tr></table></DD>\n";
	}
}
