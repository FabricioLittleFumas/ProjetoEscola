package br.com.noticiarioOficial;

import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link NoticiarioOficialApplication}.
 */
@Generated
public class NoticiarioOficialApplication__BeanDefinitions {
  /**
   * Get the bean definition for 'noticiarioOficialApplication'.
   */
  public static BeanDefinition getNoticiarioOficialApplicationBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(NoticiarioOficialApplication.class);
    beanDefinition.setInstanceSupplier(NoticiarioOficialApplication::new);
    return beanDefinition;
  }
}
