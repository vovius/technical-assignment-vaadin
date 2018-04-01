package com.aimsio.view;

import com.aimsio.data.ChartDataProvider;
import com.aimsio.data.DataFetcher;
import com.aimsio.model.SignalCount;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.DataProviderSeries;
import com.vaadin.data.HasValue;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import static java.util.Arrays.asList;

public class ChartView extends VerticalLayout {

    private final DataFetcher dataFetcher = new DataFetcher();
    private final ChartDataProvider signalDataProvider = new ChartDataProvider(dataFetcher);
    private final Chart timeline;

    public ChartView() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponent(initUNCombo());
        horizontalLayout.addComponent(initStatusCombo());
        this.addComponent(horizontalLayout);

        timeline = initChart();
        this.addComponent(timeline);
    }

    private ComboBox<String> initUNCombo() {
        ComboBox<String> assetUNComboBox = new ComboBox<>("AssetUN:", dataFetcher.fetchUNs());
        assetUNComboBox.addValueChangeListener(this::comboUNChangeListener);
        return assetUNComboBox;
    }

    private ComboBox<String> initStatusCombo() {
        ComboBox<String> statusComboBox = new ComboBox<>("Status:", asList("Active", "Engaged", "Override"));
        statusComboBox.addValueChangeListener(this::statusChangeListener);
        return statusComboBox;
    }

    private Chart initChart() {
        Chart timeline = new Chart();
        timeline.setSizeFull();

        Configuration configuration = timeline.getConfiguration();
        configuration.getTitle().setText("# of Signals over Time");

        configuration.getxAxis().setType(AxisType.DATETIME);
        configuration.getxAxis().getLabels().setEnabled(false);

        configuration.getyAxis().setMin(0);
        configuration.getyAxis().setTitle("# of Signals");

        configuration.getLegend().setEnabled(true);

        configuration.getChart().setType(ChartType.LINE);

        DataProviderSeries<SignalCount> ds = createChartDS();
        configuration.setSeries(ds);

        return timeline;
    }

    private DataProviderSeries<SignalCount> createChartDS() {
        DataProviderSeries<SignalCount> dataProviderSeries = new DataProviderSeries<>(signalDataProvider);
        dataProviderSeries.setX(SignalCount::getEntryDate);
        dataProviderSeries.setY(SignalCount::getStatusCount);
        return dataProviderSeries;
    }

    private void comboUNChangeListener(HasValue.ValueChangeEvent<String> value) {
        dataFetcher.setAssetUNFilter(value.getValue());
        timeline.drawChart();
    }

    private void statusChangeListener(HasValue.ValueChangeEvent<String> value) {
        dataFetcher.setStatusFilter(value.getValue());
        timeline.drawChart();
    }
}
