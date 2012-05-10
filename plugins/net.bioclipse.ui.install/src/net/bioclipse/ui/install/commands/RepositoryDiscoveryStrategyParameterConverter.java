package net.bioclipse.ui.install.commands;

import java.lang.reflect.Constructor;

import net.bioclipse.ui.install.discovery.BasicRepositoryDiscoveryStrategy;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.ParameterValueConversionException;


public class RepositoryDiscoveryStrategyParameterConverter extends
                AbstractParameterValueConverter {

    public RepositoryDiscoveryStrategyParameterConverter() {

        // TODO Auto-generated constructor stub
    }

    @Override
    public Object convertToObject( String parameterValue )
                                                          throws ParameterValueConversionException {

        try {
            if ( parameterValue == null )
                return null;
        Class<?> param = Class.forName( parameterValue );
            if ( BasicRepositoryDiscoveryStrategy.class
                            .isAssignableFrom( param ) ) {
                Class<BasicRepositoryDiscoveryStrategy> strategy =
                                (Class<BasicRepositoryDiscoveryStrategy>) param;
                Constructor<BasicRepositoryDiscoveryStrategy> constructor =
                                strategy.getConstructor();
            return constructor.newInstance();
        }
        } catch ( Exception e ) {
            throw new ParameterValueConversionException(
                                                         "Faild to get class from parameter",
                                                         e );
        }
        return null;
    }

    @Override
    public String convertToString( Object parameterValue )
                                                          throws ParameterValueConversionException {

        if ( parameterValue instanceof BasicRepositoryDiscoveryStrategy ) {
            return parameterValue.getClass().getName();
        }
        return null;
    }

}
