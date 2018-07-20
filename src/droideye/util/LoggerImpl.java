package droideye.util;

import com.briup.util.Logger;

import org.apache.log4j.PropertyConfigurator;

import java.util.Properties;

public class LoggerImpl implements Logger {
    private String logPropFilePath;
    org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(LoggerImpl.class);

    @Override
    public void debug(String s) {
        PropertyConfigurator.configure(logPropFilePath);

        logger.debug(s);
    }

    @Override
    public void info(String s) {
        PropertyConfigurator.configure(logPropFilePath);

        logger.info(s);
    }

    @Override
    public void warn(String s) {
        PropertyConfigurator.configure(logPropFilePath);

        logger.warn(s);
    }

    @Override
    public void error(String s) {
        PropertyConfigurator.configure(logPropFilePath);

        logger.error(s);
    }

    @Override
    public void fatal(String s) {
        PropertyConfigurator.configure(logPropFilePath);

        logger.fatal(s);
    }

    @Override
    public void init(Properties properties) {
        logPropFilePath = properties.getProperty("log-pro");
    }
}
