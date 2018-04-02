package com.aimsio.data;

import com.aimsio.model.SignalCount;
import com.aimsio.util.WhereBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

public class DataFetcher {

    private final static Logger LOG = LoggerFactory.getLogger(DataFetcher.class);

    private final static String PROPERTIES_FILE = "application.properties";

    private Connection connection;
    private String assetUNFilter = "";
    private String statusFilter = "";

    public DataFetcher() {
        try {
            init();
        } catch (Exception e) {
            LOG.error("Error initializing DataFetcher", e);
        }
    }

    public List<SignalCount> fetchData() {
        String where = "";
        if (!assetUNFilter.isEmpty() || !statusFilter.isEmpty()) {
             where = new WhereBuilder.Builder()
                        .add(!assetUNFilter.isEmpty() ? "AssetUN = '" + assetUNFilter + "'" : "")
                        .add(!statusFilter.isEmpty() ? "status = '" + statusFilter + "'" : "")
                        .build();
             LOG.debug("fetchData: where={}", where);
        }
        String query = MessageFormat.format(
                  "select entry_date, count(*) cnt " +
                          "from assets.`signal` " +
                          "{0} " +
                          "group by entry_date", where);

        Optional<List<SignalCount>> result = getResultSet(query, this::mapDataResults);
        return result.orElseGet(Collections::emptyList);
    }

    public List<String> fetchUNs() {
        String query = "select distinct AssetUN from assets.`signal` order by 1";
        Optional<List<String>> result = getResultSet(query, this::mapUNResults);
        return result.orElseGet(Collections::emptyList);
    }

    public void setAssetUNFilter(String assetUNFilter) {
        this.assetUNFilter = Optional.ofNullable(assetUNFilter).orElse("");
    }

    public void setStatusFilter(String statusFilter) {
        this.statusFilter = Optional.ofNullable(statusFilter).orElse("");
    }

    private <T> Optional<T> getResultSet(String query, Function<ResultSet, T> mapper) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            return Optional.of(mapper.apply(resultSet));
        } catch (SQLException e) {
            LOG.error("Error executing query {}", query, e);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    LOG.error("Error closing statement {}", statement);
                }
            }
        }

        return Optional.empty();
    }

    private void init() throws Exception {
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
        if (inputStream != null) {
            properties.load(inputStream);
        } else {
            throw new FileNotFoundException(MessageFormat.format("property file {0} not found in the classpath", PROPERTIES_FILE));
        }

        initConnection(properties);
    }

    private void initConnection(Properties properties) throws SQLException {
        String host = properties.getProperty("db.host");
        String user = properties.getProperty("db.user");
        String pass = properties.getProperty("db.pass");
        String db = properties.getProperty("db.database");
        String connectionString = MessageFormat.format("jdbc:mysql://{0}/{1}?user={2}&password={3}&autoReconnect=true&useSSL=false", host, db, user, pass);
        connection = DriverManager.getConnection(connectionString);
    }

    private List<SignalCount> mapDataResults(ResultSet resultSet) {
        List<SignalCount> results = new LinkedList<>();
        try {
            while (resultSet.next()) {
                results.add(new SignalCount(resultSet.getInt("cnt"), resultSet.getString("entry_date")));
            }
        } catch (SQLException e) {
            LOG.error("Error mapping resultSet in mapDataResults", e);
        }

        return results;
    }

    private List<String> mapUNResults(ResultSet resultSet) {
        List<String> results = new LinkedList<>();
        try {
            while (resultSet.next()) {
                results.add(resultSet.getString("AssetUN"));
            }
        } catch (SQLException e) {
            LOG.error("Error mapping resultSet in mapUNResults", e);
        }

        return results;
    }
}
