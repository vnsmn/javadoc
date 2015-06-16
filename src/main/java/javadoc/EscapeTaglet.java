package javadoc;

import com.sun.tools.doclets.Taglet;
import com.sun.javadoc.*;

import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import javadoc.Util.TAG;

/**
 * <p>
 * SourceLink class.
 * </p>
 *
 * @author vns
 */
public class EscapeTaglet implements com.sun.tools.doclets.Taglet {
	private static final String NAME = "escape";
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
		EscapeTaglet tag = new EscapeTaglet();
		Taglet t = (Taglet) tagletMap.get(tag.getName());
		if (t != null) {
			tagletMap.remove(tag.getName());
		}
		tagletMap.put(tag.getName(), tag);
	}

	public String toString(Tag tag) {
		return StringEscapeUtils.escapeHtml4(tag.text());
	}

	public String toString(Tag[] tags) {
		String result = "";
		for (int i = 0; i < tags.length; i++) {
			result += StringEscapeUtils.escapeHtml4(tags[i].text());
		}
		return result;
	}
}
