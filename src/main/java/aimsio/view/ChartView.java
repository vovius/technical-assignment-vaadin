package aimsio.view;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.AxisType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;

import java.util.Collections;

public class ChartView extends VerticalLayout {
    public ChartView() {
        ComboBox<String> assetUNComboBox = new ComboBox<>("AssetUN:", Collections.singleton("3112"));
        this.addComponent(assetUNComboBox);

        Chart timeline = new Chart();
        timeline.setSizeFull();

        Configuration configuration = timeline.getConfiguration();
        configuration.getTitle().setText("# of Signals over Time");
        configuration.getxAxis().setType(AxisType.DATETIME);
        configuration.getyAxis().setMin(0);
        configuration.getyAxis().setTitle("# of Signals");
        configuration.getxAxis().getLabels().setEnabled(false);
        configuration.getLegend().setEnabled(true);
        this.addComponent(timeline);
    }
}
