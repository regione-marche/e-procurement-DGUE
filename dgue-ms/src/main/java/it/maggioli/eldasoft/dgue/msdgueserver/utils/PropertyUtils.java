package it.maggioli.eldasoft.dgue.msdgueserver.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDDocument;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 06, 2020
 */
public final class PropertyUtils {

    /**
     *
     * @param bean
     * @param propertyName
     * @return
     * @throws IntrospectionException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object getProperty(Object bean, String propertyName) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        final BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
                     
        final Optional<PropertyDescriptor> propertyDescriptorOptional = Arrays
                .stream(beanInfo.getPropertyDescriptors())
                .filter(pd -> pd.getName().equals(propertyName)).findFirst();
        if (propertyDescriptorOptional.isPresent()) {
            return propertyDescriptorOptional.get().getReadMethod().invoke(bean);
        } else {
            return null;
        }
    }

	public static void setProperty(ESPDCriterion espdCriterion, String espdFieldName, Object value) {
		// TODO Auto-generated method stub
		
	}

	public static void setSimpleProperty(ESPDDocument espdDocument, String espdDocumentField,
			ESPDCriterion buildEspdCriterion) {
		// TODO Auto-generated method stub
		
	}
}
