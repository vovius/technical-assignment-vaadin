package com.aimsio.data;

import com.aimsio.model.SignalCount;
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Stream;

public class ChartDataProvider extends AbstractBackEndDataProvider<SignalCount, String> {

    private final static Logger LOG = LoggerFactory.getLogger(ChartDataProvider.class);
    private final DataFetcher dataFetcher;

    public ChartDataProvider(DataFetcher dataFetcher) {
        this.dataFetcher = dataFetcher;
    }


    @Override
    protected Stream<SignalCount> fetchFromBackEnd(Query<SignalCount, String> query) {
        List<SignalCount> result = dataFetcher.fetchData();
        LOG.debug("fetchFromBackend: {} records fetched", result.size());
        return result.stream();
    }

    @Override
    protected int sizeInBackEnd(Query<SignalCount, String> query) {
        return dataFetcher.fetchData().size();
    }

    @Override
    public Object getId(SignalCount item) {
        return item.getEntryDate();
    }
}
