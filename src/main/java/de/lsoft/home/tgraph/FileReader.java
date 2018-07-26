package de.lsoft.home.tgraph;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class FileReader {

    public TreeMap<LocalDateTime, String> readFile(String filename, LocalDate von, LocalDate bis, String... errors) throws IOException {
        TreeMap<LocalDateTime, String> map = new TreeMap<>();

        try (Stream<String> lines = Files.lines(Paths.get(filename), Charset.defaultCharset())) {
            lines.forEachOrdered(line -> {
                map.putAll(process(line, von, bis, errors));
            });
        }
        return map;
    }

    private Map<LocalDateTime, String> process(String line, LocalDate von, LocalDate bis, String... errors) {
        HashMap<LocalDateTime, String> map = new HashMap<>();
        for (String error : errors) {
            if (line.contains("(" + error + ")")) {
                von = von == null ? LocalDate.MIN : von;
                bis = bis == null ? LocalDate.MAX.minusDays(1) : bis;
                try {
                    String dateString = line.split("\"")[1].trim();
                    LocalDateTime date = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
                    if (date.isAfter(von.atStartOfDay()) && date.isBefore(bis.atStartOfDay().plusDays(1)))
                        map.put(date, error);
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }
}
