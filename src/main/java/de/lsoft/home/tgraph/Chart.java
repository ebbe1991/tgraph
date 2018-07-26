package de.lsoft.home.tgraph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class Chart extends ApplicationFrame {

    public Chart(final String title, TreeMap<LocalDateTime, String> map, LocalDate von, LocalDate bis) {
        super(title);
        final XYDataset dataset = createDataset(map, von, bis);
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        // chartPanel.setMouseZoomable(true, false);
        setContentPane(chartPanel);
    }

    @Override
    public void windowClosing(WindowEvent event) {
        this.dispose();
    }

    private XYDataset createDataset(TreeMap<LocalDateTime, String> map, LocalDate von, LocalDate bis) {

        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        for (String error : new HashSet<>(map.values())) {
            Map<LocalDate, Integer> map2 = new LinkedHashMap<>();
            Map<LocalDate, Integer> map3 = new LinkedHashMap<>();
            for (Map.Entry<LocalDateTime, String> entry : map.entrySet()) {

                if (entry.getValue().equals(error)) {
                    LocalDate localDate = entry.getKey().toLocalDate();
                    Integer integer = map2.getOrDefault(localDate, 0);
                    integer = integer + 1;
                    map2.put(localDate, integer);
                }
            }
            LocalDate datum = von == null ? map.firstKey().toLocalDate() : von;
            LocalDate max = bis == null ? LocalDate.now() : bis;
            while (!datum.equals(max.plusDays(1))) {
                map3.put(datum, map2.getOrDefault(datum, 0));
                datum = datum.plusDays(1);
            }

            final TimeSeries series = new TimeSeries("Fehlerrate " + error);
            for (Map.Entry<LocalDate, Integer> entry : map3.entrySet()) {
                LocalDate key = entry.getKey();
                series.add(new Day(key.getDayOfMonth(), key.getMonthValue(), key.getYear()), entry.getValue());
            }
            timeSeriesCollection.addSeries(series);
        }

        return timeSeriesCollection;
    }

    private JFreeChart createChart(final XYDataset dataset) {
        return ChartFactory.createTimeSeriesChart(
                "Fehler SP Smart",
                "Datum",
                "Fehler",
                dataset,
                false,
                true,
                true);
    }
}   