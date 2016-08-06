package org.inventivetalent.protocol.versiongenerator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class Main {

	public static void main(String... args) throws IOException {
		Document document = Jsoup.connect("http://wiki.vg/Protocol_version_numbers").get();

		Element versionTable = document.select("table.wikitable").first();
		Element tableBody = versionTable.select("tbody").first();

		Map<String, String> versionMap = new HashMap<>();

		String latestNumber = "-1";
		boolean first = true;
		for (Element element : tableBody.select("tr")) {
			if (first) {
				first = false;
				continue;
			}

			Elements columns = element.select("td");
			String nameString = columns.get(0).text();
			String versionString = columns.size() >= 2 ? columns.get(1).text() : latestNumber;
			latestNumber = versionString;

			System.out.println(nameString + " > " + versionString);

			// Replace previous entries, so we end up with the earliest version string
			versionMap.put(versionString, nameString);
		}

		System.out.println();

		List<String> enums = new ArrayList<>();

		List<String> keys = new ArrayList<>(versionMap.keySet());
		Collections.sort(keys, (o1, o2) -> Integer.valueOf(o1).compareTo(Integer.valueOf(o2)));
		for (String number : keys) {
			String s = "v" + versionMap.get(number).replace(".", "_").replace("-", "_").toUpperCase() + "(" + number + "),";
			System.out.println(s);
			enums.add(s);
		}

		File file = new File("GENERATED_ENUMS");
		if (!file.exists()) { file.createNewFile(); }
		try (Writer writer = new FileWriter(file)) {
			for (String e : enums) {
				writer.write(e + "\n");
			}
		}
	}

}
