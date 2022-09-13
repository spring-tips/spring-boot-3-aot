package bootiful.aot;

import java.util.Map;

public abstract class Utils {

	public static String toString(Map<?, ?> map) {
		var builder = new StringBuilder();
		builder.append(System.lineSeparator());
		builder.append(System.lineSeparator());
		for (var e : map.entrySet()) {
			builder.append("\t");
			builder.append(string(e.getKey()));
			builder.append(" = ");
			builder.append(string(e.getValue()));
			builder.append(System.lineSeparator());
		}
		return builder.toString();
	}

	private static String string(Object o) {
		if (o == null) {
			return "";
		}
		if (o instanceof String s) {
			return s;
		}

		return o.toString();

	}

}
